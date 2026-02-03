package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class UniformInt extends IntProvider {
   public static final MapCodec<UniformInt> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.INT.fieldOf("min_inclusive").forGetter((u) -> u.minInclusive), Codec.INT.fieldOf("max_inclusive").forGetter((u) -> u.maxInclusive)).apply(i, UniformInt::new)).validate((u) -> u.maxInclusive < u.minInclusive ? DataResult.error(() -> "Max must be at least min, min_inclusive: " + u.minInclusive + ", max_inclusive: " + u.maxInclusive) : DataResult.success(u));
   private final int minInclusive;
   private final int maxInclusive;

   private UniformInt(final int minInclusive, final int maxInclusive) {
      super();
      this.minInclusive = minInclusive;
      this.maxInclusive = maxInclusive;
   }

   public static UniformInt of(final int minInclusive, final int maxInclusive) {
      return new UniformInt(minInclusive, maxInclusive);
   }

   public int sample(final RandomSource random) {
      return Mth.randomBetweenInclusive(random, this.minInclusive, this.maxInclusive);
   }

   public int getMinValue() {
      return this.minInclusive;
   }

   public int getMaxValue() {
      return this.maxInclusive;
   }

   public IntProviderType<?> getType() {
      return IntProviderType.UNIFORM;
   }

   public String toString() {
      return "[" + this.minInclusive + "-" + this.maxInclusive + "]";
   }
}
