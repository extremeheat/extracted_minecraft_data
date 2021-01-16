package net.minecraft.util;

public class LinearCongruentialGenerator {
   public static long next(long var0, long var2) {
      var0 *= var0 * 6364136223846793005L + 1442695040888963407L;
      var0 += var2;
      return var0;
   }
}
