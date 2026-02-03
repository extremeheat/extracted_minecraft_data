package net.minecraft.world.level.levelgen.synth;

import java.util.Locale;

public class NoiseUtils {
   public NoiseUtils() {
      super();
   }

   public static double biasTowardsExtreme(final double noise, final double factor) {
      return noise + Math.sin(3.141592653589793 * noise) * factor / 3.141592653589793;
   }

   public static void parityNoiseOctaveConfigString(final StringBuilder sb, final double xo, final double yo, final double zo, final byte[] p) {
      sb.append(String.format(Locale.ROOT, "xo=%.3f, yo=%.3f, zo=%.3f, p0=%d, p255=%d", (float)xo, (float)yo, (float)zo, p[0], p[255]));
   }

   public static void parityNoiseOctaveConfigString(final StringBuilder sb, final double xo, final double yo, final double zo, final int[] p) {
      sb.append(String.format(Locale.ROOT, "xo=%.3f, yo=%.3f, zo=%.3f, p0=%d, p255=%d", (float)xo, (float)yo, (float)zo, p[0], p[255]));
   }
}
