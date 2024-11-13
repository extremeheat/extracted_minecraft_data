package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;

public final class RegistryFileCodec<E> implements Codec<Holder<E>> {
   private final ResourceKey<? extends Registry<E>> registryKey;
   private final Codec<E> elementCodec;
   private final boolean allowInline;

   public static <E> RegistryFileCodec<E> create(ResourceKey<? extends Registry<E>> var0, Codec<E> var1) {
      return create(var0, var1, true);
   }

   public static <E> RegistryFileCodec<E> create(ResourceKey<? extends Registry<E>> var0, Codec<E> var1, boolean var2) {
      return new RegistryFileCodec<E>(var0, var1, var2);
   }

   private RegistryFileCodec(ResourceKey<? extends Registry<E>> var1, Codec<E> var2, boolean var3) {
      super();
      this.registryKey = var1;
      this.elementCodec = var2;
      this.allowInline = var3;
   }

   public <T> DataResult<T> encode(Holder<E> var1, DynamicOps<T> var2, T var3) {
      if (var2 instanceof RegistryOps var4) {
         Optional var5 = var4.owner(this.registryKey);
         if (var5.isPresent()) {
            if (!var1.canSerializeIn((HolderOwner)var5.get())) {
               return DataResult.error(() -> "Element " + String.valueOf(var1) + " is not valid in current registry set");
            }

            return (DataResult)var1.unwrap().map((var2x) -> ResourceLocation.CODEC.encode(var2x.location(), var2, var3), (var3x) -> this.elementCodec.encode(var3x, var2, var3));
         }
      }

      return this.elementCodec.encode(var1.value(), var2, var3);
   }

   public <T> DataResult<Pair<Holder<E>, T>> decode(DynamicOps<T> var1, T var2) {
      if (var1 instanceof RegistryOps var3) {
         Optional var4 = var3.getter(this.registryKey);
         if (var4.isEmpty()) {
            return DataResult.error(() -> "Registry does not exist: " + String.valueOf(this.registryKey));
         } else {
            HolderGetter var5 = (HolderGetter)var4.get();
            DataResult var6 = ResourceLocation.CODEC.decode(var1, var2);
            if (var6.result().isEmpty()) {
               return !this.allowInline ? DataResult.error(() -> "Inline definitions not allowed here") : this.elementCodec.decode(var1, var2).map((var0) -> var0.mapFirst(Holder::direct));
            } else {
               Pair var7 = (Pair)var6.result().get();
               ResourceKey var8 = ResourceKey.create(this.registryKey, (ResourceLocation)var7.getFirst());
               return ((DataResult)var5.get(var8).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Failed to get element " + String.valueOf(var8)))).map((var1x) -> Pair.of(var1x, var7.getSecond())).setLifecycle(Lifecycle.stable());
            }
         }
      } else {
         return this.elementCodec.decode(var1, var2).map((var0) -> var0.mapFirst(Holder::direct));
      }
   }

   public String toString() {
      String var10000 = String.valueOf(this.registryKey);
      return "RegistryFileCodec[" + var10000 + " " + String.valueOf(this.elementCodec) + "]";
   }

   // $FF: synthetic method
   public DataResult encode(final Object var1, final DynamicOps var2, final Object var3) {
      return this.encode((Holder)var1, var2, var3);
   }
}
