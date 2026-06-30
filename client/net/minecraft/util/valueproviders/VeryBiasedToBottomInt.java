package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;

public record VeryBiasedToBottomInt(int minInclusive, int maxInclusive) implements IntProvider {
   public static final MapCodec<VeryBiasedToBottomInt> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.INT.fieldOf("min_inclusive").forGetter(VeryBiasedToBottomInt::minInclusive), Codec.INT.fieldOf("max_inclusive").forGetter(VeryBiasedToBottomInt::maxInclusive)).apply(i, VeryBiasedToBottomInt::new)).validate((u) -> u.maxInclusive < u.minInclusive ? DataResult.error(() -> "Max must be at least min, min_inclusive: " + u.minInclusive + ", max_inclusive: " + u.maxInclusive) : DataResult.success(u));

   public VeryBiasedToBottomInt {
      super();
   }

   public static VeryBiasedToBottomInt of(final int minInclusive, final int maxInclusive) {
      return new VeryBiasedToBottomInt(minInclusive, maxInclusive);
   }

   public int sample(final RandomSource random) {
      return this.minInclusive + random.nextInt(random.nextInt(random.nextInt(this.maxInclusive - this.minInclusive + 1) + 1) + 1);
   }

   public MapCodec<VeryBiasedToBottomInt> codec() {
      return MAP_CODEC;
   }

   public String toString() {
      return "[" + this.minInclusive + "-" + this.maxInclusive + "]";
   }
}
