package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

public class NoiseBasedCountPlacement extends RepeatingPlacement {
   public static final Codec<NoiseBasedCountPlacement> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               Codec.INT.fieldOf("noise_to_count_ratio").forGetter(var0x -> var0x.noiseToCountRatio),
               Codec.DOUBLE.fieldOf("noise_factor").forGetter(var0x -> var0x.noiseFactor),
               Codec.DOUBLE.fieldOf("noise_offset").orElse(0.0).forGetter(var0x -> var0x.noiseOffset)
            )
            .apply(var0, NoiseBasedCountPlacement::new)
   );
   private final int noiseToCountRatio;
   private final double noiseFactor;
   private final double noiseOffset;

   private NoiseBasedCountPlacement(int var1, double var2, double var4) {
      super();
      this.noiseToCountRatio = var1;
      this.noiseFactor = var2;
      this.noiseOffset = var4;
   }

   public static NoiseBasedCountPlacement of(int var0, double var1, double var3) {
      return new NoiseBasedCountPlacement(var0, var1, var3);
   }

   @Override
   protected int count(RandomSource var1, BlockPos var2) {
      double var3 = Biome.BIOME_INFO_NOISE.getValue((double)var2.getX() / this.noiseFactor, (double)var2.getZ() / this.noiseFactor, false);
      return (int)Math.ceil((var3 + this.noiseOffset) * (double)this.noiseToCountRatio);
   }

   @Override
   public PlacementModifierType<?> type() {
      return PlacementModifierType.NOISE_BASED_COUNT;
   }
}
