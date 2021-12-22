package net.minecraft.tags;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagContainer {
   static final Logger LOGGER = LogManager.getLogger();
   public static final TagContainer EMPTY = new TagContainer(ImmutableMap.of());
   private final Map<ResourceKey<? extends Registry<?>>, TagCollection<?>> collections;

   TagContainer(Map<ResourceKey<? extends Registry<?>>, TagCollection<?>> var1) {
      super();
      this.collections = var1;
   }

   @Nullable
   private <T> TagCollection<T> get(ResourceKey<? extends Registry<T>> var1) {
      return (TagCollection)this.collections.get(var1);
   }

   public <T> TagCollection<T> getOrEmpty(ResourceKey<? extends Registry<T>> var1) {
      return (TagCollection)this.collections.getOrDefault(var1, TagCollection.empty());
   }

   public <T, E extends Exception> Tag<T> getTagOrThrow(ResourceKey<? extends Registry<T>> var1, ResourceLocation var2, Function<ResourceLocation, E> var3) throws E {
      TagCollection var4 = this.get(var1);
      if (var4 == null) {
         throw (Exception)var3.apply(var2);
      } else {
         Tag var5 = var4.getTag(var2);
         if (var5 == null) {
            throw (Exception)var3.apply(var2);
         } else {
            return var5;
         }
      }
   }

   public <T, E extends Exception> ResourceLocation getIdOrThrow(ResourceKey<? extends Registry<T>> var1, Tag<T> var2, Supplier<E> var3) throws E {
      TagCollection var4 = this.get(var1);
      if (var4 == null) {
         throw (Exception)var3.get();
      } else {
         ResourceLocation var5 = var4.getId(var2);
         if (var5 == null) {
            throw (Exception)var3.get();
         } else {
            return var5;
         }
      }
   }

   public void getAll(TagContainer.CollectionConsumer var1) {
      this.collections.forEach((var1x, var2) -> {
         acceptCap(var1, var1x, var2);
      });
   }

   private static <T> void acceptCap(TagContainer.CollectionConsumer var0, ResourceKey<? extends Registry<?>> var1, TagCollection<?> var2) {
      var0.accept(var1, var2);
   }

   public void bindToGlobal() {
      StaticTags.resetAll(this);
      Blocks.rebuildCache();
   }

   public Map<ResourceKey<? extends Registry<?>>, TagCollection.NetworkPayload> serializeToNetwork(final RegistryAccess var1) {
      final HashMap var2 = Maps.newHashMap();
      this.getAll(new TagContainer.CollectionConsumer() {
         public <T> void accept(ResourceKey<? extends Registry<T>> var1x, TagCollection<T> var2x) {
            Optional var3 = var1.registry(var1x);
            if (var3.isPresent()) {
               var2.put(var1x, var2x.serializeToNetwork((Registry)var3.get()));
            } else {
               TagContainer.LOGGER.error("Unknown registry {}", var1x);
            }

         }
      });
      return var2;
   }

   public static TagContainer deserializeFromNetwork(RegistryAccess var0, Map<ResourceKey<? extends Registry<?>>, TagCollection.NetworkPayload> var1) {
      TagContainer.Builder var2 = new TagContainer.Builder();
      var1.forEach((var2x, var3) -> {
         addTagsFromPayload(var0, var2, var2x, var3);
      });
      return var2.build();
   }

   private static <T> void addTagsFromPayload(RegistryAccess var0, TagContainer.Builder var1, ResourceKey<? extends Registry<? extends T>> var2, TagCollection.NetworkPayload var3) {
      Optional var4 = var0.registry(var2);
      if (var4.isPresent()) {
         var1.add(var2, TagCollection.createFromNetwork(var3, (Registry)var4.get()));
      } else {
         LOGGER.error("Unknown registry {}", var2);
      }

   }

   @FunctionalInterface
   interface CollectionConsumer {
      <T> void accept(ResourceKey<? extends Registry<T>> var1, TagCollection<T> var2);
   }

   public static class Builder {
      private final com.google.common.collect.ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, TagCollection<?>> result = ImmutableMap.builder();

      public Builder() {
         super();
      }

      public <T> TagContainer.Builder add(ResourceKey<? extends Registry<? extends T>> var1, TagCollection<T> var2) {
         this.result.put(var1, var2);
         return this;
      }

      public TagContainer build() {
         return new TagContainer(this.result.build());
      }
   }
}
