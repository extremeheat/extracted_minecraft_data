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

public final class RegistryFixedCodec<E> implements Codec<Holder<E>> {
   private final ResourceKey<? extends Registry<E>> registryKey;

   public static <E> RegistryFixedCodec<E> create(final ResourceKey<? extends Registry<E>> registryKey) {
      return new RegistryFixedCodec<E>(registryKey);
   }

   private RegistryFixedCodec(final ResourceKey<? extends Registry<E>> registryKey) {
      super();
      this.registryKey = registryKey;
   }

   public <T> DataResult<T> encode(final Holder<E> input, final DynamicOps<T> ops, final T prefix) {
      if (ops instanceof RegistryOps<?> registryOps) {
         Optional<? extends HolderOwner<E>> maybeOwner = registryOps.getter(this.registryKey);
         if (maybeOwner.isPresent()) {
            if (!input.canSerializeIn((HolderOwner)maybeOwner.get())) {
               return DataResult.error(() -> "Element " + String.valueOf(input) + " is not valid in current registry set");
            }

            return (DataResult)input.unwrap().map((id) -> Identifier.CODEC.encode(id.identifier(), ops, prefix), (value) -> DataResult.error(() -> "Elements from registry " + String.valueOf(this.registryKey) + " can't be serialized to a value"));
         }
      }

      return DataResult.error(() -> "Can't access registry " + String.valueOf(this.registryKey));
   }

   public <T> DataResult<Pair<Holder<E>, T>> decode(final DynamicOps<T> ops, final T input) {
      if (ops instanceof RegistryOps<?> registryOps) {
         Optional<HolderGetter<E>> lookup = registryOps.getter(this.registryKey);
         if (lookup.isPresent()) {
            return Identifier.CODEC.decode(ops, input).flatMap((pair) -> {
               Identifier id = (Identifier)pair.getFirst();
               return ((DataResult)((HolderGetter)lookup.get()).get(ResourceKey.create(this.registryKey, id)).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Failed to get element " + String.valueOf(id)))).map((h) -> Pair.of(h, pair.getSecond())).setLifecycle(Lifecycle.stable());
            });
         }
      }

      return DataResult.error(() -> "Can't access registry " + String.valueOf(this.registryKey));
   }

   public String toString() {
      return "RegistryFixedCodec[" + String.valueOf(this.registryKey) + "]";
   }
}
