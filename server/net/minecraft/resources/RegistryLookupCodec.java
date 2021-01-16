package net.minecraft.resources;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.stream.Stream;
import net.minecraft.core.Registry;

public final class RegistryLookupCodec<E> extends MapCodec<Registry<E>> {
   private final ResourceKey<? extends Registry<E>> registryKey;

   public static <E> RegistryLookupCodec<E> create(ResourceKey<? extends Registry<E>> var0) {
      return new RegistryLookupCodec(var0);
   }

   private RegistryLookupCodec(ResourceKey<? extends Registry<E>> var1) {
      super();
      this.registryKey = var1;
   }

   public <T> RecordBuilder<T> encode(Registry<E> var1, DynamicOps<T> var2, RecordBuilder<T> var3) {
      return var3;
   }

   public <T> DataResult<Registry<E>> decode(DynamicOps<T> var1, MapLike<T> var2) {
      return var1 instanceof RegistryReadOps ? ((RegistryReadOps)var1).registry(this.registryKey) : DataResult.error("Not a registry ops");
   }

   public String toString() {
      return "RegistryLookupCodec[" + this.registryKey + "]";
   }

   public <T> Stream<T> keys(DynamicOps<T> var1) {
      return Stream.empty();
   }

   // $FF: synthetic method
   public RecordBuilder encode(Object var1, DynamicOps var2, RecordBuilder var3) {
      return this.encode((Registry)var1, var2, var3);
   }
}
