package net.minecraft.world.level.levelgen.synth;

import net.minecraft.util.Mth;

public class NoiseUtils {
   public NoiseUtils() {
      super();
   }

   public static double sampleNoiseAndMapToRange(NormalNoise var0, double var1, double var3, double var5, double var7, double var9) {
      double var11 = var0.getValue(var1, var3, var5);
      return Mth.map(var11, -1.0D, 1.0D, var7, var9);
   }

   public static double biasTowardsExtreme(double var0, double var2) {
      return var0 + Math.sin(3.141592653589793D * var0) * var2 / 3.141592653589793D;
   }

   public static void parityNoiseOctaveConfigString(StringBuilder var0, double var1, double var3, double var5, byte[] var7) {
      var0.append(String.format("xo=%.3f, yo=%.3f, zo=%.3f, p0=%d, p255=%d", (float)var1, (float)var3, (float)var5, var7[0], var7[255]));
   }

   public static void parityNoiseOctaveConfigString(StringBuilder var0, double var1, double var3, double var5, int[] var7) {
      var0.append(String.format("xo=%.3f, yo=%.3f, zo=%.3f, p0=%d, p255=%d", (float)var1, (float)var3, (float)var5, var7[0], var7[255]));
   }
}
