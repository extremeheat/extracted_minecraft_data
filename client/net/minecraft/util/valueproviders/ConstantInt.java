package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;

public class ConstantInt extends IntProvider {
   public static final ConstantInt ZERO = new ConstantInt(0);
   public static final MapCodec<ConstantInt> CODEC;
   private final int value;

   public static ConstantInt of(final int value) {
      return value == 0 ? ZERO : new ConstantInt(value);
   }

   private ConstantInt(final int value) {
      super();
      this.value = value;
   }

   public int getValue() {
      return this.value;
   }

   public int sample(final RandomSource random) {
      return this.value;
   }

   public int getMinValue() {
      return this.value;
   }

   public int getMaxValue() {
      return this.value;
   }

   public IntProviderType<?> getType() {
      return IntProviderType.CONSTANT;
   }

   public String toString() {
      return Integer.toString(this.value);
   }

   static {
      CODEC = Codec.INT.fieldOf("value").xmap(ConstantInt::of, ConstantInt::getValue);
   }
}
