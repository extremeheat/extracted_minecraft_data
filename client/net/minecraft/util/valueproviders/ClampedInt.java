package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class ClampedInt extends IntProvider {
   public static final MapCodec<ClampedInt> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(IntProvider.CODEC.fieldOf("source").forGetter((u) -> u.source), Codec.INT.fieldOf("min_inclusive").forGetter((u) -> u.minInclusive), Codec.INT.fieldOf("max_inclusive").forGetter((u) -> u.maxInclusive)).apply(i, ClampedInt::new)).validate((u) -> u.maxInclusive < u.minInclusive ? DataResult.error(() -> "Max must be at least min, min_inclusive: " + u.minInclusive + ", max_inclusive: " + u.maxInclusive) : DataResult.success(u));
   private final IntProvider source;
   private final int minInclusive;
   private final int maxInclusive;

   public static ClampedInt of(final IntProvider source, final int minInclusive, final int maxInclusive) {
      return new ClampedInt(source, minInclusive, maxInclusive);
   }

   public ClampedInt(final IntProvider source, final int minInclusive, final int maxInclusive) {
      super();
      this.source = source;
      this.minInclusive = minInclusive;
      this.maxInclusive = maxInclusive;
   }

   public int sample(final RandomSource random) {
      return Mth.clamp(this.source.sample(random), this.minInclusive, this.maxInclusive);
   }

   public int getMinValue() {
      return Math.max(this.minInclusive, this.source.getMinValue());
   }

   public int getMaxValue() {
      return Math.min(this.maxInclusive, this.source.getMaxValue());
   }

   public IntProviderType<?> getType() {
      return IntProviderType.CLAMPED;
   }
}
