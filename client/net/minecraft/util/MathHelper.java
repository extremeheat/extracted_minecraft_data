package net.minecraft.util;

import java.util.Random;

public class MathHelper {
   private static float[] field_76144_a = new float[65536];
   private static final int[] field_151242_b;

   public static final float func_76126_a(float var0) {
      return field_76144_a[(int)(var0 * 10430.378F) & 65535];
   }

   public static final float func_76134_b(float var0) {
      return field_76144_a[(int)(var0 * 10430.378F + 16384.0F) & 65535];
   }

   public static final float func_76129_c(float var0) {
      return (float)Math.sqrt((double)var0);
   }

   public static final float func_76133_a(double var0) {
      return (float)Math.sqrt(var0);
   }

   public static int func_76141_d(float var0) {
      int var1 = (int)var0;
      return var0 < (float)var1 ? var1 - 1 : var1;
   }

   public static int func_76140_b(double var0) {
      return (int)(var0 + 1024.0) - 1024;
   }

   public static int func_76128_c(double var0) {
      int var2 = (int)var0;
      return var0 < (double)var2 ? var2 - 1 : var2;
   }

   public static long func_76124_d(double var0) {
      long var2 = (long)var0;
      return var0 < (double)var2 ? var2 - 1L : var2;
   }

   public static int func_154353_e(double var0) {
      return (int)(var0 >= 0.0 ? var0 : -var0 + 1.0);
   }

   public static float func_76135_e(float var0) {
      return var0 >= 0.0F ? var0 : -var0;
   }

   public static int func_76130_a(int var0) {
      return var0 >= 0 ? var0 : -var0;
   }

   public static int func_76123_f(float var0) {
      int var1 = (int)var0;
      return var0 > (float)var1 ? var1 + 1 : var1;
   }

   public static int func_76143_f(double var0) {
      int var2 = (int)var0;
      return var0 > (double)var2 ? var2 + 1 : var2;
   }

   public static int func_76125_a(int var0, int var1, int var2) {
      if (var0 < var1) {
         return var1;
      } else {
         return var0 > var2 ? var2 : var0;
      }
   }

   public static float func_76131_a(float var0, float var1, float var2) {
      if (var0 < var1) {
         return var1;
      } else {
         return var0 > var2 ? var2 : var0;
      }
   }

   public static double func_151237_a(double var0, double var2, double var4) {
      if (var0 < var2) {
         return var2;
      } else {
         return var0 > var4 ? var4 : var0;
      }
   }

   public static double func_151238_b(double var0, double var2, double var4) {
      if (var4 < 0.0) {
         return var0;
      } else {
         return var4 > 1.0 ? var2 : var0 + (var2 - var0) * var4;
      }
   }

   public static double func_76132_a(double var0, double var2) {
      if (var0 < 0.0) {
         var0 = -var0;
      }

      if (var2 < 0.0) {
         var2 = -var2;
      }

      return var0 > var2 ? var0 : var2;
   }

   public static int func_76137_a(int var0, int var1) {
      return var0 < 0 ? -((-var0 - 1) / var1) - 1 : var0 / var1;
   }

   public static boolean func_76139_a(String var0) {
      return var0 == null || var0.length() == 0;
   }

   public static int func_76136_a(Random var0, int var1, int var2) {
      return var1 >= var2 ? var1 : var0.nextInt(var2 - var1 + 1) + var1;
   }

   public static float func_151240_a(Random var0, float var1, float var2) {
      return var1 >= var2 ? var1 : var0.nextFloat() * (var2 - var1) + var1;
   }

   public static double func_82716_a(Random var0, double var1, double var3) {
      return var1 >= var3 ? var1 : var0.nextDouble() * (var3 - var1) + var1;
   }

   public static double func_76127_a(long[] var0) {
      long var1 = 0L;

      for(long var6 : var0) {
         var1 += var6;
      }

      return (double)var1 / (double)var0.length;
   }

   public static float func_76142_g(float var0) {
      var0 %= 360.0F;
      if (var0 >= 180.0F) {
         var0 -= 360.0F;
      }

      if (var0 < -180.0F) {
         var0 += 360.0F;
      }

      return var0;
   }

   public static double func_76138_g(double var0) {
      var0 %= 360.0;
      if (var0 >= 180.0) {
         var0 -= 360.0;
      }

      if (var0 < -180.0) {
         var0 += 360.0;
      }

      return var0;
   }

   public static int func_82715_a(String var0, int var1) {
      int var2 = var1;

      try {
         var2 = Integer.parseInt(var0);
      } catch (Throwable var4) {
      }

      return var2;
   }

   public static int func_82714_a(String var0, int var1, int var2) {
      int var3 = var1;

      try {
         var3 = Integer.parseInt(var0);
      } catch (Throwable var5) {
      }

      if (var3 < var2) {
         var3 = var2;
      }

      return var3;
   }

   public static double func_82712_a(String var0, double var1) {
      double var3 = var1;

      try {
         var3 = Double.parseDouble(var0);
      } catch (Throwable var6) {
      }

      return var3;
   }

   public static double func_82713_a(String var0, double var1, double var3) {
      double var5 = var1;

      try {
         var5 = Double.parseDouble(var0);
      } catch (Throwable var8) {
      }

      if (var5 < var3) {
         var5 = var3;
      }

      return var5;
   }

   public static int func_151236_b(int var0) {
      int var1 = var0 - 1;
      var1 |= var1 >> 1;
      var1 |= var1 >> 2;
      var1 |= var1 >> 4;
      var1 |= var1 >> 8;
      var1 |= var1 >> 16;
      return var1 + 1;
   }

   private static boolean func_151235_d(int var0) {
      return var0 != 0 && (var0 & var0 - 1) == 0;
   }

   private static int func_151241_e(int var0) {
      var0 = func_151235_d(var0) ? var0 : func_151236_b(var0);
      return field_151242_b[(int)((long)var0 * 125613361L >> 27) & 31];
   }

   public static int func_151239_c(int var0) {
      return func_151241_e(var0) - (func_151235_d(var0) ? 0 : 1);
   }

   public static int func_154354_b(int var0, int var1) {
      if (var1 == 0) {
         return 0;
      } else {
         if (var0 < 0) {
            var1 *= -1;
         }

         int var2 = var0 % var1;
         return var2 == 0 ? var0 : var0 + var1 - var2;
      }
   }

   static {
      for(int var0 = 0; var0 < 65536; ++var0) {
         field_76144_a[var0] = (float)Math.sin((double)var0 * 3.141592653589793 * 2.0 / 65536.0);
      }

      field_151242_b = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
   }
}
