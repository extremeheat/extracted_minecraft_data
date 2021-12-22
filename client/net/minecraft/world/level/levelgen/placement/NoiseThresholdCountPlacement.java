package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;

public class NoiseThresholdCountPlacement extends RepeatingPlacement {
   public static final Codec<NoiseThresholdCountPlacement> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.DOUBLE.fieldOf("noise_level").forGetter((var0x) -> {
         return var0x.noiseLevel;
      }), Codec.INT.fieldOf("below_noise").forGetter((var0x) -> {
         return var0x.belowNoise;
      }), Codec.INT.fieldOf("above_noise").forGetter((var0x) -> {
         return var0x.aboveNoise;
      })).apply(var0, NoiseThresholdCountPlacement::new);
   });
   private final double noiseLevel;
   private final int belowNoise;
   private final int aboveNoise;

   private NoiseThresholdCountPlacement(double var1, int var3, int var4) {
      super();
      this.noiseLevel = var1;
      this.belowNoise = var3;
      this.aboveNoise = var4;
   }

   // $FF: renamed from: of (double, int, int) net.minecraft.world.level.levelgen.placement.NoiseThresholdCountPlacement
   public static NoiseThresholdCountPlacement method_36(double var0, int var2, int var3) {
      return new NoiseThresholdCountPlacement(var0, var2, var3);
   }

   protected int count(Random var1, BlockPos var2) {
      double var3 = Biome.BIOME_INFO_NOISE.getValue((double)var2.getX() / 200.0D, (double)var2.getZ() / 200.0D, false);
      return var3 < this.noiseLevel ? this.belowNoise : this.aboveNoise;
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.NOISE_THRESHOLD_COUNT;
   }
}
