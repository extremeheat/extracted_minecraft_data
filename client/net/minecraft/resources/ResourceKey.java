package net.minecraft.resources;

import com.google.common.collect.MapMaker;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;

public class ResourceKey<T> {
   private static final ConcurrentMap<InternKey, ResourceKey<?>> VALUES = (new MapMaker()).weakValues().makeMap();
   private final Identifier registryName;
   private final Identifier identifier;

   public static <T> Codec<ResourceKey<T>> codec(ResourceKey<? extends Registry<T>> var0) {
      return Identifier.CODEC.xmap((var1) -> create(var0, var1), ResourceKey::identifier);
   }

   public static <T> StreamCodec<ByteBuf, ResourceKey<T>> streamCodec(ResourceKey<? extends Registry<T>> var0) {
      return Identifier.STREAM_CODEC.map((var1) -> create(var0, var1), ResourceKey::identifier);
   }

   public static <T> ResourceKey<T> create(ResourceKey<? extends Registry<T>> var0, Identifier var1) {
      return create(var0.identifier, var1);
   }

   public static <T> ResourceKey<Registry<T>> createRegistryKey(Identifier var0) {
      return create(Registries.ROOT_REGISTRY_NAME, var0);
   }

   private static <T> ResourceKey<T> create(Identifier var0, Identifier var1) {
      return (ResourceKey)VALUES.computeIfAbsent(new InternKey(var0, var1), (var0x) -> new ResourceKey(var0x.registry, var0x.identifier));
   }

   private ResourceKey(Identifier var1, Identifier var2) {
      super();
      this.registryName = var1;
      this.identifier = var2;
   }

   public String toString() {
      String var10000 = String.valueOf(this.registryName);
      return "ResourceKey[" + var10000 + " / " + String.valueOf(this.identifier) + "]";
   }

   public boolean isFor(ResourceKey<? extends Registry<?>> var1) {
      return this.registryName.equals(var1.identifier());
   }

   public <E> Optional<ResourceKey<E>> cast(ResourceKey<? extends Registry<E>> var1) {
      return this.isFor(var1) ? Optional.of(this) : Optional.empty();
   }

   public Identifier identifier() {
      return this.identifier;
   }

   public Identifier registry() {
      return this.registryName;
   }

   public ResourceKey<Registry<T>> registryKey() {
      return createRegistryKey(this.registryName);
   }

   static record InternKey(Identifier registry, Identifier identifier) {
      final Identifier registry;
      final Identifier identifier;

      InternKey(Identifier var1, Identifier var2) {
         super();
         this.registry = var1;
         this.identifier = var2;
      }
   }
}
