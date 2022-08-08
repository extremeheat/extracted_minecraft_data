package net.minecraft.tags;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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

   public Map<ResourceLocation, List<EntryWithSource>> load(ResourceManager var1) {
      HashMap var2 = Maps.newHashMap();
      Iterator var3 = var1.listResourceStacks(this.directory, (var0) -> {
         return var0.getPath().endsWith(".json");
      }).entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry var4 = (Map.Entry)var3.next();
         ResourceLocation var5 = (ResourceLocation)var4.getKey();
         String var6 = var5.getPath();
         ResourceLocation var7 = new ResourceLocation(var5.getNamespace(), var6.substring(this.directory.length() + 1, var6.length() - PATH_SUFFIX_LENGTH));
         Iterator var8 = ((List)var4.getValue()).iterator();

         while(var8.hasNext()) {
            Resource var9 = (Resource)var8.next();

            try {
               BufferedReader var10 = var9.openAsReader();

               try {
                  JsonElement var11 = JsonParser.parseReader(var10);
                  List var12 = (List)var2.computeIfAbsent(var7, (var0) -> {
                     return new ArrayList();
                  });
                  DataResult var10000 = TagFile.CODEC.parse(new Dynamic(JsonOps.INSTANCE, var11));
                  Logger var10002 = LOGGER;
                  Objects.requireNonNull(var10002);
                  TagFile var13 = (TagFile)var10000.getOrThrow(false, var10002::error);
                  if (var13.replace()) {
                     var12.clear();
                  }

                  String var14 = var9.sourcePackId();
                  var13.entries().forEach((var2x) -> {
                     var12.add(new EntryWithSource(var2x, var14));
                  });
               } catch (Throwable var16) {
                  if (var10 != null) {
                     try {
                        var10.close();
                     } catch (Throwable var15) {
                        var16.addSuppressed(var15);
                     }
                  }

                  throw var16;
               }

               if (var10 != null) {
                  var10.close();
               }
            } catch (Exception var17) {
               LOGGER.error("Couldn't read tag list {} from {} in data pack {}", new Object[]{var7, var5, var9.sourcePackId(), var17});
            }
         }
      }

      return var2;
   }

   private static void visitDependenciesAndElement(Map<ResourceLocation, List<EntryWithSource>> var0, Multimap<ResourceLocation, ResourceLocation> var1, Set<ResourceLocation> var2, ResourceLocation var3, BiConsumer<ResourceLocation, List<EntryWithSource>> var4) {
      if (var2.add(var3)) {
         var1.get(var3).forEach((var4x) -> {
            visitDependenciesAndElement(var0, var1, var2, var4x, var4);
         });
         List var5 = (List)var0.get(var3);
         if (var5 != null) {
            var4.accept(var3, var5);
         }

      }
   }

   private static boolean isCyclic(Multimap<ResourceLocation, ResourceLocation> var0, ResourceLocation var1, ResourceLocation var2) {
      Collection var3 = var0.get(var2);
      return var3.contains(var1) ? true : var3.stream().anyMatch((var2x) -> {
         return isCyclic(var0, var1, var2x);
      });
   }

   private static void addDependencyIfNotCyclic(Multimap<ResourceLocation, ResourceLocation> var0, ResourceLocation var1, ResourceLocation var2) {
      if (!isCyclic(var0, var1, var2)) {
         var0.put(var1, var2);
      }

   }

   private Either<Collection<EntryWithSource>, Collection<T>> build(TagEntry.Lookup<T> var1, List<EntryWithSource> var2) {
      ImmutableSet.Builder var3 = ImmutableSet.builder();
      ArrayList var4 = new ArrayList();
      Iterator var5 = var2.iterator();

      while(var5.hasNext()) {
         EntryWithSource var6 = (EntryWithSource)var5.next();
         TagEntry var10000 = var6.entry();
         Objects.requireNonNull(var3);
         if (!var10000.build(var1, var3::add)) {
            var4.add(var6);
         }
      }

      return var4.isEmpty() ? Either.right(var3.build()) : Either.left(var4);
   }

   public Map<ResourceLocation, Collection<T>> build(Map<ResourceLocation, List<EntryWithSource>> var1) {
      final HashMap var2 = Maps.newHashMap();
      TagEntry.Lookup var3 = new TagEntry.Lookup<T>() {
         @Nullable
         public T element(ResourceLocation var1) {
            return ((Optional)TagLoader.this.idToValue.apply(var1)).orElse((Object)null);
         }

         @Nullable
         public Collection<T> tag(ResourceLocation var1) {
            return (Collection)var2.get(var1);
         }
      };
      HashMultimap var4 = HashMultimap.create();
      var1.forEach((var1x, var2x) -> {
         var2x.forEach((var2) -> {
            var2.entry.visitRequiredDependencies((var2x) -> {
               addDependencyIfNotCyclic(var4, var1x, var2x);
            });
         });
      });
      var1.forEach((var1x, var2x) -> {
         var2x.forEach((var2) -> {
            var2.entry.visitOptionalDependencies((var2x) -> {
               addDependencyIfNotCyclic(var4, var1x, var2x);
            });
         });
      });
      HashSet var5 = Sets.newHashSet();
      var1.keySet().forEach((var6) -> {
         visitDependenciesAndElement(var1, var4, var5, var6, (var3x, var4x) -> {
            this.build(var3, var4x).ifLeft((var1) -> {
               LOGGER.error("Couldn't load tag {} as it is missing following references: {}", var3x, var1.stream().map(Objects::toString).collect(Collectors.joining(", ")));
            }).ifRight((var2x) -> {
               var2.put(var3x, var2x);
            });
         });
      });
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

      public String toString() {
         return this.entry + " (from " + this.source + ")";
      }

      public TagEntry entry() {
         return this.entry;
      }

      public String source() {
         return this.source;
      }
   }
}
