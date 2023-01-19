package net.minecraft.tags;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public class TagLoader<T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String PATH_SUFFIX = ".json";
   private static final int PATH_SUFFIX_LENGTH = ".json".length();
   final Function<ResourceLocation, Optional<T>> idToValue;
   private final String directory;

   public TagLoader(Function<ResourceLocation, Optional<T>> var1, String var2) {
      super();
      this.idToValue = var1;
      this.directory = var2;
   }

   public Map<ResourceLocation, List<TagLoader.EntryWithSource>> load(ResourceManager var1) {
      HashMap var2 = Maps.newHashMap();

      for(Entry var4 : var1.listResourceStacks(this.directory, var0 -> var0.getPath().endsWith(".json")).entrySet()) {
         ResourceLocation var5 = (ResourceLocation)var4.getKey();
         String var6 = var5.getPath();
         ResourceLocation var7 = new ResourceLocation(var5.getNamespace(), var6.substring(this.directory.length() + 1, var6.length() - PATH_SUFFIX_LENGTH));

         for(Resource var9 : (List)var4.getValue()) {
            try (BufferedReader var10 = var9.openAsReader()) {
               JsonElement var11 = JsonParser.parseReader(var10);
               List var12 = var2.computeIfAbsent(var7, var0 -> new ArrayList());
               TagFile var13 = (TagFile)TagFile.CODEC.parse(new Dynamic(JsonOps.INSTANCE, var11)).getOrThrow(false, LOGGER::error);
               if (var13.replace()) {
                  var12.clear();
               }

               String var14 = var9.sourcePackId();
               var13.entries().forEach(var2x -> var12.add(new TagLoader.EntryWithSource(var2x, var14)));
            } catch (Exception var17) {
               LOGGER.error("Couldn't read tag list {} from {} in data pack {}", new Object[]{var7, var5, var9.sourcePackId(), var17});
            }
         }
      }

      return var2;
   }

   private static void visitDependenciesAndElement(
      Map<ResourceLocation, List<TagLoader.EntryWithSource>> var0,
      Multimap<ResourceLocation, ResourceLocation> var1,
      Set<ResourceLocation> var2,
      ResourceLocation var3,
      BiConsumer<ResourceLocation, List<TagLoader.EntryWithSource>> var4
   ) {
      if (var2.add(var3)) {
         var1.get(var3).forEach(var4x -> visitDependenciesAndElement(var0, var1, var2, var4x, var4));
         List var5 = (List)var0.get(var3);
         if (var5 != null) {
            var4.accept(var3, var5);
         }
      }
   }

   private static boolean isCyclic(Multimap<ResourceLocation, ResourceLocation> var0, ResourceLocation var1, ResourceLocation var2) {
      Collection var3 = var0.get(var2);
      return var3.contains(var1) ? true : var3.stream().anyMatch(var2x -> isCyclic(var0, var1, var2x));
   }

   private static void addDependencyIfNotCyclic(Multimap<ResourceLocation, ResourceLocation> var0, ResourceLocation var1, ResourceLocation var2) {
      if (!isCyclic(var0, var1, var2)) {
         var0.put(var1, var2);
      }
   }

   private Either<Collection<TagLoader.EntryWithSource>, Collection<T>> build(TagEntry.Lookup<T> var1, List<TagLoader.EntryWithSource> var2) {
      Builder var3 = ImmutableSet.builder();
      ArrayList var4 = new ArrayList();

      for(TagLoader.EntryWithSource var6 : var2) {
         if (!var6.entry().build(var1, var3::add)) {
            var4.add(var6);
         }
      }

      return var4.isEmpty() ? Either.right(var3.build()) : Either.left(var4);
   }

   public Map<ResourceLocation, Collection<T>> build(Map<ResourceLocation, List<TagLoader.EntryWithSource>> var1) {
      final HashMap var2 = Maps.newHashMap();
      TagEntry.Lookup var3 = new TagEntry.Lookup<T>() {
         @Nullable
         @Override
         public T element(ResourceLocation var1) {
            return TagLoader.this.idToValue.apply(var1).orElse((T)null);
         }

         @Nullable
         @Override
         public Collection<T> tag(ResourceLocation var1) {
            return (Collection<T>)var2.get(var1);
         }
      };
      HashMultimap var4 = HashMultimap.create();
      var1.forEach(
         (var1x, var2x) -> var2x.forEach(var2xx -> var2xx.entry.visitRequiredDependencies(var2xxx -> addDependencyIfNotCyclic(var4, var1x, var2xxx)))
      );
      var1.forEach(
         (var1x, var2x) -> var2x.forEach(var2xx -> var2xx.entry.visitOptionalDependencies(var2xxx -> addDependencyIfNotCyclic(var4, var1x, var2xxx)))
      );
      HashSet var5 = Sets.newHashSet();
      var1.keySet()
         .forEach(
            var6 -> visitDependenciesAndElement(
                  var1,
                  var4,
                  var5,
                  var6,
                  (var3xx, var4xx) -> this.build(var3, var4xx)
                        .ifLeft(
                           var1xxx -> LOGGER.error(
                                 "Couldn't load tag {} as it is missing following references: {}",
                                 var3xx,
                                 var1xxx.stream().map(Objects::toString).collect(Collectors.joining(", "))
                              )
                        )
                        .ifRight(var2xxx -> var2.put(var3xx, var2xxx))
               )
         );
      return var2;
   }

   public Map<ResourceLocation, Collection<T>> loadAndBuild(ResourceManager var1) {
      return this.build(this.load(var1));
   }

   public static record EntryWithSource(TagEntry a, String b) {
      final TagEntry entry;
      private final String source;

      public EntryWithSource(TagEntry var1, String var2) {
         super();
         this.entry = var1;
         this.source = var2;
      }

      @Override
      public String toString() {
         return this.entry + " (from " + this.source + ")";
      }
   }
}
