package net.minecraft.world.level.levelgen.synth;

import java.util.Random;

public class PerlinSimplexNoise implements SurfaceNoise {
   private final SimplexNoise[] noiseLevels;
   private final int levels;

   public PerlinSimplexNoise(Random var1, int var2) {
      super();
      this.levels = var2;
      this.noiseLevels = new SimplexNoise[var2];

      for(int var3 = 0; var3 < var2; ++var3) {
         this.noiseLevels[var3] = new SimplexNoise(var1);
      }

   }

   public double getValue(double var1, double var3) {
      return this.getValue(var1, var3, false);
   }

   public double getValue(double var1, double var3, boolean var5) {
      double var6 = 0.0D;
      double var8 = 1.0D;

      for(int var10 = 0; var10 < this.levels; ++var10) {
         var6 += this.noiseLevels[var10].getValue(var1 * var8 + (var5 ? this.noiseLevels[var10].xo : 0.0D), var3 * var8 + (var5 ? this.noiseLevels[var10].yo : 0.0D)) / var8;
         var8 /= 2.0D;
      }

      return var6;
   }

   public double getSurfaceNoiseValue(double var1, double var3, double var5, double var7) {
      return this.getValue(var1, var3, true) * 0.55D;
   }
}
