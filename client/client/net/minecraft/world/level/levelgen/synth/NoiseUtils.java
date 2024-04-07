package net.minecraft.world.level.levelgen.synth;

import java.util.Locale;

public class NoiseUtils {
   public NoiseUtils() {
      super();
   }

   public static double biasTowardsExtreme(double var0, double var2) {
      return var0 + Math.sin(3.141592653589793 * var0) * var2 / 3.141592653589793;
   }

   public static void parityNoiseOctaveConfigString(StringBuilder var0, double var1, double var3, double var5, byte[] var7) {
      var0.append(String.format(Locale.ROOT, "xo=%.3f, yo=%.3f, zo=%.3f, p0=%d, p255=%d", (float)var1, (float)var3, (float)var5, var7[0], var7[255]));
   }

   public static void parityNoiseOctaveConfigString(StringBuilder var0, double var1, double var3, double var5, int[] var7) {
      var0.append(String.format(Locale.ROOT, "xo=%.3f, yo=%.3f, zo=%.3f, p0=%d, p255=%d", (float)var1, (float)var3, (float)var5, var7[0], var7[255]));
   }
}
