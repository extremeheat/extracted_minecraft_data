package net.minecraft.world.item;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

public record EitherHolder<T>(Either<Holder<T>, ResourceKey<T>> contents) {
   public EitherHolder(Holder<T> var1) {
      this(Either.left(var1));
   }

   public EitherHolder(ResourceKey<T> var1) {
      this(Either.right(var1));
   }

   public EitherHolder(Either<Holder<T>, ResourceKey<T>> var1) {
      super();
      this.contents = var1;
   }

   public static <T> Codec<EitherHolder<T>> codec(ResourceKey<Registry<T>> var0, Codec<Holder<T>> var1) {
      return Codec.either(var1, ResourceKey.codec(var0).comapFlatMap((var0x) -> DataResult.error(() -> "Cannot parse as key without registry"), Function.identity())).xmap(EitherHolder::new, EitherHolder::contents);
   }

   public static <T> StreamCodec<RegistryFriendlyByteBuf, EitherHolder<T>> streamCodec(ResourceKey<Registry<T>> var0, StreamCodec<RegistryFriendlyByteBuf, Holder<T>> var1) {
      return StreamCodec.composite(ByteBufCodecs.either(var1, ResourceKey.streamCodec(var0)), EitherHolder::contents, EitherHolder::new);
   }

   public Optional<T> unwrap(Registry<T> var1) {
      Either var10000 = this.contents;
      Function var10001 = (var0) -> Optional.of(var0.value());
      Objects.requireNonNull(var1);
      return (Optional)var10000.map(var10001, var1::getOptional);
   }

   public Optional<Holder<T>> unwrap(HolderLookup.Provider var1) {
      return (Optional)this.contents.map(Optional::of, (var1x) -> var1.get(var1x).map((var0) -> var0));
   }

   public Optional<ResourceKey<T>> key() {
      return (Optional)this.contents.map(Holder::unwrapKey, Optional::of);
   }
}
