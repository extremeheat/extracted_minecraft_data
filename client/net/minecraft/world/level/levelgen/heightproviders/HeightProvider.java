package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public abstract class HeightProvider {
   private static final Codec<Either<VerticalAnchor, HeightProvider>> CONSTANT_OR_DISPATCH_CODEC = Codec.either(
      VerticalAnchor.CODEC, Registry.HEIGHT_PROVIDER_TYPES.byNameCodec().dispatch(HeightProvider::getType, HeightProviderType::codec)
   );
   public static final Codec<HeightProvider> CODEC = CONSTANT_OR_DISPATCH_CODEC.xmap(
      var0 -> (HeightProvider)var0.map(ConstantHeight::of, var0x -> var0x),
      var0 -> var0.getType() == HeightProviderType.CONSTANT ? Either.left(((ConstantHeight)var0).getValue()) : Either.right(var0)
   );

   public HeightProvider() {
      super();
   }

   public abstract int sample(RandomSource var1, WorldGenerationContext var2);

   public abstract HeightProviderType<?> getType();
}