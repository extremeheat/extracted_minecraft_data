package net.minecraft.util;

public class FastColor {
   public static class ARGB32 {
      public static int alpha(int var0) {
         return var0 >>> 24;
      }

      public static int red(int var0) {
         return var0 >> 16 & 255;
      }

      public static int green(int var0) {
         return var0 >> 8 & 255;
      }

      public static int blue(int var0) {
         return var0 & 255;
      }

      public static int color(int var0, int var1, int var2, int var3) {
         return var0 << 24 | var1 << 16 | var2 << 8 | var3;
      }

      public static int multiply(int var0, int var1) {
         return color(alpha(var0) * alpha(var1) / 255, red(var0) * red(var1) / 255, green(var0) * green(var1) / 255, blue(var0) * blue(var1) / 255);
      }
   }
}
