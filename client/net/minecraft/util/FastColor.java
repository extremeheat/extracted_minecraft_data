package net.minecraft.util;

public class FastColor {
   public FastColor() {
      super();
   }

   public static int as8BitChannel(float var0) {
      return Mth.floor(var0 * 255.0F);
   }

   public static class ABGR32 {
      public ABGR32() {
         super();
      }

      public static int alpha(int var0) {
         return var0 >>> 24;
      }

      public static int red(int var0) {
         return var0 & 255;
      }

      public static int green(int var0) {
         return var0 >> 8 & 255;
      }

      public static int blue(int var0) {
         return var0 >> 16 & 255;
      }

      public static int transparent(int var0) {
         return var0 & 16777215;
      }

      public static int opaque(int var0) {
         return var0 | -16777216;
      }

      public static int color(int var0, int var1, int var2, int var3) {
         return var0 << 24 | var1 << 16 | var2 << 8 | var3;
      }

      public static int color(int var0, int var1) {
         return var0 << 24 | var1 & 16777215;
      }

      public static int fromArgb32(int var0) {
         return var0 & -16711936 | (var0 & 16711680) >> 16 | (var0 & 255) << 16;
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

      public static int color(int var0, int var1, int var2) {
         return color(255, var0, var1, var2);
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

      public static int opaque(int var0) {
         return var0 | -16777216;
      }

      public static int color(int var0, int var1) {
         return var0 << 24 | var1 & 16777215;
      }

      public static int colorFromFloat(float var0, float var1, float var2, float var3) {
         return color(FastColor.as8BitChannel(var0), FastColor.as8BitChannel(var1), FastColor.as8BitChannel(var2), FastColor.as8BitChannel(var3));
      }

      public static int average(int var0, int var1) {
         return color((alpha(var0) + alpha(var1)) / 2, (red(var0) + red(var1)) / 2, (green(var0) + green(var1)) / 2, (blue(var0) + blue(var1)) / 2);
      }
   }
}
