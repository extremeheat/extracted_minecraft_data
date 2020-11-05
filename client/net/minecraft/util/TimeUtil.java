package net.minecraft.util;

public class TimeUtil {
   public static IntRange rangeOfSeconds(int var0, int var1) {
      return new IntRange(var0 * 20, var1 * 20);
   }
}
