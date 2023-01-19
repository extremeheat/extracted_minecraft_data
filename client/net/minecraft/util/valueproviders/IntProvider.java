package net.minecraft.util.valueproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

public abstract class IntProvider {
   private static final Codec<Either<Integer, IntProvider>> CONSTANT_OR_DISPATCH_CODEC = Codec.either(
      Codec.INT, BuiltInRegistries.INT_PROVIDER_TYPE.byNameCodec().dispatch(IntProvider::getType, IntProviderType::codec)
   );
   public static final Codec<IntProvider> CODEC = CONSTANT_OR_DISPATCH_CODEC.xmap(
      var0 -> (IntProvider)var0.map(ConstantInt::of, var0x -> var0x),
      var0 -> var0.getType() == IntProviderType.CONSTANT ? Either.left(((ConstantInt)var0).getValue()) : Either.right(var0)
   );
   public static final Codec<IntProvider> NON_NEGATIVE_CODEC = codec(0, 2147483647);
   public static final Codec<IntProvider> POSITIVE_CODEC = codec(1, 2147483647);

   public IntProvider() {
      super();
   }

   public static Codec<IntProvider> codec(int var0, int var1) {
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

   public abstract int sample(RandomSource var1);

   public abstract int getMinValue();

   public abstract int getMaxValue();

   public abstract IntProviderType<?> getType();
}
