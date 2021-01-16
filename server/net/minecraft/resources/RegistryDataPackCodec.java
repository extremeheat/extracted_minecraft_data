package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;

public final class RegistryDataPackCodec<E> implements Codec<MappedRegistry<E>> {
   private final Codec<MappedRegistry<E>> directCodec;
   private final ResourceKey<? extends Registry<E>> registryKey;
   private final Codec<E> elementCodec;

   public static <E> RegistryDataPackCodec<E> create(ResourceKey<? extends Registry<E>> var0, Lifecycle var1, Codec<E> var2) {
      return new RegistryDataPackCodec(var0, var1, var2);
   }

   private RegistryDataPackCodec(ResourceKey<? extends Registry<E>> var1, Lifecycle var2, Codec<E> var3) {
      super();
      this.directCodec = MappedRegistry.directCodec(var1, var2, var3);
      this.registryKey = var1;
      this.elementCodec = var3;
   }

   public <T> DataResult<T> encode(MappedRegistry<E> var1, DynamicOps<T> var2, T var3) {
      return this.directCodec.encode(var1, var2, var3);
   }

   public <T> DataResult<Pair<MappedRegistry<E>, T>> decode(DynamicOps<T> var1, T var2) {
      DataResult var3 = this.directCodec.decode(var1, var2);
      return var1 instanceof RegistryReadOps ? var3.flatMap((var2x) -> {
         return ((RegistryReadOps)var1).decodeElements((MappedRegistry)var2x.getFirst(), this.registryKey, this.elementCodec).map((var1x) -> {
            return Pair.of(var1x, var2x.getSecond());
         });
      }) : var3;
   }

   public String toString() {
      return "RegistryDataPackCodec[" + this.directCodec + " " + this.registryKey + " " + this.elementCodec + "]";
   }

   // $FF: synthetic method
   public DataResult encode(Object var1, DynamicOps var2, Object var3) {
      return this.encode((MappedRegistry)var1, var2, var3);
   }
}
