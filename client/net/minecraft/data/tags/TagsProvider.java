package net.minecraft.data.tags;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagFile;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import org.slf4j.Logger;

public abstract class TagsProvider<T> implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected final DataGenerator.PathProvider pathProvider;
   protected final Registry<T> registry;
   private final Map<ResourceLocation, TagBuilder> builders = Maps.newLinkedHashMap();

   protected TagsProvider(DataGenerator var1, Registry<T> var2) {
      super();
      this.pathProvider = var1.createPathProvider(DataGenerator.Target.DATA_PACK, TagManager.getTagDir(var2.key()));
      this.registry = var2;
   }

   @Override
   public final String getName() {
      return "Tags for " + this.registry.key().location();
   }

   protected abstract void addTags();

   @Override
   public void run(CachedOutput var1) {
      this.builders.clear();
      this.addTags();
      this.builders
         .forEach(
            (var2, var3) -> {
               List var4 = var3.build();
               List var5 = var4.stream().filter(var1xx -> !var1xx.verifyIfPresent(this.registry::containsKey, this.builders::containsKey)).toList();
               if (!var5.isEmpty()) {
                  throw new IllegalArgumentException(
                     String.format(
                        "Couldn't define tag %s as it is missing following references: %s",
                        var2,
                        var5.stream().map(Objects::toString).collect(Collectors.joining(","))
                     )
                  );
               } else {
                  JsonElement var6 = (JsonElement)TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(var4, false)).getOrThrow(false, LOGGER::error);
                  Path var7 = this.pathProvider.json(var2);
      
                  try {
                     DataProvider.saveStable(var1, var6, var7);
                  } catch (IOException var9) {
                     LOGGER.error("Couldn't save tags to {}", var7, var9);
                  }
               }
            }
         );
   }

   protected TagsProvider.TagAppender<T> tag(TagKey<T> var1) {
      TagBuilder var2 = this.getOrCreateRawBuilder(var1);
      return new TagsProvider.TagAppender<>(var2, this.registry);
   }

   protected TagBuilder getOrCreateRawBuilder(TagKey<T> var1) {
      return this.builders.computeIfAbsent(var1.location(), var0 -> TagBuilder.create());
   }

   protected static class TagAppender<T> {
      private final TagBuilder builder;
      private final Registry<T> registry;

      TagAppender(TagBuilder var1, Registry<T> var2) {
         super();
         this.builder = var1;
         this.registry = var2;
      }

      public TagsProvider.TagAppender<T> add(T var1) {
         this.builder.addElement(this.registry.getKey((T)var1));
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

      @SafeVarargs
      public final TagsProvider.TagAppender<T> add(T... var1) {
         Stream.of(var1).map(this.registry::getKey).forEach(var1x -> this.builder.addElement(var1x));
         return this;
      }
   }
}
