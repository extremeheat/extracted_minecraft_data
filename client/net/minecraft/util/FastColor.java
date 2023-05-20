package net.minecraft.util;

public class FastColor {
   public FastColor() {
      super();
   }

   public static class ABGR32 {
      public ABGR32() {
         super();
      }

      public static int alpha(int var0) {
         return var0 >>> 24;
      }

      public static int red(int var0) {
         return var0 & 0xFF;
      }

      public static int green(int var0) {
         return var0 >> 8 & 0xFF;
      }

      public static int blue(int var0) {
         return var0 >> 16 & 0xFF;
      }

      public static int transparent(int var0) {
         return var0 & 16777215;
      }

      public static int opaque(int var0) {
         return var0 | 0xFF000000;
      }

      public static int color(int var0, int var1, int var2, int var3) {
         return var0 << 24 | var1 << 16 | var2 << 8 | var3;
      }

      public static int color(int var0, int var1) {
         return var0 << 24 | var1 & 16777215;
      }
   }

   public static class ARGB32 {
      public ARGB32() {
         super();
      }

      public static int alpha(int var0) {
         return var0 >>> 24;
      }

      public static int red(int var0) {
         return var0 >> 16 & 0xFF;
      }

      public static int green(int var0) {
         return var0 >> 8 & 0xFF;
      }

      public static int blue(int var0) {
         return var0 & 0xFF;
      }

      public static int color(int var0, int var1, int var2, int var3) {
         return var0 << 24 | var1 << 16 | var2 << 8 | var3;
      }

      public static int multiply(int var0, int var1) {
         return color(alpha(var0) * alpha(var1) / 255, red(var0) * red(var1) / 255, green(var0) * green(var1) / 255, blue(var0) * blue(var1) / 255);
      }

      public static int lerp(float var0, int var1, int var2) {
         int var3 = Mth.lerpInt(var0, alpha(var1), alpha(var2));
         int var4 = Mth.lerpInt(var0, red(var1), red(var2));
         int var5 = Mth.lerpInt(var0, green(var1), green(var2));
         int var6 = Mth.lerpInt(var0, blue(var1), blue(var2));
         return color(var3, var4, var5, var6);
      }
   }
}
