package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoiseSamplingSettings {
   private static final Codec<Double> SCALE_RANGE = Codec.doubleRange(0.001D, 1000.0D);
   public static final Codec<NoiseSamplingSettings> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(SCALE_RANGE.fieldOf("xz_scale").forGetter(NoiseSamplingSettings::xzScale), SCALE_RANGE.fieldOf("y_scale").forGetter(NoiseSamplingSettings::yScale), SCALE_RANGE.fieldOf("xz_factor").forGetter(NoiseSamplingSettings::xzFactor), SCALE_RANGE.fieldOf("y_factor").forGetter(NoiseSamplingSettings::yFactor)).apply(var0, NoiseSamplingSettings::new);
   });
   private final double xzScale;
   private final double yScale;
   private final double xzFactor;
   private final double yFactor;

   public NoiseSamplingSettings(double var1, double var3, double var5, double var7) {
      super();
      this.xzScale = var1;
      this.yScale = var3;
      this.xzFactor = var5;
      this.yFactor = var7;
   }

   public double xzScale() {
      return this.xzScale;
   }

   public double yScale() {
      return this.yScale;
   }

   public double xzFactor() {
      return this.xzFactor;
   }

   public double yFactor() {
      return this.yFactor;
   }
}
