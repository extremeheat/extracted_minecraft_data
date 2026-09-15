package net.minecraft.core.registries.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;

public class HolderSetCodec<E> implements Codec<HolderSet<E>> {
   private final ResourceKey<? extends Registry<E>> registryKey;
   private final Codec<Either<TagKey<E>, List<Holder<E>>>> tagKeyOrValuesCodec;

   private static <E> Codec<List<Holder<E>>> directCodec(final Codec<Holder<E>> elementCodec, final boolean alwaysUseList) {
      Codec<List<Holder<E>>> listCodec = elementCodec.listOf();
      return alwaysUseList ? listCodec : ExtraCodecs.compactListCodec(elementCodec, listCodec);
   }

   public static <E> Codec<HolderSet<E>> create(final ResourceKey<? extends Registry<E>> registryKey, final Codec<Holder<E>> elementCodec, final boolean alwaysUseList) {
      return new HolderSetCodec<HolderSet<E>>(registryKey, elementCodec, alwaysUseList);
   }

   private HolderSetCodec(final ResourceKey<? extends Registry<E>> registryKey, final Codec<Holder<E>> elementCodec, final boolean alwaysUseList) {
      super();
      this.registryKey = registryKey;
      this.tagKeyOrValuesCodec = Codec.either(TagKey.hashedCodec(registryKey), directCodec(elementCodec, alwaysUseList));
   }

   public <T> DataResult<Pair<HolderSet<E>, T>> decode(final DynamicOps<T> ops, final T input) {
      return this.tagKeyOrValuesCodec.decode(ops, input).flatMap((tagKeyOrValues) -> {
         DataResult<HolderSet<E>> result = (DataResult)((Either)tagKeyOrValues.getFirst()).map((tagKey) -> {
            if (ops instanceof RegistryOps<T> registryOps) {
               Optional<HolderGetter<E>> maybeRegistry = registryOps.getter(this.registryKey);
               return maybeRegistry.isPresent() ? lookupTag((HolderGetter)maybeRegistry.get(), tagKey) : DataResult.error(() -> "Registry " + String.valueOf(this.registryKey.identifier()) + " is not available in this context");
            } else {
               return DataResult.error(() -> "Registries are not available in this context");
            }
         }, (values) -> DataResult.success(HolderSet.direct(values)));
         return result.map((holders) -> Pair.of(holders, tagKeyOrValues.getSecond()));
      });
   }

   private static <E> DataResult<HolderSet<E>> lookupTag(final HolderGetter<E> registry, final TagKey<E> key) {
      return (DataResult)registry.get(key).map(DataResult::success).orElseGet(() -> DataResult.error(() -> {
            String var10000 = String.valueOf(key.location());
            return "Missing tag: '" + var10000 + "' in '" + String.valueOf(key.registry().identifier()) + "'";
         }));
   }

   public <T> DataResult<T> encode(final HolderSet<E> input, final DynamicOps<T> ops, final T prefix) {
      if (input instanceof HolderSet.Named<E> named) {
         if (ops instanceof RegistryOps<T> registryOps) {
            Optional<? extends HolderOwner<E>> maybeOwner = registryOps.getter(this.registryKey);
            if (maybeOwner.isPresent()) {
               if (!named.canSerializeIn((HolderOwner)maybeOwner.get())) {
                  return DataResult.error(() -> "HolderSet " + String.valueOf(named) + " is not valid in current registry set");
               }

               return this.tagKeyOrValuesCodec.encode(Either.left(named.key()), ops, prefix);
            }

            return DataResult.error(() -> "Registry " + String.valueOf(this.registryKey.identifier()) + " is not available in this context");
         }
      }

      return this.tagKeyOrValuesCodec.encode(Either.right(input.stream().toList()), ops, prefix);
   }
}
