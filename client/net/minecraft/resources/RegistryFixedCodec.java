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
      return new RegistryFixedCodec<>(var0);
   }

   private RegistryFixedCodec(ResourceKey<? extends Registry<E>> var1) {
      super();
      this.registryKey = var1;
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public <T> DataResult<T> encode(Holder<E> var1, DynamicOps<T> var2, T var3) {
      if (var2 instanceof RegistryOps var4) {
         Optional var5 = var4.registry(this.registryKey);
         if (var5.isPresent()) {
            if (!var1.isValidInRegistry((Registry<T>)var5.get())) {
               return DataResult.error("Element " + var1 + " is not valid in current registry set");
            }

            return (DataResult<T>)var1.unwrap()
               .map(
                  var2x -> ResourceLocation.CODEC.encode(var2x.location(), var2, var3),
                  var1x -> DataResult.error("Elements from registry " + this.registryKey + " can't be serialized to a value")
               );
         }
      }

      return DataResult.error("Can't access registry " + this.registryKey);
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public <T> DataResult<Pair<Holder<E>, T>> decode(DynamicOps<T> var1, T var2) {
      if (var1 instanceof RegistryOps var3) {
         Optional var4 = var3.registry(this.registryKey);
         if (var4.isPresent()) {
            return ResourceLocation.CODEC.decode(var1, var2).flatMap(var2x -> {
               ResourceLocation var3x = (ResourceLocation)var2x.getFirst();
               DataResult var4x = ((Registry)var4.get()).getOrCreateHolder(ResourceKey.create(this.registryKey, var3x));
               return var4x.map(var1xx -> Pair.of(var1xx, var2x.getSecond())).setLifecycle(Lifecycle.stable());
            });
         }
      }

      return DataResult.error("Can't access registry " + this.registryKey);
   }

   @Override
   public String toString() {
      return "RegistryFixedCodec[" + this.registryKey + "]";
   }
}