package net.minecraft.tags;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.DependencySorter;
import org.slf4j.Logger;

public class TagLoader<T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   final ElementLookup<T> elementLookup;
   private final String directory;

   public TagLoader(ElementLookup<T> var1, String var2) {
      super();
      this.elementLookup = var1;
      this.directory = var2;
   }

   public Map<ResourceLocation, List<EntryWithSource>> load(ResourceManager var1) {
      HashMap var2 = new HashMap();
      FileToIdConverter var3 = FileToIdConverter.json(this.directory);

      for(Map.Entry var5 : var3.listMatchingResourceStacks(var1).entrySet()) {
         ResourceLocation var6 = (ResourceLocation)var5.getKey();
         ResourceLocation var7 = var3.fileToId(var6);

         for(Resource var9 : (List)var5.getValue()) {
            try {
               BufferedReader var10 = var9.openAsReader();

               try {
                  JsonElement var11 = JsonParser.parseReader(var10);
                  List var12 = (List)var2.computeIfAbsent(var7, (var0) -> new ArrayList());
                  TagFile var13 = (TagFile)TagFile.CODEC.parse(new Dynamic(JsonOps.INSTANCE, var11)).getOrThrow();
                  if (var13.replace()) {
                     var12.clear();
                  }

                  String var14 = var9.sourcePackId();
                  var13.entries().forEach((var2x) -> var12.add(new EntryWithSource(var2x, var14)));
               } catch (Throwable var16) {
                  if (var10 != null) {
                     try {
                        ((Reader)var10).close();
                     } catch (Throwable var15) {
                        var16.addSuppressed(var15);
                     }
                  }

                  throw var16;
               }

               if (var10 != null) {
                  ((Reader)var10).close();
               }
            } catch (Exception var17) {
               LOGGER.error("Couldn't read tag list {} from {} in data pack {}", new Object[]{var7, var6, var9.sourcePackId(), var17});
            }
         }
      }

      return var2;
   }

   private Either<List<EntryWithSource>, List<T>> tryBuildTag(TagEntry.Lookup<T> var1, List<EntryWithSource> var2) {
      LinkedHashSet var3 = new LinkedHashSet();
      ArrayList var4 = new ArrayList();

      for(EntryWithSource var6 : var2) {
         TagEntry var10000 = var6.entry();
         Objects.requireNonNull(var3);
         if (!var10000.build(var1, var3::add)) {
            var4.add(var6);
         }
      }

      return var4.isEmpty() ? Either.right(List.copyOf(var3)) : Either.left(var4);
   }

   public Map<ResourceLocation, List<T>> build(Map<ResourceLocation, List<EntryWithSource>> var1) {
      final HashMap var2 = new HashMap();
      TagEntry.Lookup var3 = new TagEntry.Lookup<T>() {
         @Nullable
         public T element(ResourceLocation var1, boolean var2x) {
            return (T)TagLoader.this.elementLookup.get(var1, var2x).orElse((Object)null);
         }

         @Nullable
         public Collection<T> tag(ResourceLocation var1) {
            return (Collection)var2.get(var1);
         }
      };
      DependencySorter var4 = new DependencySorter();
      var1.forEach((var1x, var2x) -> var4.addEntry(var1x, new SortingEntry(var2x)));
      var4.orderByDependencies((var3x, var4x) -> this.tryBuildTag(var3, var4x.entries).ifLeft((var1) -> LOGGER.error("Couldn't load tag {} as it is missing following references: {}", var3x, var1.stream().map(Objects::toString).collect(Collectors.joining(", ")))).ifRight((var2x) -> var2.put(var3x, var2x)));
      return var2;
   }

   public static <T> void loadTagsFromNetwork(TagNetworkSerialization.NetworkPayload var0, WritableRegistry<T> var1) {
      Map var10000 = var0.resolve(var1).tags;
      Objects.requireNonNull(var1);
      var10000.forEach(var1::bindTag);
   }

   public static List<Registry.PendingTags<?>> loadTagsForExistingRegistries(ResourceManager var0, RegistryAccess var1) {
      return (List)var1.registries().map((var1x) -> loadPendingTags(var0, var1x.value())).flatMap(Optional::stream).collect(Collectors.toUnmodifiableList());
   }

   public static <T> void loadTagsForRegistry(ResourceManager var0, WritableRegistry<T> var1) {
      ResourceKey var2 = var1.key();
      TagLoader var3 = new TagLoader(TagLoader.ElementLookup.fromWritableRegistry(var1), Registries.tagsDirPath(var2));
      var3.build(var3.load(var0)).forEach((var2x, var3x) -> var1.bindTag(TagKey.create(var2, var2x), var3x));
   }

   private static <T> Map<TagKey<T>, List<Holder<T>>> wrapTags(ResourceKey<? extends Registry<T>> var0, Map<ResourceLocation, List<Holder<T>>> var1) {
      return (Map)var1.entrySet().stream().collect(Collectors.toUnmodifiableMap((var1x) -> TagKey.create(var0, (ResourceLocation)var1x.getKey()), Map.Entry::getValue));
   }

   private static <T> Optional<Registry.PendingTags<T>> loadPendingTags(ResourceManager var0, Registry<T> var1) {
      ResourceKey var2 = var1.key();
      TagLoader var3 = new TagLoader(TagLoader.ElementLookup.fromFrozenRegistry(var1), Registries.tagsDirPath(var2));
      LoadResult var4 = new LoadResult(var2, wrapTags(var1.key(), var3.build(var3.load(var0))));
      return var4.tags().isEmpty() ? Optional.empty() : Optional.of(var1.prepareTagReload(var4));
   }

   public static List<HolderLookup.RegistryLookup<?>> buildUpdatedLookups(RegistryAccess.Frozen var0, List<Registry.PendingTags<?>> var1) {
      ArrayList var2 = new ArrayList();
      var0.registries().forEach((var2x) -> {
         Registry.PendingTags var3 = findTagsForRegistry(var1, var2x.key());
         var2.add(var3 != null ? var3.lookup() : var2x.value());
      });
      return var2;
   }

   @Nullable
   private static Registry.PendingTags<?> findTagsForRegistry(List<Registry.PendingTags<?>> var0, ResourceKey<? extends Registry<?>> var1) {
      for(Registry.PendingTags var3 : var0) {
         if (var3.key() == var1) {
            return var3;
         }
      }

      return null;
   }

   public static record EntryWithSource(TagEntry entry, String source) {
      final TagEntry entry;

      public EntryWithSource(TagEntry var1, String var2) {
         super();
         this.entry = var1;
         this.source = var2;
      }

      public String toString() {
         String var10000 = String.valueOf(this.entry);
         return var10000 + " (from " + this.source + ")";
      }
   }

   static record SortingEntry(List<EntryWithSource> entries) implements DependencySorter.Entry<ResourceLocation> {
      final List<EntryWithSource> entries;

      SortingEntry(List<EntryWithSource> var1) {
         super();
         this.entries = var1;
      }

      public void visitRequiredDependencies(Consumer<ResourceLocation> var1) {
         this.entries.forEach((var1x) -> var1x.entry.visitRequiredDependencies(var1));
      }

      public void visitOptionalDependencies(Consumer<ResourceLocation> var1) {
         this.entries.forEach((var1x) -> var1x.entry.visitOptionalDependencies(var1));
      }
   }

   public static record LoadResult<T>(ResourceKey<? extends Registry<T>> key, Map<TagKey<T>, List<Holder<T>>> tags) {
      final Map<TagKey<T>, List<Holder<T>>> tags;

      public LoadResult(ResourceKey<? extends Registry<T>> var1, Map<TagKey<T>, List<Holder<T>>> var2) {
         super();
         this.key = var1;
         this.tags = var2;
      }
   }

   public interface ElementLookup<T> {
      Optional<? extends T> get(ResourceLocation var1, boolean var2);

      static <T> ElementLookup<? extends Holder<T>> fromFrozenRegistry(Registry<T> var0) {
         return (var1, var2) -> var0.get(var1);
      }

      static <T> ElementLookup<Holder<T>> fromWritableRegistry(WritableRegistry<T> var0) {
         HolderGetter var1 = var0.createRegistrationLookup();
         return (var2, var3) -> ((HolderGetter)(var3 ? var1 : var0)).get(ResourceKey.create(var0.key(), var2));
      }
   }
}
