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
   private static final ConcurrentMap<ResourceKey.InternKey, ResourceKey<?>> VALUES = new MapMaker().weakValues().makeMap();
   private final ResourceLocation registryName;
   private final ResourceLocation location;

   public static <T> Codec<ResourceKey<T>> codec(ResourceKey<? extends Registry<T>> var0) {
      return ResourceLocation.CODEC.xmap(var1 -> create(var0, var1), ResourceKey::location);
   }

   public static <T> StreamCodec<ByteBuf, ResourceKey<T>> streamCodec(ResourceKey<? extends Registry<T>> var0) {
      return ResourceLocation.STREAM_CODEC.map(var1 -> create(var0, var1), ResourceKey::location);
   }

   public static <T> ResourceKey<T> create(ResourceKey<? extends Registry<T>> var0, ResourceLocation var1) {
      return create(var0.location, var1);
   }

   public static <T> ResourceKey<Registry<T>> createRegistryKey(ResourceLocation var0) {
      return create(Registries.ROOT_REGISTRY_NAME, var0);
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
      return this.isFor(var1) ? Optional.of((ResourceKey<E>)this) : Optional.empty();
   }

   public ResourceLocation location() {
      return this.location;
   }

   public ResourceLocation registry() {
      return this.registryName;
   }

   public ResourceKey<Registry<T>> registryKey() {
      return createRegistryKey(this.registryName);
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
