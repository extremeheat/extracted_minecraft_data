package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;

public class ConstantFloat extends FloatProvider {
   public static final ConstantFloat ZERO = new ConstantFloat(0.0F);
   public static final MapCodec<ConstantFloat> CODEC;
   private final float value;

   public static ConstantFloat of(final float value) {
      return value == 0.0F ? ZERO : new ConstantFloat(value);
   }

   private ConstantFloat(final float value) {
      super();
      this.value = value;
   }

   public float getValue() {
      return this.value;
   }

   public float sample(final RandomSource random) {
      return this.value;
   }

   public float getMinValue() {
      return this.value;
   }

   public float getMaxValue() {
      return this.value;
   }

   public FloatProviderType<?> getType() {
      return FloatProviderType.CONSTANT;
   }

   public String toString() {
      return Float.toString(this.value);
   }

   static {
      CODEC = Codec.FLOAT.fieldOf("value").xmap(ConstantFloat::of, ConstantFloat::getValue);
   }
}
