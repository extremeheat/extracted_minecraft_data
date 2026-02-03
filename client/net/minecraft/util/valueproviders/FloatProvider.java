package net.minecraft.util.valueproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.registries.BuiltInRegistries;

public abstract class FloatProvider implements SampledFloat {
   private static final Codec<Either<Float, FloatProvider>> CONSTANT_OR_DISPATCH_CODEC;
   public static final Codec<FloatProvider> CODEC;

   public FloatProvider() {
      super();
   }

   public static Codec<FloatProvider> codec(final float minValue, final float maxValue) {
      return CODEC.validate((value) -> {
         if (value.getMinValue() < minValue) {
            return DataResult.error(() -> "Value provider too low: " + minValue + " [" + value.getMinValue() + "-" + value.getMaxValue() + "]");
         } else {
            return value.getMaxValue() > maxValue ? DataResult.error(() -> "Value provider too high: " + maxValue + " [" + value.getMinValue() + "-" + value.getMaxValue() + "]") : DataResult.success(value);
         }
      });
   }

   public abstract float getMinValue();

   public abstract float getMaxValue();

   public abstract FloatProviderType<?> getType();

   static {
      CONSTANT_OR_DISPATCH_CODEC = Codec.either(Codec.FLOAT, BuiltInRegistries.FLOAT_PROVIDER_TYPE.byNameCodec().dispatch(FloatProvider::getType, FloatProviderType::codec));
      CODEC = CONSTANT_OR_DISPATCH_CODEC.xmap((either) -> (FloatProvider)either.map(ConstantFloat::of, (f) -> f), (f) -> f.getType() == FloatProviderType.CONSTANT ? Either.left(((ConstantFloat)f).getValue()) : Either.right(f));
   }
}
