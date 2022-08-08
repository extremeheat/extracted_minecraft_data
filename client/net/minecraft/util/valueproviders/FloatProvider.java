package net.minecraft.util.valueproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;
import net.minecraft.core.Registry;

public abstract class FloatProvider implements SampledFloat {
   private static final Codec<Either<Float, FloatProvider>> CONSTANT_OR_DISPATCH_CODEC;
   public static final Codec<FloatProvider> CODEC;

   public FloatProvider() {
      super();
   }

   public static Codec<FloatProvider> codec(float var0, float var1) {
      Function var2 = (var2x) -> {
         if (var2x.getMinValue() < var0) {
            return DataResult.error("Value provider too low: " + var0 + " [" + var2x.getMinValue() + "-" + var2x.getMaxValue() + "]");
         } else {
            return var2x.getMaxValue() > var1 ? DataResult.error("Value provider too high: " + var1 + " [" + var2x.getMinValue() + "-" + var2x.getMaxValue() + "]") : DataResult.success(var2x);
         }
      };
      return CODEC.flatXmap(var2, var2);
   }

   public abstract float getMinValue();

   public abstract float getMaxValue();

   public abstract FloatProviderType<?> getType();

   static {
      CONSTANT_OR_DISPATCH_CODEC = Codec.either(Codec.FLOAT, Registry.FLOAT_PROVIDER_TYPES.byNameCodec().dispatch(FloatProvider::getType, FloatProviderType::codec));
      CODEC = CONSTANT_OR_DISPATCH_CODEC.xmap((var0) -> {
         return (FloatProvider)var0.map(ConstantFloat::of, (var0x) -> {
            return var0x;
         });
      }, (var0) -> {
         return var0.getType() == FloatProviderType.CONSTANT ? Either.left(((ConstantFloat)var0).getValue()) : Either.right(var0);
      });
   }
}
