package net.minecraft.util.valueproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.Registry;

public abstract class IntProvider {
   private static final Codec<Either<Integer, IntProvider>> CONSTANT_OR_DISPATCH_CODEC;
   public static final Codec<IntProvider> CODEC;
   public static final Codec<IntProvider> NON_NEGATIVE_CODEC;
   public static final Codec<IntProvider> POSITIVE_CODEC;

   public IntProvider() {
      super();
   }

   public static Codec<IntProvider> codec(int var0, int var1) {
      Function var2 = (var2x) -> {
         if (var2x.getMinValue() < var0) {
            return DataResult.error("Value provider too low: " + var0 + " [" + var2x.getMinValue() + "-" + var2x.getMaxValue() + "]");
         } else {
            return var2x.getMaxValue() > var1 ? DataResult.error("Value provider too high: " + var1 + " [" + var2x.getMinValue() + "-" + var2x.getMaxValue() + "]") : DataResult.success(var2x);
         }
      };
      return CODEC.flatXmap(var2, var2);
   }

   public abstract int sample(Random var1);

   public abstract int getMinValue();

   public abstract int getMaxValue();

   public abstract IntProviderType<?> getType();

   static {
      CONSTANT_OR_DISPATCH_CODEC = Codec.either(Codec.INT, Registry.INT_PROVIDER_TYPES.byNameCodec().dispatch(IntProvider::getType, IntProviderType::codec));
      CODEC = CONSTANT_OR_DISPATCH_CODEC.xmap((var0) -> {
         return (IntProvider)var0.map(ConstantInt::of, (var0x) -> {
            return var0x;
         });
      }, (var0) -> {
         return var0.getType() == IntProviderType.CONSTANT ? Either.left(((ConstantInt)var0).getValue()) : Either.right(var0);
      });
      NON_NEGATIVE_CODEC = codec(0, 2147483647);
      POSITIVE_CODEC = codec(1, 2147483647);
   }
}
