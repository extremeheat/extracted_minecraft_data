package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;

public class ConstantInt extends IntProvider {
   public static final ConstantInt ZERO = new ConstantInt(0);
   public static final MapCodec<ConstantInt> CODEC = Codec.INT.fieldOf("value").xmap(ConstantInt::of, ConstantInt::getValue);
   private final int value;

   public static ConstantInt of(int var0) {
      return var0 == 0 ? ZERO : new ConstantInt(var0);
   }

   private ConstantInt(int var1) {
      super();
      this.value = var1;
   }

   public int getValue() {
      return this.value;
   }

   @Override
   public int sample(RandomSource var1) {
      return this.value;
   }

   @Override
   public int getMinValue() {
      return this.value;
   }

   @Override
   public int getMaxValue() {
      return this.value;
   }

   @Override
   public IntProviderType<?> getType() {
      return IntProviderType.CONSTANT;
   }

   @Override
   public String toString() {
      return Integer.toString(this.value);
   }
}
