package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class UniformFloat extends FloatProvider {
   public static final MapCodec<UniformFloat> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.FLOAT.fieldOf("min_inclusive").forGetter((u) -> u.minInclusive), Codec.FLOAT.fieldOf("max_exclusive").forGetter((u) -> u.maxExclusive)).apply(i, UniformFloat::new)).validate((u) -> u.maxExclusive <= u.minInclusive ? DataResult.error(() -> "Max must be larger than min, min_inclusive: " + u.minInclusive + ", max_exclusive: " + u.maxExclusive) : DataResult.success(u));
   private final float minInclusive;
   private final float maxExclusive;

   private UniformFloat(final float minInclusive, final float maxExclusive) {
      super();
      this.minInclusive = minInclusive;
      this.maxExclusive = maxExclusive;
   }

   public static UniformFloat of(final float minInclusive, final float maxExclusive) {
      if (maxExclusive <= minInclusive) {
         throw new IllegalArgumentException("Max must exceed min");
      } else {
         return new UniformFloat(minInclusive, maxExclusive);
      }
   }

   public float sample(final RandomSource random) {
      return Mth.randomBetween(random, this.minInclusive, this.maxExclusive);
   }

   public float getMinValue() {
      return this.minInclusive;
   }

   public float getMaxValue() {
      return this.maxExclusive;
   }

   public FloatProviderType<?> getType() {
      return FloatProviderType.UNIFORM;
   }

   public String toString() {
      return "[" + this.minInclusive + "-" + this.maxExclusive + "]";
   }
}
