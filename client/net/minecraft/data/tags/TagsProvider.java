package net.minecraft.data.tags;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
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

   public final String getName() {
      return "Tags for " + this.registry.key().location();
   }

   protected abstract void addTags();

   public void run(CachedOutput var1) {
      this.builders.clear();
      this.addTags();
      this.builders.forEach((var2, var3) -> {
         List var4 = var3.build();
         List var5 = var4.stream().filter((var1x) -> {
            Registry var10001 = this.registry;
            Objects.requireNonNull(var10001);
            Predicate var2 = var10001::containsKey;
            Map var10002 = this.builders;
            Objects.requireNonNull(var10002);
            return !var1x.verifyIfPresent(var2, var10002::containsKey);
         }).toList();
         if (!var5.isEmpty()) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Couldn't define tag %s as it is missing following references: %s", var2, var5.stream().map(Objects::toString).collect(Collectors.joining(","))));
         } else {
            DataResult var10000 = TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(var4, false));
            Logger var10002 = LOGGER;
            Objects.requireNonNull(var10002);
            JsonElement var6 = (JsonElement)var10000.getOrThrow(false, var10002::error);
            Path var7 = this.pathProvider.json(var2);

            try {
               DataProvider.saveStable(var1, var6, var7);
            } catch (IOException var9) {
               LOGGER.error("Couldn't save tags to {}", var7, var9);
            }

         }
      });
   }

   protected TagAppender<T> tag(TagKey<T> var1) {
      TagBuilder var2 = this.getOrCreateRawBuilder(var1);
      return new TagAppender(var2, this.registry);
   }

   protected TagBuilder getOrCreateRawBuilder(TagKey<T> var1) {
      return (TagBuilder)this.builders.computeIfAbsent(var1.location(), (var0) -> {
         return TagBuilder.create();
      });
   }

   protected static class TagAppender<T> {
      private final TagBuilder builder;
      private final Registry<T> registry;

      TagAppender(TagBuilder var1, Registry<T> var2) {
         super();
         this.builder = var1;
         this.registry = var2;
      }

      public TagAppender<T> add(T var1) {
         this.builder.addElement(this.registry.getKey(var1));
         return this;
      }

      @SafeVarargs
      public final TagAppender<T> add(ResourceKey<T>... var1) {
         ResourceKey[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            ResourceKey var5 = var2[var4];
            this.builder.addElement(var5.location());
         }

         return this;
      }

      public TagAppender<T> addOptional(ResourceLocation var1) {
         this.builder.addOptionalElement(var1);
         return this;
      }

      public TagAppender<T> addTag(TagKey<T> var1) {
         this.builder.addTag(var1.location());
         return this;
      }

      public TagAppender<T> addOptionalTag(ResourceLocation var1) {
         this.builder.addOptionalTag(var1);
         return this;
      }

      @SafeVarargs
      public final TagAppender<T> add(T... var1) {
         Stream var10000 = Stream.of(var1);
         Registry var10001 = this.registry;
         Objects.requireNonNull(var10001);
         var10000.map(var10001::getKey).forEach((var1x) -> {
            this.builder.addElement(var1x);
         });
         return this;
      }
   }
}
