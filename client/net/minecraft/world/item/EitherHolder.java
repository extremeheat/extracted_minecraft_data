package net.minecraft.world.item;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

public record EitherHolder<T>(Optional<Holder<T>> holder, ResourceKey<T> key) {
   public EitherHolder(Holder<T> var1) {
      this(Optional.of(var1), (ResourceKey)var1.unwrapKey().orElseThrow());
   }

   public EitherHolder(ResourceKey<T> var1) {
      this(Optional.empty(), var1);
   }

   public EitherHolder(Optional<Holder<T>> var1, ResourceKey<T> var2) {
      super();
      this.holder = var1;
      this.key = var2;
   }

   public static <T> Codec<EitherHolder<T>> codec(ResourceKey<Registry<T>> var0, Codec<Holder<T>> var1) {
      return Codec.either(var1, ResourceKey.codec(var0).comapFlatMap((var0x) -> DataResult.error(() -> "Cannot parse as key without registry"), Function.identity())).xmap(EitherHolder::fromEither, EitherHolder::asEither);
   }

   public static <T> StreamCodec<RegistryFriendlyByteBuf, EitherHolder<T>> streamCodec(ResourceKey<Registry<T>> var0, StreamCodec<RegistryFriendlyByteBuf, Holder<T>> var1) {
      return StreamCodec.composite(ByteBufCodecs.either(var1, ResourceKey.streamCodec(var0)), EitherHolder::asEither, EitherHolder::fromEither);
   }

   public Either<Holder<T>, ResourceKey<T>> asEither() {
      return (Either)this.holder.map(Either::left).orElseGet(() -> Either.right(this.key));
   }

   public static <T> EitherHolder<T> fromEither(Either<Holder<T>, ResourceKey<T>> var0) {
      return (EitherHolder)var0.map(EitherHolder::new, EitherHolder::new);
   }

   public Optional<T> unwrap(Registry<T> var1) {
      return this.holder.map(Holder::value).or(() -> var1.getOptional(this.key));
   }

   public Optional<Holder<T>> unwrap(HolderLookup.Provider var1) {
      return this.holder.or(() -> var1.lookupOrThrow(this.key.registryKey()).get(this.key));
   }
}
