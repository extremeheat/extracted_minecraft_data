package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;

public abstract class HeightProvider {
   private static final Codec<Either<VerticalAnchor, HeightProvider>> CONSTANT_OR_DISPATCH_CODEC;
   public static final Codec<HeightProvider> CODEC;

   public HeightProvider() {
      super();
   }

   public abstract int sample(RandomSource random, VerticalAnchor.Context anchorContext);

   public abstract HeightProviderType<?> getType();

   static {
      CONSTANT_OR_DISPATCH_CODEC = Codec.either(VerticalAnchor.CODEC, BuiltInRegistries.HEIGHT_PROVIDER_TYPE.byNameCodec().dispatch(HeightProvider::getType, HeightProviderType::codec));
      CODEC = CONSTANT_OR_DISPATCH_CODEC.xmap((either) -> (HeightProvider)either.map(ConstantHeight::of, (f) -> f), (f) -> f.getType() == HeightProviderType.CONSTANT ? Either.left(((ConstantHeight)f).getValue()) : Either.right(f));
   }
}
