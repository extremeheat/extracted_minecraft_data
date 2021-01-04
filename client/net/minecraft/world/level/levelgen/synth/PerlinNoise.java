package net.minecraft.world.level.levelgen.synth;

import java.util.Random;
import net.minecraft.util.Mth;

public class PerlinNoise implements SurfaceNoise {
   private final ImprovedNoise[] noiseLevels;

   public PerlinNoise(Random var1, int var2) {
      super();
      this.noiseLevels = new ImprovedNoise[var2];

      for(int var3 = 0; var3 < var2; ++var3) {
         this.noiseLevels[var3] = new ImprovedNoise(var1);
      }

   }

   public double getValue(double var1, double var3, double var5) {
      return this.getValue(var1, var3, var5, 0.0D, 0.0D, false);
   }

   public double getValue(double var1, double var3, double var5, double var7, double var9, boolean var11) {
      double var12 = 0.0D;
      double var14 = 1.0D;
      ImprovedNoise[] var16 = this.noiseLevels;
      int var17 = var16.length;

      for(int var18 = 0; var18 < var17; ++var18) {
         ImprovedNoise var19 = var16[var18];
         var12 += var19.noise(wrap(var1 * var14), var11 ? -var19.yo : wrap(var3 * var14), wrap(var5 * var14), var7 * var14, var9 * var14) / var14;
         var14 /= 2.0D;
      }

      return var12;
   }

   public ImprovedNoise getOctaveNoise(int var1) {
      return this.noiseLevels[var1];
   }

   public static double wrap(double var0) {
      return var0 - (double)Mth.lfloor(var0 / 3.3554432E7D + 0.5D) * 3.3554432E7D;
   }

   public double getSurfaceNoiseValue(double var1, double var3, double var5, double var7) {
      return this.getValue(var1, var3, 0.0D, var5, var7, false);
   }
}
