package net.minecraft.data.tags;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagFile;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import org.slf4j.Logger;

public abstract class TagsProvider<T> implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected final PackOutput.PathProvider pathProvider;
   protected final CompletableFuture<HolderLookup.Provider> lookupProvider;
   protected final ResourceKey<? extends Registry<T>> registryKey;
   private final Map<ResourceLocation, TagBuilder> builders = Maps.newLinkedHashMap();

   protected TagsProvider(PackOutput var1, ResourceKey<? extends Registry<T>> var2, CompletableFuture<HolderLookup.Provider> var3) {
      super();
      this.pathProvider = var1.createPathProvider(PackOutput.Target.DATA_PACK, TagManager.getTagDir(var2));
      this.lookupProvider = var3;
      this.registryKey = var2;
   }

   @Override
   public final String getName() {
      return "Tags for " + this.registryKey.location();
   }

   protected abstract void addTags(HolderLookup.Provider var1);

   @Override
   public CompletableFuture<?> run(CachedOutput var1) {
      return this.lookupProvider
         .thenCompose(
            var2 -> {
               this.builders.clear();
               this.addTags(var2);
               HolderLookup.RegistryLookup var3 = var2.lookupOrThrow(this.registryKey);
               Predicate var4 = var2x -> var3.get(ResourceKey.create(this.registryKey, var2x)).isPresent();
               return CompletableFuture.allOf(
                  this.builders
                     .entrySet()
                     .stream()
                     .map(
                        var3x -> {
                           ResourceLocation var4x = var3x.getKey();
                           TagBuilder var5 = var3x.getValue();
                           List var6 = var5.build();
                           List var7 = var6.stream().filter(var2xx -> !var2xx.verifyIfPresent(var4, this.builders::containsKey)).toList();
                           if (!var7.isEmpty()) {
                              throw new IllegalArgumentException(
                                 String.format(
                                    Locale.ROOT,
                                    "Couldn't define tag %s as it is missing following references: %s",
                                    var4x,
                                    var7.stream().map(Objects::toString).collect(Collectors.joining(","))
                                 )
                              );
                           } else {
                              JsonElement var8 = (JsonElement)TagFile.CODEC
                                 .encodeStart(JsonOps.INSTANCE, new TagFile(var6, false))
                                 .getOrThrow(false, LOGGER::error);
                              Path var9 = this.pathProvider.json(var4x);
                              return DataProvider.saveStable(var1, var8, var9);
                           }
                        }
                     )
                     .toArray(var0 -> new CompletableFuture[var0])
               );
            }
         );
   }

   protected TagsProvider.TagAppender<T> tag(TagKey<T> var1) {
      TagBuilder var2 = this.getOrCreateRawBuilder(var1);
      return new TagsProvider.TagAppender<>(var2);
   }

   protected TagBuilder getOrCreateRawBuilder(TagKey<T> var1) {
      return this.builders.computeIfAbsent(var1.location(), var0 -> TagBuilder.create());
   }

   protected static class TagAppender<T> {
      private final TagBuilder builder;

      protected TagAppender(TagBuilder var1) {
         super();
         this.builder = var1;
      }

      public final TagsProvider.TagAppender<T> add(ResourceKey<T> var1) {
         this.builder.addElement(var1.location());
         return this;
      }

      @SafeVarargs
      public final TagsProvider.TagAppender<T> add(ResourceKey<T>... var1) {
         for(ResourceKey var5 : var1) {
            this.builder.addElement(var5.location());
         }

         return this;
      }

      public TagsProvider.TagAppender<T> addOptional(ResourceLocation var1) {
         this.builder.addOptionalElement(var1);
         return this;
      }

      public TagsProvider.TagAppender<T> addTag(TagKey<T> var1) {
         this.builder.addTag(var1.location());
         return this;
      }

      public TagsProvider.TagAppender<T> addOptionalTag(ResourceLocation var1) {
         this.builder.addOptionalTag(var1);
         return this;
      }
   }
}
