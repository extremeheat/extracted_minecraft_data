package net.minecraft.util.valueproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;

public abstract class IntProvider {
   private static final Codec<Either<Integer, IntProvider>> CONSTANT_OR_DISPATCH_CODEC;
   public static final Codec<IntProvider> CODEC;
   public static final Codec<IntProvider> NON_NEGATIVE_CODEC;
   public static final Codec<IntProvider> POSITIVE_CODEC;

   public IntProvider() {
      super();
   }

   public static Codec<IntProvider> codec(final int minValue, final int maxValue) {
      return validateCodec(minValue, maxValue, CODEC);
   }

   public static <T extends IntProvider> Codec<T> validateCodec(final int minValue, final int maxValue, final Codec<T> codec) {
      return codec.validate((value) -> validate(minValue, maxValue, value));
   }

   private static <T extends IntProvider> DataResult<T> validate(final int minValue, final int maxValue, final T value) {
      if (value.getMinValue() < minValue) {
         return DataResult.error(() -> "Value provider too low: " + minValue + " [" + value.getMinValue() + "-" + value.getMaxValue() + "]");
      } else {
         return value.getMaxValue() > maxValue ? DataResult.error(() -> "Value provider too high: " + maxValue + " [" + value.getMinValue() + "-" + value.getMaxValue() + "]") : DataResult.success(value);
      }
   }

   public abstract int sample(final RandomSource random);

   public abstract int getMinValue();

   public abstract int getMaxValue();

   public abstract IntProviderType<?> getType();

   static {
      CONSTANT_OR_DISPATCH_CODEC = Codec.either(Codec.INT, BuiltInRegistries.INT_PROVIDER_TYPE.byNameCodec().dispatch(IntProvider::getType, IntProviderType::codec));
      CODEC = CONSTANT_OR_DISPATCH_CODEC.xmap((either) -> (IntProvider)either.map(ConstantInt::of, (f) -> f), (f) -> f.getType() == IntProviderType.CONSTANT ? Either.left(((ConstantInt)f).getValue()) : Either.right(f));
      NON_NEGATIVE_CODEC = codec(0, 2147483647);
      POSITIVE_CODEC = codec(1, 2147483647);
   }
}
