package net.minecraft.util.valueproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class IntProviders {
   private static final Codec<Either<Integer, IntProvider>> CONSTANT_OR_DISPATCH_CODEC;
   public static final Codec<IntProvider> CODEC;
   public static final Codec<IntProvider> NON_NEGATIVE_CODEC;
   public static final Codec<IntProvider> POSITIVE_CODEC;

   public IntProviders() {
      super();
   }

   public static Codec<IntProvider> codec(final int minValue, final int maxValue) {
      return validateCodec(minValue, maxValue, CODEC);
   }

   public static <T extends IntProvider> Codec<T> validateCodec(final int minValue, final int maxValue, final Codec<T> codec) {
      return codec.validate((value) -> validate(minValue, maxValue, value));
   }

   private static <T extends IntProvider> DataResult<T> validate(final int minValue, final int maxValue, final T value) {
      if (value.minInclusive() < minValue) {
         return DataResult.error(() -> "Value provider too low: " + minValue + " [" + value.minInclusive() + "-" + value.maxInclusive() + "]");
      } else {
         return value.maxInclusive() > maxValue ? DataResult.error(() -> "Value provider too high: " + maxValue + " [" + value.minInclusive() + "-" + value.maxInclusive() + "]") : DataResult.success(value);
      }
   }

   public static MapCodec<? extends IntProvider> bootstrap(final Registry<MapCodec<? extends IntProvider>> registry) {
      Registry.register(registry, (String)"constant", ConstantInt.MAP_CODEC);
      Registry.register(registry, (String)"uniform", UniformInt.MAP_CODEC);
      Registry.register(registry, (String)"biased_to_bottom", BiasedToBottomInt.MAP_CODEC);
      Registry.register(registry, (String)"very_biased_to_bottom", VeryBiasedToBottomInt.MAP_CODEC);
      Registry.register(registry, (String)"clamped", ClampedInt.MAP_CODEC);
      Registry.register(registry, (String)"weighted_list", WeightedListInt.MAP_CODEC);
      Registry.register(registry, (String)"clamped_normal", ClampedNormalInt.MAP_CODEC);
      return (MapCodec)Registry.register(registry, (String)"trapezoid", TrapezoidInt.MAP_CODEC);
   }

   static {
      CONSTANT_OR_DISPATCH_CODEC = Codec.either(Codec.INT, BuiltInRegistries.INT_PROVIDER_TYPE.byNameCodec().dispatch(IntProvider::codec, (t) -> t));
      CODEC = CONSTANT_OR_DISPATCH_CODEC.xmap((either) -> (IntProvider)either.map(ConstantInt::of, (f) -> f), (f) -> {
         Either var10000;
         if (f instanceof ConstantInt constantInt) {
            var10000 = Either.left(constantInt.value());
         } else {
            var10000 = Either.right(f);
         }

         return var10000;
      });
      NON_NEGATIVE_CODEC = codec(0, 2147483647);
      POSITIVE_CODEC = codec(1, 2147483647);
   }
}
