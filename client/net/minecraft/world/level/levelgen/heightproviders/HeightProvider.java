package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public abstract class HeightProvider {
   private static final Codec<Either<VerticalAnchor, HeightProvider>> CONSTANT_OR_DISPATCH_CODEC;
   public static final Codec<HeightProvider> CODEC;

   public HeightProvider() {
      super();
   }

   public abstract int sample(RandomSource var1, WorldGenerationContext var2);

   public abstract HeightProviderType<?> getType();

   static {
      CONSTANT_OR_DISPATCH_CODEC = Codec.either(VerticalAnchor.CODEC, BuiltInRegistries.HEIGHT_PROVIDER_TYPE.byNameCodec().dispatch(HeightProvider::getType, HeightProviderType::codec));
      CODEC = CONSTANT_OR_DISPATCH_CODEC.xmap((var0) -> {
         return (HeightProvider)var0.map(ConstantHeight::of, (var0x) -> {
            return var0x;
         });
      }, (var0) -> {
         return var0.getType() == HeightProviderType.CONSTANT ? Either.left(((ConstantHeight)var0).getValue()) : Either.right(var0);
      });
   }
}
