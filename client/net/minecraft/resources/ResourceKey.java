package net.minecraft.resources;

import com.google.common.collect.MapMaker;
import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class ResourceKey<T> {
   private static final ConcurrentMap<ResourceKey.InternKey, ResourceKey<?>> VALUES = new MapMaker().weakValues().makeMap();
   private final ResourceLocation registryName;
   private final ResourceLocation location;

   public static <T> Codec<ResourceKey<T>> codec(ResourceKey<? extends Registry<T>> var0) {
      return ResourceLocation.CODEC.xmap(var1 -> create(var0, var1), ResourceKey::location);
   }

   public static <T> ResourceKey<T> create(ResourceKey<? extends Registry<T>> var0, ResourceLocation var1) {
      return create(var0.location, var1);
   }

   public static <T> ResourceKey<Registry<T>> createRegistryKey(ResourceLocation var0) {
      return create(BuiltInRegistries.ROOT_REGISTRY_NAME, var0);
   }

   private static <T> ResourceKey<T> create(ResourceLocation var0, ResourceLocation var1) {
      return (ResourceKey<T>)VALUES.computeIfAbsent(new ResourceKey.InternKey(var0, var1), var0x -> new ResourceKey(var0x.registry, var0x.location));
   }

   private ResourceKey(ResourceLocation var1, ResourceLocation var2) {
      super();
      this.registryName = var1;
      this.location = var2;
   }

   @Override
   public String toString() {
      return "ResourceKey[" + this.registryName + " / " + this.location + "]";
   }

   public boolean isFor(ResourceKey<? extends Registry<?>> var1) {
      return this.registryName.equals(var1.location());
   }

   public <E> Optional<ResourceKey<E>> cast(ResourceKey<? extends Registry<E>> var1) {
      return this.isFor(var1) ? Optional.of(this) : Optional.empty();
   }

   public ResourceLocation location() {
      return this.location;
   }

   public ResourceLocation registry() {
      return this.registryName;
   }

   static record InternKey(ResourceLocation a, ResourceLocation b) {
      final ResourceLocation registry;
      final ResourceLocation location;

      InternKey(ResourceLocation var1, ResourceLocation var2) {
         super();
         this.registry = var1;
         this.location = var2;
      }
   }
}
