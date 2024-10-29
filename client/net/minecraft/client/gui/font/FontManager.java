package net.minecraft.client.gui.font;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.DependencySorter;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class FontManager implements PreparableReloadListener, AutoCloseable {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final String FONTS_PATH = "fonts.json";
   public static final ResourceLocation MISSING_FONT = ResourceLocation.withDefaultNamespace("missing");
   private static final FileToIdConverter FONT_DEFINITIONS = FileToIdConverter.json("font");
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private final FontSet missingFontSet;
   private final List<GlyphProvider> providersToClose = new ArrayList();
   private final Map<ResourceLocation, FontSet> fontSets = new HashMap();
   private final TextureManager textureManager;
   @Nullable
   private volatile FontSet lastFontSetCache;

   public FontManager(TextureManager var1) {
      super();
      this.textureManager = var1;
      this.missingFontSet = (FontSet)Util.make(new FontSet(var1, MISSING_FONT), (var0) -> {
         var0.reload(List.of(createFallbackProvider()), Set.of());
      });
   }

   private static GlyphProvider.Conditional createFallbackProvider() {
      return new GlyphProvider.Conditional(new AllMissingGlyphProvider(), FontOption.Filter.ALWAYS_PASS);
   }

   public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, Executor var3, Executor var4) {
      CompletableFuture var10000 = this.prepare(var2, var3);
      Objects.requireNonNull(var1);
      return var10000.thenCompose(var1::wait).thenAcceptAsync((var1x) -> {
         this.apply(var1x, Profiler.get());
      }, var4);
   }

   private CompletableFuture<Preparation> prepare(ResourceManager var1, Executor var2) {
      ArrayList var3 = new ArrayList();
      Iterator var4 = FONT_DEFINITIONS.listMatchingResourceStacks(var1).entrySet().iterator();

      while(var4.hasNext()) {
         Map.Entry var5 = (Map.Entry)var4.next();
         ResourceLocation var6 = FONT_DEFINITIONS.fileToId((ResourceLocation)var5.getKey());
         var3.add(CompletableFuture.supplyAsync(() -> {
            List var5x = loadResourceStack((List)var5.getValue(), var6);
            UnresolvedBuilderBundle var6x = new UnresolvedBuilderBundle(var6);
            Iterator var7 = var5x.iterator();

            while(var7.hasNext()) {
               Pair var8 = (Pair)var7.next();
               BuilderId var9 = (BuilderId)var8.getFirst();
               FontOption.Filter var10 = ((GlyphProviderDefinition.Conditional)var8.getSecond()).filter();
               ((GlyphProviderDefinition.Conditional)var8.getSecond()).definition().unpack().ifLeft((var6xx) -> {
                  CompletableFuture var7 = this.safeLoad(var9, var6xx, var1, var2);
                  var6x.add(var9, var10, var7);
               }).ifRight((var3) -> {
                  var6x.add(var9, var10, var3);
               });
            }

            return var6x;
         }, var2));
      }

      return Util.sequence(var3).thenCompose((var2x) -> {
         List var3 = (List)var2x.stream().flatMap(UnresolvedBuilderBundle::listBuilders).collect(Util.toMutableList());
         GlyphProvider.Conditional var4 = createFallbackProvider();
         var3.add(CompletableFuture.completedFuture(Optional.of(var4.provider())));
         return Util.sequence(var3).thenCompose((var4x) -> {
            Map var5 = this.resolveProviders(var2x);
            CompletableFuture[] var6 = (CompletableFuture[])var5.values().stream().map((var3) -> {
               return CompletableFuture.runAsync(() -> {
                  this.finalizeProviderLoading(var3, var4);
               }, var2);
            }).toArray((var0) -> {
               return new CompletableFuture[var0];
            });
            return CompletableFuture.allOf(var6).thenApply((var2xx) -> {
               List var3 = var4x.stream().flatMap(Optional::stream).toList();
               return new Preparation(var5, var3);
            });
         });
      });
   }

   private CompletableFuture<Optional<GlyphProvider>> safeLoad(BuilderId var1, GlyphProviderDefinition.Loader var2, ResourceManager var3, Executor var4) {
      return CompletableFuture.supplyAsync(() -> {
         try {
            return Optional.of(var2.load(var3));
         } catch (Exception var4) {
            LOGGER.warn("Failed to load builder {}, rejecting", var1, var4);
            return Optional.empty();
         }
      }, var4);
   }

   private Map<ResourceLocation, List<GlyphProvider.Conditional>> resolveProviders(List<UnresolvedBuilderBundle> var1) {
      HashMap var2 = new HashMap();
      DependencySorter var3 = new DependencySorter();
      var1.forEach((var1x) -> {
         var3.addEntry(var1x.fontId, var1x);
      });
      var3.orderByDependencies((var1x, var2x) -> {
         Objects.requireNonNull(var2);
         var2x.resolve(var2::get).ifPresent((var2xx) -> {
            var2.put(var1x, var2xx);
         });
      });
      return var2;
   }

   private void finalizeProviderLoading(List<GlyphProvider.Conditional> var1, GlyphProvider.Conditional var2) {
      var1.add(0, var2);
      IntOpenHashSet var3 = new IntOpenHashSet();
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         GlyphProvider.Conditional var5 = (GlyphProvider.Conditional)var4.next();
         var3.addAll(var5.provider().getSupportedGlyphs());
      }

      var3.forEach((var1x) -> {
         if (var1x != 32) {
            Iterator var2 = Lists.reverse(var1).iterator();

            while(var2.hasNext()) {
               GlyphProvider.Conditional var3 = (GlyphProvider.Conditional)var2.next();
               if (var3.provider().getGlyph(var1x) != null) {
                  break;
               }
            }

         }
      });
   }

   private static Set<FontOption> getFontOptions(Options var0) {
      EnumSet var1 = EnumSet.noneOf(FontOption.class);
      if ((Boolean)var0.forceUnicodeFont().get()) {
         var1.add(FontOption.UNIFORM);
      }

      if ((Boolean)var0.japaneseGlyphVariants().get()) {
         var1.add(FontOption.JAPANESE_VARIANTS);
      }

      return var1;
   }

   private void apply(Preparation var1, ProfilerFiller var2) {
      var2.push("closing");
      this.lastFontSetCache = null;
      this.fontSets.values().forEach(FontSet::close);
      this.fontSets.clear();
      this.providersToClose.forEach(GlyphProvider::close);
      this.providersToClose.clear();
      Set var3 = getFontOptions(Minecraft.getInstance().options);
      var2.popPush("reloading");
      var1.fontSets().forEach((var2x, var3x) -> {
         FontSet var4 = new FontSet(this.textureManager, var2x);
         var4.reload(Lists.reverse(var3x), var3);
         this.fontSets.put(var2x, var4);
      });
      this.providersToClose.addAll(var1.allProviders);
      var2.pop();
      if (!this.fontSets.containsKey(Minecraft.DEFAULT_FONT)) {
         throw new IllegalStateException("Default font failed to load");
      }
   }

   public void updateOptions(Options var1) {
      Set var2 = getFontOptions(var1);
      Iterator var3 = this.fontSets.values().iterator();

      while(var3.hasNext()) {
         FontSet var4 = (FontSet)var3.next();
         var4.reload(var2);
      }

   }

   private static List<Pair<BuilderId, GlyphProviderDefinition.Conditional>> loadResourceStack(List<Resource> var0, ResourceLocation var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var0.iterator();

      while(var3.hasNext()) {
         Resource var4 = (Resource)var3.next();

         try {
            BufferedReader var5 = var4.openAsReader();

            try {
               JsonElement var6 = (JsonElement)GSON.fromJson(var5, JsonElement.class);
               FontDefinitionFile var7 = (FontDefinitionFile)FontManager.FontDefinitionFile.CODEC.parse(JsonOps.INSTANCE, var6).getOrThrow(JsonParseException::new);
               List var8 = var7.providers;

               for(int var9 = var8.size() - 1; var9 >= 0; --var9) {
                  BuilderId var10 = new BuilderId(var1, var4.sourcePackId(), var9);
                  var2.add(Pair.of(var10, (GlyphProviderDefinition.Conditional)var8.get(var9)));
               }
            } catch (Throwable var12) {
               if (var5 != null) {
                  try {
                     ((Reader)var5).close();
                  } catch (Throwable var11) {
                     var12.addSuppressed(var11);
                  }
               }

               throw var12;
            }

            if (var5 != null) {
               ((Reader)var5).close();
            }
         } catch (Exception var13) {
            LOGGER.warn("Unable to load font '{}' in {} in resourcepack: '{}'", new Object[]{var1, "fonts.json", var4.sourcePackId(), var13});
         }
      }

      return var2;
   }

   public Font createFont() {
      return new Font(this::getFontSetCached, false);
   }

   public Font createFontFilterFishy() {
      return new Font(this::getFontSetCached, true);
   }

   private FontSet getFontSetRaw(ResourceLocation var1) {
      return (FontSet)this.fontSets.getOrDefault(var1, this.missingFontSet);
   }

   private FontSet getFontSetCached(ResourceLocation var1) {
      FontSet var2 = this.lastFontSetCache;
      if (var2 != null && var1.equals(var2.name())) {
         return var2;
      } else {
         FontSet var3 = this.getFontSetRaw(var1);
         this.lastFontSetCache = var3;
         return var3;
      }
   }

   public void close() {
      this.fontSets.values().forEach(FontSet::close);
      this.providersToClose.forEach(GlyphProvider::close);
      this.missingFontSet.close();
   }

   static record BuilderId(ResourceLocation fontId, String pack, int index) {
      BuilderId(ResourceLocation var1, String var2, int var3) {
         super();
         this.fontId = var1;
         this.pack = var2;
         this.index = var3;
      }

      public String toString() {
         String var10000 = String.valueOf(this.fontId);
         return "(" + var10000 + ": builder #" + this.index + " from pack " + this.pack + ")";
      }

      public ResourceLocation fontId() {
         return this.fontId;
      }

      public String pack() {
         return this.pack;
      }

      public int index() {
         return this.index;
      }
   }

   static record Preparation(Map<ResourceLocation, List<GlyphProvider.Conditional>> fontSets, List<GlyphProvider> allProviders) {
      final List<GlyphProvider> allProviders;

      Preparation(Map<ResourceLocation, List<GlyphProvider.Conditional>> var1, List<GlyphProvider> var2) {
         super();
         this.fontSets = var1;
         this.allProviders = var2;
      }

      public Map<ResourceLocation, List<GlyphProvider.Conditional>> fontSets() {
         return this.fontSets;
      }

      public List<GlyphProvider> allProviders() {
         return this.allProviders;
      }
   }

   static record FontDefinitionFile(List<GlyphProviderDefinition.Conditional> providers) {
      final List<GlyphProviderDefinition.Conditional> providers;
      public static final Codec<FontDefinitionFile> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(GlyphProviderDefinition.Conditional.CODEC.listOf().fieldOf("providers").forGetter(FontDefinitionFile::providers)).apply(var0, FontDefinitionFile::new);
      });

      private FontDefinitionFile(List<GlyphProviderDefinition.Conditional> var1) {
         super();
         this.providers = var1;
      }

      public List<GlyphProviderDefinition.Conditional> providers() {
         return this.providers;
      }
   }

   static record UnresolvedBuilderBundle(ResourceLocation fontId, List<BuilderResult> builders, Set<ResourceLocation> dependencies) implements DependencySorter.Entry<ResourceLocation> {
      final ResourceLocation fontId;

      public UnresolvedBuilderBundle(ResourceLocation var1) {
         this(var1, new ArrayList(), new HashSet());
      }

      private UnresolvedBuilderBundle(ResourceLocation var1, List<BuilderResult> var2, Set<ResourceLocation> var3) {
         super();
         this.fontId = var1;
         this.builders = var2;
         this.dependencies = var3;
      }

      public void add(BuilderId var1, FontOption.Filter var2, GlyphProviderDefinition.Reference var3) {
         this.builders.add(new BuilderResult(var1, var2, Either.right(var3.id())));
         this.dependencies.add(var3.id());
      }

      public void add(BuilderId var1, FontOption.Filter var2, CompletableFuture<Optional<GlyphProvider>> var3) {
         this.builders.add(new BuilderResult(var1, var2, Either.left(var3)));
      }

      private Stream<CompletableFuture<Optional<GlyphProvider>>> listBuilders() {
         return this.builders.stream().flatMap((var0) -> {
            return var0.result.left().stream();
         });
      }

      public Optional<List<GlyphProvider.Conditional>> resolve(Function<ResourceLocation, List<GlyphProvider.Conditional>> var1) {
         ArrayList var2 = new ArrayList();
         Iterator var3 = this.builders.iterator();

         while(var3.hasNext()) {
            BuilderResult var4 = (BuilderResult)var3.next();
            Optional var5 = var4.resolve(var1);
            if (!var5.isPresent()) {
               return Optional.empty();
            }

            var2.addAll((Collection)var5.get());
         }

         return Optional.of(var2);
      }

      public void visitRequiredDependencies(Consumer<ResourceLocation> var1) {
         this.dependencies.forEach(var1);
      }

      public void visitOptionalDependencies(Consumer<ResourceLocation> var1) {
      }

      public ResourceLocation fontId() {
         return this.fontId;
      }

      public List<BuilderResult> builders() {
         return this.builders;
      }

      public Set<ResourceLocation> dependencies() {
         return this.dependencies;
      }
   }

   private static record BuilderResult(BuilderId id, FontOption.Filter filter, Either<CompletableFuture<Optional<GlyphProvider>>, ResourceLocation> result) {
      final Either<CompletableFuture<Optional<GlyphProvider>>, ResourceLocation> result;

      BuilderResult(BuilderId var1, FontOption.Filter var2, Either<CompletableFuture<Optional<GlyphProvider>>, ResourceLocation> var3) {
         super();
         this.id = var1;
         this.filter = var2;
         this.result = var3;
      }

      public Optional<List<GlyphProvider.Conditional>> resolve(Function<ResourceLocation, List<GlyphProvider.Conditional>> var1) {
         return (Optional)this.result.map((var1x) -> {
            return ((Optional)var1x.join()).map((var1) -> {
               return List.of(new GlyphProvider.Conditional(var1, this.filter));
            });
         }, (var2) -> {
            List var3 = (List)var1.apply(var2);
            if (var3 == null) {
               FontManager.LOGGER.warn("Can't find font {} referenced by builder {}, either because it's missing, failed to load or is part of loading cycle", var2, this.id);
               return Optional.empty();
            } else {
               return Optional.of(var3.stream().map(this::mergeFilters).toList());
            }
         });
      }

      private GlyphProvider.Conditional mergeFilters(GlyphProvider.Conditional var1) {
         return new GlyphProvider.Conditional(var1.provider(), this.filter.merge(var1.filter()));
      }

      public BuilderId id() {
         return this.id;
      }

      public FontOption.Filter filter() {
         return this.filter;
      }

      public Either<CompletableFuture<Optional<GlyphProvider>>, ResourceLocation> result() {
         return this.result;
      }
   }
}
