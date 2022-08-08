package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;

public final class RegistryFixedCodec<E> implements Codec<Holder<E>> {
   private final ResourceKey<? extends Registry<E>> registryKey;

   public static <E> RegistryFixedCodec<E> create(ResourceKey<? extends Registry<E>> var0) {
      return new RegistryFixedCodec(var0);
   }

   private RegistryFixedCodec(ResourceKey<? extends Registry<E>> var1) {
      super();
      this.registryKey = var1;
   }

   public <T> DataResult<T> encode(Holder<E> var1, DynamicOps<T> var2, T var3) {
      if (var2 instanceof RegistryOps var4) {
         Optional var5 = var4.registry(this.registryKey);
         if (var5.isPresent()) {
            if (!var1.isValidInRegistry((Registry)var5.get())) {
               return DataResult.error("Element " + var1 + " is not valid in current registry set");
            }

            return (DataResult)var1.unwrap().map((var2x) -> {
               return ResourceLocation.CODEC.encode(var2x.location(), var2, var3);
            }, (var1x) -> {
               return DataResult.error("Elements from registry " + this.registryKey + " can't be serialized to a value");
            });
         }
      }

      return DataResult.error("Can't access registry " + this.registryKey);
   }

   public <T> DataResult<Pair<Holder<E>, T>> decode(DynamicOps<T> var1, T var2) {
      if (var1 instanceof RegistryOps var3) {
         Optional var4 = var3.registry(this.registryKey);
         if (var4.isPresent()) {
            return ResourceLocation.CODEC.decode(var1, var2).flatMap((var2x) -> {
               ResourceLocation var3 = (ResourceLocation)var2x.getFirst();
               DataResult var4x = ((Registry)var4.get()).getOrCreateHolder(ResourceKey.create(this.registryKey, var3));
               return var4x.map((var1) -> {
                  return Pair.of(var1, var2x.getSecond());
               }).setLifecycle(Lifecycle.stable());
            });
         }
      }

      return DataResult.error("Can't access registry " + this.registryKey);
   }

   public String toString() {
      return "RegistryFixedCodec[" + this.registryKey + "]";
   }

   // $FF: synthetic method
   public DataResult encode(Object var1, DynamicOps var2, Object var3) {
      return this.encode((Holder)var1, var2, var3);
   }
}
