package net.minecraft.util.valueproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;

public abstract class FloatProvider implements SampledFloat {
   private static final Codec<Either<Float, FloatProvider>> CONSTANT_OR_DISPATCH_CODEC = Codec.either(
      Codec.FLOAT, BuiltInRegistries.FLOAT_PROVIDER_TYPE.byNameCodec().dispatch(FloatProvider::getType, FloatProviderType::codec)
   );
   public static final Codec<FloatProvider> CODEC = CONSTANT_OR_DISPATCH_CODEC.xmap(
      var0 -> (FloatProvider)var0.map(ConstantFloat::of, var0x -> var0x),
      var0 -> var0.getType() == FloatProviderType.CONSTANT ? Either.left(((ConstantFloat)var0).getValue()) : Either.right(var0)
   );

   public FloatProvider() {
      super();
   }

   public static Codec<FloatProvider> codec(float var0, float var1) {
      return ExtraCodecs.validate(
         CODEC,
         var2 -> {
            if (var2.getMinValue() < var0) {
               return DataResult.error("Value provider too low: " + var0 + " [" + var2.getMinValue() + "-" + var2.getMaxValue() + "]");
            } else {
               return var2.getMaxValue() > var1
                  ? DataResult.error("Value provider too high: " + var1 + " [" + var2.getMinValue() + "-" + var2.getMaxValue() + "]")
                  : DataResult.success(var2);
            }
         }
      );
   }

   public abstract float getMinValue();

   public abstract float getMaxValue();

   public abstract FloatProviderType<?> getType();
}
