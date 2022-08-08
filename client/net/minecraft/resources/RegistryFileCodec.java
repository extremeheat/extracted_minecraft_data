package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;

public final class RegistryFileCodec<E> implements Codec<Holder<E>> {
   private final ResourceKey<? extends Registry<E>> registryKey;
   private final Codec<E> elementCodec;
   private final boolean allowInline;

   public static <E> RegistryFileCodec<E> create(ResourceKey<? extends Registry<E>> var0, Codec<E> var1) {
      return create(var0, var1, true);
   }

   public static <E> RegistryFileCodec<E> create(ResourceKey<? extends Registry<E>> var0, Codec<E> var1, boolean var2) {
      return new RegistryFileCodec(var0, var1, var2);
   }

   private RegistryFileCodec(ResourceKey<? extends Registry<E>> var1, Codec<E> var2, boolean var3) {
      super();
      this.registryKey = var1;
      this.elementCodec = var2;
      this.allowInline = var3;
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
            }, (var3x) -> {
               return this.elementCodec.encode(var3x, var2, var3);
            });
         }
      }

      return this.elementCodec.encode(var1.value(), var2, var3);
   }

   public <T> DataResult<Pair<Holder<E>, T>> decode(DynamicOps<T> var1, T var2) {
      if (var1 instanceof RegistryOps var3) {
         Optional var4 = var3.registry(this.registryKey);
         if (var4.isEmpty()) {
            return DataResult.error("Registry does not exist: " + this.registryKey);
         } else {
            Registry var5 = (Registry)var4.get();
            DataResult var6 = ResourceLocation.CODEC.decode(var1, var2);
            if (var6.result().isEmpty()) {
               return !this.allowInline ? DataResult.error("Inline definitions not allowed here") : this.elementCodec.decode(var1, var2).map((var0) -> {
                  return var0.mapFirst(Holder::direct);
               });
            } else {
               Pair var7 = (Pair)var6.result().get();
               ResourceKey var8 = ResourceKey.create(this.registryKey, (ResourceLocation)var7.getFirst());
               Optional var9 = var3.registryLoader();
               if (var9.isPresent()) {
                  return ((RegistryLoader.Bound)var9.get()).overrideElementFromResources(this.registryKey, this.elementCodec, var8, var3.getAsJson()).map((var1x) -> {
                     return Pair.of(var1x, var7.getSecond());
                  });
               } else {
                  DataResult var10 = var5.getOrCreateHolder(var8);
                  return var10.map((var1x) -> {
                     return Pair.of(var1x, var7.getSecond());
                  }).setLifecycle(Lifecycle.stable());
               }
            }
         }
      } else {
         return this.elementCodec.decode(var1, var2).map((var0) -> {
            return var0.mapFirst(Holder::direct);
         });
      }
   }

   public String toString() {
      return "RegistryFileCodec[" + this.registryKey + " " + this.elementCodec + "]";
   }

   // $FF: synthetic method
   public DataResult encode(Object var1, DynamicOps var2, Object var3) {
      return this.encode((Holder)var1, var2, var3);
   }
}
