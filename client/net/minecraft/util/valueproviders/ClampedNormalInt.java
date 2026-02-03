package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class ClampedNormalInt extends IntProvider {
   public static final MapCodec<ClampedNormalInt> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.FLOAT.fieldOf("mean").forGetter((c) -> c.mean), Codec.FLOAT.fieldOf("deviation").forGetter((c) -> c.deviation), Codec.INT.fieldOf("min_inclusive").forGetter((c) -> c.minInclusive), Codec.INT.fieldOf("max_inclusive").forGetter((c) -> c.maxInclusive)).apply(i, ClampedNormalInt::new)).validate((c) -> c.maxInclusive < c.minInclusive ? DataResult.error(() -> "Max must be larger than min: [" + c.minInclusive + ", " + c.maxInclusive + "]") : DataResult.success(c));
   private final float mean;
   private final float deviation;
   private final int minInclusive;
   private final int maxInclusive;

   public static ClampedNormalInt of(final float mean, final float deviation, final int min_inclusive, final int max_inclusive) {
      return new ClampedNormalInt(mean, deviation, min_inclusive, max_inclusive);
   }

   private ClampedNormalInt(final float mean, final float deviation, final int minInclusive, final int maxInclusive) {
      super();
      this.mean = mean;
      this.deviation = deviation;
      this.minInclusive = minInclusive;
      this.maxInclusive = maxInclusive;
   }

   public int sample(final RandomSource random) {
      return sample(random, this.mean, this.deviation, (float)this.minInclusive, (float)this.maxInclusive);
   }

   public static int sample(final RandomSource random, final float mean, final float deviation, final float min_inclusive, final float max_inclusive) {
      return (int)Mth.clamp(Mth.normal(random, mean, deviation), min_inclusive, max_inclusive);
   }

   public int getMinValue() {
      return this.minInclusive;
   }

   public int getMaxValue() {
      return this.maxInclusive;
   }

   public IntProviderType<?> getType() {
      return IntProviderType.CLAMPED_NORMAL;
   }

   public String toString() {
      return "normal(" + this.mean + ", " + this.deviation + ") in [" + this.minInclusive + "-" + this.maxInclusive + "]";
   }
}
