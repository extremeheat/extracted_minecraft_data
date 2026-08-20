package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

public record NoiseBasedCountPlacement(int noiseToCountRatio, double noiseFactor, double noiseOffset) implements RepeatingPlacement {
   public static final MapCodec<NoiseBasedCountPlacement> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.INT.fieldOf("noise_to_count_ratio").forGetter(NoiseBasedCountPlacement::noiseToCountRatio), Codec.DOUBLE.fieldOf("noise_factor").forGetter(NoiseBasedCountPlacement::noiseFactor), Codec.DOUBLE.optionalFieldOf("noise_offset", 0.0).forGetter(NoiseBasedCountPlacement::noiseOffset)).apply(i, NoiseBasedCountPlacement::new));

   public NoiseBasedCountPlacement {
      super();
   }

   public static NoiseBasedCountPlacement of(final int noiseToCountRatio, final double noiseFactor, final double noiseOffset) {
      return new NoiseBasedCountPlacement(noiseToCountRatio, noiseFactor, noiseOffset);
   }

   public int count(final RandomSource random, final BlockPos origin) {
      double flowerNoise = (double)Biome.BIOME_INFO_NOISE.get((double)origin.getX() / this.noiseFactor, (double)origin.getZ() / this.noiseFactor);
      return (int)Math.ceil((flowerNoise + this.noiseOffset) * (double)this.noiseToCountRatio);
   }

   public MapCodec<NoiseBasedCountPlacement> codec() {
      return CODEC;
   }
}
