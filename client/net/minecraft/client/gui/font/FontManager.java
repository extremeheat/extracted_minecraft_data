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
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
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
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class FontManager implements PreparableReloadListener, AutoCloseable {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final String FONTS_PATH = "fonts.json";
   public static final ResourceLocation MISSING_FONT = new ResourceLocation("minecraft", "missing");
   private static final FileToIdConverter FONT_DEFINITIONS = FileToIdConverter.json("font");
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
   private final FontSet missingFontSet;
   private final List<GlyphProvider> providersToClose = new ArrayList<>();
   private final Map<ResourceLocation, FontSet> fontSets = new HashMap<>();
   private final TextureManager textureManager;
   @Nullable
   private volatile FontSet lastFontSetCache;

   public FontManager(TextureManager var1) {
      super();
      this.textureManager = var1;
      this.missingFontSet = Util.make(new FontSet(var1, MISSING_FONT), var0 -> var0.reload(List.of(createFallbackProvider()), Set.of()));
   }

   private static GlyphProvider.Conditional createFallbackProvider() {
      return new GlyphProvider.Conditional(new AllMissingGlyphProvider(), FontOption.Filter.ALWAYS_PASS);
   }

   @Override
   public CompletableFuture<Void> reload(
      PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6
   ) {
      var3.startTick();
      var3.endTick();
      return this.prepare(var2, var5).thenCompose(var1::wait).thenAcceptAsync(var2x -> this.apply(var2x, var4), var6);
   }

   private CompletableFuture<FontManager.Preparation> prepare(ResourceManager var1, Executor var2) {
      ArrayList var3 = new ArrayList();

      for (Entry var5 : FONT_DEFINITIONS.listMatchingResourceStacks(var1).entrySet()) {
         ResourceLocation var6 = FONT_DEFINITIONS.fileToId((ResourceLocation)var5.getKey());
         var3.add(CompletableFuture.supplyAsync(() -> {
            List var5x = loadResourceStack((List<Resource>)var5.getValue(), var6);
            FontManager.UnresolvedBuilderBundle var6x = new FontManager.UnresolvedBuilderBundle(var6);

            for (Pair var8 : var5x) {
               FontManager.BuilderId var9 = (FontManager.BuilderId)var8.getFirst();
               FontOption.Filter var10 = ((GlyphProviderDefinition.Conditional)var8.getSecond()).filter();
               ((GlyphProviderDefinition.Conditional)var8.getSecond()).definition().unpack().ifLeft(var6xx -> {
                  CompletableFuture var7 = this.safeLoad(var9, var6xx, var1, var2);
                  var6x.add(var9, var10, var7);
               }).ifRight(var3xx -> var6x.add(var9, var10, var3xx));
            }

            return var6x;
         }, var2));
      }

      return Util.sequence(var3)
         .thenCompose(
            var2x -> {
               List var3x = var2x.stream().flatMap(FontManager.UnresolvedBuilderBundle::listBuilders).collect(Util.toMutableList());
               GlyphProvider.Conditional var4 = createFallbackProvider();
               var3x.add(CompletableFuture.completedFuture(Optional.of(var4.provider())));
               return Util.sequence(var3x)
                  .thenCompose(
                     var4x -> {
                        Map var5x = this.resolveProviders(var2x);
                        CompletableFuture[] var6x = var5x.values()
                           .stream()
                           .map(var3xxx -> CompletableFuture.runAsync(() -> this.finalizeProviderLoading(var3xxx, var4), var2))
                           .toArray(CompletableFuture[]::new);
                        return CompletableFuture.allOf(var6x).thenApply(var2xxx -> {
                           List var3xxx = var4x.stream().flatMap(Optional::stream).toList();
                           return new FontManager.Preparation(var5x, var3xxx);
                        });
                     }
                  );
            }
         );
   }

   private CompletableFuture<Optional<GlyphProvider>> safeLoad(
      FontManager.BuilderId var1, GlyphProviderDefinition.Loader var2, ResourceManager var3, Executor var4
   ) {
      return CompletableFuture.supplyAsync(() -> {
         try {
            return Optional.of(var2.load(var3));
         } catch (Exception var4x) {
            LOGGER.warn("Failed to load builder {}, rejecting", var1, var4x);
            return Optional.empty();
         }
      }, var4);
   }

   private Map<ResourceLocation, List<GlyphProvider.Conditional>> resolveProviders(List<FontManager.UnresolvedBuilderBundle> var1) {
      HashMap var2 = new HashMap();
      DependencySorter var3 = new DependencySorter();
      var1.forEach(var1x -> var3.addEntry(var1x.fontId, var1x));
      var3.orderByDependencies((var1x, var2x) -> var2x.resolve(var2::get).ifPresent(var2xx -> var2.put(var1x, var2xx)));
      return var2;
   }

   private void finalizeProviderLoading(List<GlyphProvider.Conditional> var1, GlyphProvider.Conditional var2) {
      var1.add(0, var2);
      IntOpenHashSet var3 = new IntOpenHashSet();

      for (GlyphProvider.Conditional var5 : var1) {
         var3.addAll(var5.provider().getSupportedGlyphs());
      }

      var3.forEach(var1x -> {
         if (var1x != 32) {
            for (GlyphProvider.Conditional var3x : Lists.reverse(var1)) {
               if (var3x.provider().getGlyph(var1x) != null) {
                  break;
               }
            }
         }
      });
   }

   private static Set<FontOption> getFontOptions(Options var0) {
      EnumSet var1 = EnumSet.noneOf(FontOption.class);
      if (var0.forceUnicodeFont().get()) {
         var1.add(FontOption.UNIFORM);
      }

      if (var0.japaneseGlyphVariants().get()) {
         var1.add(FontOption.JAPANESE_VARIANTS);
      }

      return var1;
   }

   private void apply(FontManager.Preparation var1, ProfilerFiller var2) {
      var2.startTick();
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
      var2.endTick();
      if (!this.fontSets.containsKey(Minecraft.DEFAULT_FONT)) {
         throw new IllegalStateException("Default font failed to load");
      }
   }

   public void updateOptions(Options var1) {
      Set var2 = getFontOptions(var1);

      for (FontSet var4 : this.fontSets.values()) {
         var4.reload(var2);
      }
   }

   private static List<Pair<FontManager.BuilderId, GlyphProviderDefinition.Conditional>> loadResourceStack(List<Resource> var0, ResourceLocation var1) {
      ArrayList var2 = new ArrayList();

      for (Resource var4 : var0) {
         try (BufferedReader var5 = var4.openAsReader()) {
            JsonElement var6 = (JsonElement)GSON.fromJson(var5, JsonElement.class);
            FontManager.FontDefinitionFile var7 = (FontManager.FontDefinitionFile)FontManager.FontDefinitionFile.CODEC
               .parse(JsonOps.INSTANCE, var6)
               .getOrThrow(JsonParseException::new);
            List var8 = var7.providers;

            for (int var9 = var8.size() - 1; var9 >= 0; var9--) {
               FontManager.BuilderId var10 = new FontManager.BuilderId(var1, var4.sourcePackId(), var9);
               var2.add(Pair.of(var10, (GlyphProviderDefinition.Conditional)var8.get(var9)));
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
      return this.fontSets.getOrDefault(var1, this.missingFontSet);
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

   @Override
   public void close() {
      this.fontSets.values().forEach(FontSet::close);
      this.providersToClose.forEach(GlyphProvider::close);
      this.missingFontSet.close();
   }

   static record BuilderId(ResourceLocation fontId, String pack, int index) {
      BuilderId(ResourceLocation fontId, String pack, int index) {
         super();
         this.fontId = fontId;
         this.pack = pack;
         this.index = index;
      }

      public String toString() {
         return "(" + this.fontId + ": builder #" + this.index + " from pack " + this.pack + ")";
      }
   }

   static record BuilderResult(FontManager.BuilderId id, FontOption.Filter filter, Either<CompletableFuture<Optional<GlyphProvider>>, ResourceLocation> result) {

      BuilderResult(FontManager.BuilderId id, FontOption.Filter filter, Either<CompletableFuture<Optional<GlyphProvider>>, ResourceLocation> result) {
         super();
         this.id = id;
         this.filter = filter;
         this.result = result;
      }

      public Optional<List<GlyphProvider.Conditional>> resolve(Function<ResourceLocation, List<GlyphProvider.Conditional>> var1) {
         return (Optional<List<GlyphProvider.Conditional>>)this.result
            .map(
               var1x -> ((Optional)var1x.join()).map(var1xx -> List.of(new GlyphProvider.Conditional(var1xx, this.filter))),
               var2 -> {
                  List var3 = (List)var1.apply(var2);
                  if (var3 == null) {
                     FontManager.LOGGER
                        .warn(
                           "Can't find font {} referenced by builder {}, either because it's missing, failed to load or is part of loading cycle",
                           var2,
                           this.id
                        );
                     return Optional.empty();
                  } else {
                     return Optional.of(var3.stream().map(this::mergeFilters).toList());
                  }
               }
            );
      }

      private GlyphProvider.Conditional mergeFilters(GlyphProvider.Conditional var1) {
         return new GlyphProvider.Conditional(var1.provider(), this.filter.merge(var1.filter()));
      }
   }

   static record FontDefinitionFile(List<GlyphProviderDefinition.Conditional> providers) {
      public static final Codec<FontManager.FontDefinitionFile> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(GlyphProviderDefinition.Conditional.CODEC.listOf().fieldOf("providers").forGetter(FontManager.FontDefinitionFile::providers))
               .apply(var0, FontManager.FontDefinitionFile::new)
      );

      private FontDefinitionFile(List<GlyphProviderDefinition.Conditional> providers) {
         super();
         this.providers = providers;
      }
   }

   static record Preparation(Map<ResourceLocation, List<GlyphProvider.Conditional>> fontSets, List<GlyphProvider> allProviders) {

      Preparation(Map<ResourceLocation, List<GlyphProvider.Conditional>> fontSets, List<GlyphProvider> allProviders) {
         super();
         this.fontSets = fontSets;
         this.allProviders = allProviders;
      }
   }

   static record UnresolvedBuilderBundle(ResourceLocation fontId, List<FontManager.BuilderResult> builders, Set<ResourceLocation> dependencies)
      implements DependencySorter.Entry<ResourceLocation> {

      public UnresolvedBuilderBundle(ResourceLocation var1) {
         this(var1, new ArrayList<>(), new HashSet<>());
      }

      private UnresolvedBuilderBundle(ResourceLocation fontId, List<FontManager.BuilderResult> builders, Set<ResourceLocation> dependencies) {
         super();
         this.fontId = fontId;
         this.builders = builders;
         this.dependencies = dependencies;
      }

      public void add(FontManager.BuilderId var1, FontOption.Filter var2, GlyphProviderDefinition.Reference var3) {
         this.builders.add(new FontManager.BuilderResult(var1, var2, Either.right(var3.id())));
         this.dependencies.add(var3.id());
      }

      public void add(FontManager.BuilderId var1, FontOption.Filter var2, CompletableFuture<Optional<GlyphProvider>> var3) {
         this.builders.add(new FontManager.BuilderResult(var1, var2, Either.left(var3)));
      }

      private Stream<CompletableFuture<Optional<GlyphProvider>>> listBuilders() {
         return this.builders.stream().flatMap(var0 -> var0.result.left().stream());
      }

      public Optional<List<GlyphProvider.Conditional>> resolve(Function<ResourceLocation, List<GlyphProvider.Conditional>> var1) {
         ArrayList var2 = new ArrayList();

         for (FontManager.BuilderResult var4 : this.builders) {
            Optional var5 = var4.resolve(var1);
            if (!var5.isPresent()) {
               return Optional.empty();
            }

            var2.addAll((Collection)var5.get());
         }

         return Optional.of(var2);
      }

      @Override
      public void visitRequiredDependencies(Consumer<ResourceLocation> var1) {
         this.dependencies.forEach(var1);
      }

      @Override
      public void visitOptionalDependencies(Consumer<ResourceLocation> var1) {
      }
   }
}
