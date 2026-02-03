package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class ClampedNormalFloat extends FloatProvider {
   public static final MapCodec<ClampedNormalFloat> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.FLOAT.fieldOf("mean").forGetter((c) -> c.mean), Codec.FLOAT.fieldOf("deviation").forGetter((c) -> c.deviation), Codec.FLOAT.fieldOf("min").forGetter((c) -> c.min), Codec.FLOAT.fieldOf("max").forGetter((c) -> c.max)).apply(i, ClampedNormalFloat::new)).validate((c) -> c.max < c.min ? DataResult.error(() -> "Max must be larger than min: [" + c.min + ", " + c.max + "]") : DataResult.success(c));
   private final float mean;
   private final float deviation;
   private final float min;
   private final float max;

   public static ClampedNormalFloat of(final float mean, final float deviation, final float min, final float max) {
      return new ClampedNormalFloat(mean, deviation, min, max);
   }

   private ClampedNormalFloat(final float mean, final float deviation, final float min, final float max) {
      super();
      this.mean = mean;
      this.deviation = deviation;
      this.min = min;
      this.max = max;
   }

   public float sample(final RandomSource random) {
      return sample(random, this.mean, this.deviation, this.min, this.max);
   }

   public static float sample(final RandomSource random, final float mean, final float deviation, final float min, final float max) {
      return Mth.clamp(Mth.normal(random, mean, deviation), min, max);
   }

   public float getMinValue() {
      return this.min;
   }

   public float getMaxValue() {
      return this.max;
   }

   public FloatProviderType<?> getType() {
      return FloatProviderType.CLAMPED_NORMAL;
   }

   public String toString() {
      return "normal(" + this.mean + ", " + this.deviation + ") in [" + this.min + "-" + this.max + "]";
   }
}
