package net.minecraft.util;

import java.util.Locale;
import java.util.UUID;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import net.minecraft.Util;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.math.NumberUtils;

public class Mth {
   private static final int BIG_ENOUGH_INT = 1024;
   private static final float BIG_ENOUGH_FLOAT = 1024.0F;
   private static final long UUID_VERSION = 61440L;
   private static final long UUID_VERSION_TYPE_4 = 16384L;
   private static final long UUID_VARIANT = -4611686018427387904L;
   private static final long UUID_VARIANT_2 = -9223372036854775808L;
   public static final float PI = 3.1415927F;
   public static final float HALF_PI = 1.5707964F;
   public static final float TWO_PI = 6.2831855F;
   public static final float DEG_TO_RAD = 0.017453292F;
   public static final float RAD_TO_DEG = 57.295776F;
   public static final float EPSILON = 1.0E-5F;
   public static final float SQRT_OF_TWO = sqrt(2.0F);
   private static final float SIN_SCALE = 10430.378F;
   private static final float[] SIN = Util.make(new float[65536], var0x -> {
      for(int var1x = 0; var1x < var0x.length; ++var1x) {
         var0x[var1x] = (float)Math.sin((double)var1x * 3.141592653589793 * 2.0 / 65536.0);
      }
   });
   private static final RandomSource RANDOM = RandomSource.createThreadSafe();
   private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{
      0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9
   };
   private static final double ONE_SIXTH = 0.16666666666666666;
   private static final int FRAC_EXP = 8;
   private static final int LUT_SIZE = 257;
   private static final double FRAC_BIAS = Double.longBitsToDouble(4805340802404319232L);
   private static final double[] ASIN_TAB = new double[257];
   private static final double[] COS_TAB = new double[257];

   public Mth() {
      super();
   }

   public static float truncate(float var0, float var1) {
      float var2 = (float)Math.pow(10.0, (double)var1);
      return (float)((int)(var0 * var2)) / var2;
   }

   public static float sin(float var0) {
      return SIN[(int)(var0 * 10430.378F) & 65535];
   }

   public static float cos(float var0) {
      return SIN[(int)(var0 * 10430.378F + 16384.0F) & 65535];
   }

   public static float sqrt(float var0) {
      return (float)Math.sqrt((double)var0);
   }

   public static int floor(float var0) {
      int var1 = (int)var0;
      return var0 < (float)var1 ? var1 - 1 : var1;
   }

   public static int fastFloor(double var0) {
      return (int)(var0 + 1024.0) - 1024;
   }

   public static int floor(double var0) {
      int var2 = (int)var0;
      return var0 < (double)var2 ? var2 - 1 : var2;
   }

   public static long lfloor(double var0) {
      long var2 = (long)var0;
      return var0 < (double)var2 ? var2 - 1L : var2;
   }

   public static int absFloor(double var0) {
      return (int)(var0 >= 0.0 ? var0 : -var0 + 1.0);
   }

   public static float abs(float var0) {
      return Math.abs(var0);
   }

   public static int abs(int var0) {
      return Math.abs(var0);
   }

   public static int ceil(float var0) {
      int var1 = (int)var0;
      return var0 > (float)var1 ? var1 + 1 : var1;
   }

   public static int ceil(double var0) {
      int var2 = (int)var0;
      return var0 > (double)var2 ? var2 + 1 : var2;
   }

   public static byte clamp(byte var0, byte var1, byte var2) {
      if (var0 < var1) {
         return var1;
      } else {
         return var0 > var2 ? var2 : var0;
      }
   }

   public static int clamp(int var0, int var1, int var2) {
      if (var0 < var1) {
         return var1;
      } else {
         return var0 > var2 ? var2 : var0;
      }
   }

   public static long clamp(long var0, long var2, long var4) {
      if (var0 < var2) {
         return var2;
      } else {
         return var0 > var4 ? var4 : var0;
      }
   }

   public static float clamp(float var0, float var1, float var2) {
      if (var0 < var1) {
         return var1;
      } else {
         return var0 > var2 ? var2 : var0;
      }
   }

   public static double clamp(double var0, double var2, double var4) {
      if (var0 < var2) {
         return var2;
      } else {
         return var0 > var4 ? var4 : var0;
      }
   }

   public static double clampedLerp(double var0, double var2, double var4) {
      if (var4 < 0.0) {
         return var0;
      } else {
         return var4 > 1.0 ? var2 : lerp(var4, var0, var2);
      }
   }

   public static float clampedLerp(float var0, float var1, float var2) {
      if (var2 < 0.0F) {
         return var0;
      } else {
         return var2 > 1.0F ? var1 : lerp(var2, var0, var1);
      }
   }

   public static double absMax(double var0, double var2) {
      if (var0 < 0.0) {
         var0 = -var0;
      }

      if (var2 < 0.0) {
         var2 = -var2;
      }

      return var0 > var2 ? var0 : var2;
   }

   public static int intFloorDiv(int var0, int var1) {
      return Math.floorDiv(var0, var1);
   }

   public static int nextInt(RandomSource var0, int var1, int var2) {
      return var1 >= var2 ? var1 : var0.nextInt(var2 - var1 + 1) + var1;
   }

   public static float nextFloat(RandomSource var0, float var1, float var2) {
      return var1 >= var2 ? var1 : var0.nextFloat() * (var2 - var1) + var1;
   }

   public static double nextDouble(RandomSource var0, double var1, double var3) {
      return var1 >= var3 ? var1 : var0.nextDouble() * (var3 - var1) + var1;
   }

   public static double average(long[] var0) {
      long var1 = 0L;

      for(long var6 : var0) {
         var1 += var6;
      }

      return (double)var1 / (double)var0.length;
   }

   public static boolean equal(float var0, float var1) {
      return Math.abs(var1 - var0) < 1.0E-5F;
   }

   public static boolean equal(double var0, double var2) {
      return Math.abs(var2 - var0) < 9.999999747378752E-6;
   }

   public static int positiveModulo(int var0, int var1) {
      return Math.floorMod(var0, var1);
   }

   public static float positiveModulo(float var0, float var1) {
      return (var0 % var1 + var1) % var1;
   }

   public static double positiveModulo(double var0, double var2) {
      return (var0 % var2 + var2) % var2;
   }

   public static boolean isDivisionInteger(int var0, int var1) {
      return var0 / var1 * var1 == var0;
   }

   public static int wrapDegrees(int var0) {
      int var1 = var0 % 360;
      if (var1 >= 180) {
         var1 -= 360;
      }

      if (var1 < -180) {
         var1 += 360;
      }

      return var1;
   }

   public static float wrapDegrees(float var0) {
      float var1 = var0 % 360.0F;
      if (var1 >= 180.0F) {
         var1 -= 360.0F;
      }

      if (var1 < -180.0F) {
         var1 += 360.0F;
      }

      return var1;
   }

   public static double wrapDegrees(double var0) {
      double var2 = var0 % 360.0;
      if (var2 >= 180.0) {
         var2 -= 360.0;
      }

      if (var2 < -180.0) {
         var2 += 360.0;
      }

      return var2;
   }

   public static float degreesDifference(float var0, float var1) {
      return wrapDegrees(var1 - var0);
   }

   public static float degreesDifferenceAbs(float var0, float var1) {
      return abs(degreesDifference(var0, var1));
   }

   public static float rotateIfNecessary(float var0, float var1, float var2) {
      float var3 = degreesDifference(var0, var1);
      float var4 = clamp(var3, -var2, var2);
      return var1 - var4;
   }

   public static float approach(float var0, float var1, float var2) {
      var2 = abs(var2);
      return var0 < var1 ? clamp(var0 + var2, var0, var1) : clamp(var0 - var2, var1, var0);
   }

   public static float approachDegrees(float var0, float var1, float var2) {
      float var3 = degreesDifference(var0, var1);
      return approach(var0, var0 + var3, var2);
   }

   public static int getInt(String var0, int var1) {
      return NumberUtils.toInt(var0, var1);
   }

   public static int getInt(String var0, int var1, int var2) {
      return Math.max(var2, getInt(var0, var1));
   }

   public static double getDouble(String var0, double var1) {
      try {
         return Double.parseDouble(var0);
      } catch (Throwable var4) {
         return var1;
      }
   }

   public static double getDouble(String var0, double var1, double var3) {
      return Math.max(var3, getDouble(var0, var1));
   }

   public static int smallestEncompassingPowerOfTwo(int var0) {
      int var1 = var0 - 1;
      var1 |= var1 >> 1;
      var1 |= var1 >> 2;
      var1 |= var1 >> 4;
      var1 |= var1 >> 8;
      var1 |= var1 >> 16;
      return var1 + 1;
   }

   public static boolean isPowerOfTwo(int var0) {
      return var0 != 0 && (var0 & var0 - 1) == 0;
   }

   public static int ceillog2(int var0) {
      var0 = isPowerOfTwo(var0) ? var0 : smallestEncompassingPowerOfTwo(var0);
      return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)((long)var0 * 125613361L >> 27) & 31];
   }

   public static int log2(int var0) {
      return ceillog2(var0) - (isPowerOfTwo(var0) ? 0 : 1);
   }

   public static int color(float var0, float var1, float var2) {
      return color(floor(var0 * 255.0F), floor(var1 * 255.0F), floor(var2 * 255.0F));
   }

   public static int color(int var0, int var1, int var2) {
      int var3 = (var0 << 8) + var1;
      return (var3 << 8) + var2;
   }

   public static int colorMultiply(int var0, int var1) {
      int var2 = (var0 & 0xFF0000) >> 16;
      int var3 = (var1 & 0xFF0000) >> 16;
      int var4 = (var0 & 0xFF00) >> 8;
      int var5 = (var1 & 0xFF00) >> 8;
      int var6 = (var0 & 0xFF) >> 0;
      int var7 = (var1 & 0xFF) >> 0;
      int var8 = (int)((float)var2 * (float)var3 / 255.0F);
      int var9 = (int)((float)var4 * (float)var5 / 255.0F);
      int var10 = (int)((float)var6 * (float)var7 / 255.0F);
      return var0 & 0xFF000000 | var8 << 16 | var9 << 8 | var10;
   }

   public static int colorMultiply(int var0, float var1, float var2, float var3) {
      int var4 = (var0 & 0xFF0000) >> 16;
      int var5 = (var0 & 0xFF00) >> 8;
      int var6 = (var0 & 0xFF) >> 0;
      int var7 = (int)((float)var4 * var1);
      int var8 = (int)((float)var5 * var2);
      int var9 = (int)((float)var6 * var3);
      return var0 & 0xFF000000 | var7 << 16 | var8 << 8 | var9;
   }

   public static float frac(float var0) {
      return var0 - (float)floor(var0);
   }

   public static double frac(double var0) {
      return var0 - (double)lfloor(var0);
   }

   public static Vec3 catmullRomSplinePos(Vec3 var0, Vec3 var1, Vec3 var2, Vec3 var3, double var4) {
      double var6 = ((-var4 + 2.0) * var4 - 1.0) * var4 * 0.5;
      double var8 = ((3.0 * var4 - 5.0) * var4 * var4 + 2.0) * 0.5;
      double var10 = ((-3.0 * var4 + 4.0) * var4 + 1.0) * var4 * 0.5;
      double var12 = (var4 - 1.0) * var4 * var4 * 0.5;
      return new Vec3(
         var0.x * var6 + var1.x * var8 + var2.x * var10 + var3.x * var12,
         var0.y * var6 + var1.y * var8 + var2.y * var10 + var3.y * var12,
         var0.z * var6 + var1.z * var8 + var2.z * var10 + var3.z * var12
      );
   }

   public static long getSeed(Vec3i var0) {
      return getSeed(var0.getX(), var0.getY(), var0.getZ());
   }

   public static long getSeed(int var0, int var1, int var2) {
      long var3 = (long)(var0 * 3129871) ^ (long)var2 * 116129781L ^ (long)var1;
      var3 = var3 * var3 * 42317861L + var3 * 11L;
      return var3 >> 16;
   }

   public static UUID createInsecureUUID(RandomSource var0) {
      long var1 = var0.nextLong() & -61441L | 16384L;
      long var3 = var0.nextLong() & 4611686018427387903L | -9223372036854775808L;
      return new UUID(var1, var3);
   }

   public static UUID createInsecureUUID() {
      return createInsecureUUID(RANDOM);
   }

   public static double inverseLerp(double var0, double var2, double var4) {
      return (var0 - var2) / (var4 - var2);
   }

   public static float inverseLerp(float var0, float var1, float var2) {
      return (var0 - var1) / (var2 - var1);
   }

   public static boolean rayIntersectsAABB(Vec3 var0, Vec3 var1, AABB var2) {
      double var3 = (var2.minX + var2.maxX) * 0.5;
      double var5 = (var2.maxX - var2.minX) * 0.5;
      double var7 = var0.x - var3;
      if (Math.abs(var7) > var5 && var7 * var1.x >= 0.0) {
         return false;
      } else {
         double var9 = (var2.minY + var2.maxY) * 0.5;
         double var11 = (var2.maxY - var2.minY) * 0.5;
         double var13 = var0.y - var9;
         if (Math.abs(var13) > var11 && var13 * var1.y >= 0.0) {
            return false;
         } else {
            double var15 = (var2.minZ + var2.maxZ) * 0.5;
            double var17 = (var2.maxZ - var2.minZ) * 0.5;
            double var19 = var0.z - var15;
            if (Math.abs(var19) > var17 && var19 * var1.z >= 0.0) {
               return false;
            } else {
               double var21 = Math.abs(var1.x);
               double var23 = Math.abs(var1.y);
               double var25 = Math.abs(var1.z);
               double var27 = var1.y * var19 - var1.z * var13;
               if (Math.abs(var27) > var11 * var25 + var17 * var23) {
                  return false;
               } else {
                  var27 = var1.z * var7 - var1.x * var19;
                  if (Math.abs(var27) > var5 * var25 + var17 * var21) {
                     return false;
                  } else {
                     var27 = var1.x * var13 - var1.y * var7;
                     return Math.abs(var27) < var5 * var23 + var11 * var21;
                  }
               }
            }
         }
      }
   }

   public static double atan2(double var0, double var2) {
      double var4 = var2 * var2 + var0 * var0;
      if (Double.isNaN(var4)) {
         return 0.0 / 0.0;
      } else {
         boolean var6 = var0 < 0.0;
         if (var6) {
            var0 = -var0;
         }

         boolean var7 = var2 < 0.0;
         if (var7) {
            var2 = -var2;
         }

         boolean var8 = var0 > var2;
         if (var8) {
            double var9 = var2;
            var2 = var0;
            var0 = var9;
         }

         double var28 = fastInvSqrt(var4);
         var2 *= var28;
         var0 *= var28;
         double var11 = FRAC_BIAS + var0;
         int var13 = (int)Double.doubleToRawLongBits(var11);
         double var14 = ASIN_TAB[var13];
         double var16 = COS_TAB[var13];
         double var18 = var11 - FRAC_BIAS;
         double var20 = var0 * var16 - var2 * var18;
         double var22 = (6.0 + var20 * var20) * var20 * 0.16666666666666666;
         double var24 = var14 + var22;
         if (var8) {
            var24 = 1.5707963267948966 - var24;
         }

         if (var7) {
            var24 = 3.141592653589793 - var24;
         }

         if (var6) {
            var24 = -var24;
         }

         return var24;
      }
   }

   public static float fastInvSqrt(float var0) {
      float var1 = 0.5F * var0;
      int var2 = Float.floatToIntBits(var0);
      var2 = 1597463007 - (var2 >> 1);
      var0 = Float.intBitsToFloat(var2);
      return var0 * (1.5F - var1 * var0 * var0);
   }

   public static double fastInvSqrt(double var0) {
      double var2 = 0.5 * var0;
      long var4 = Double.doubleToRawLongBits(var0);
      var4 = 6910469410427058090L - (var4 >> 1);
      var0 = Double.longBitsToDouble(var4);
      return var0 * (1.5 - var2 * var0 * var0);
   }

   public static float fastInvCubeRoot(float var0) {
      int var1 = Float.floatToIntBits(var0);
      var1 = 1419967116 - var1 / 3;
      float var2 = Float.intBitsToFloat(var1);
      var2 = 0.6666667F * var2 + 1.0F / (3.0F * var2 * var2 * var0);
      return 0.6666667F * var2 + 1.0F / (3.0F * var2 * var2 * var0);
   }

   public static int hsvToRgb(float var0, float var1, float var2) {
      int var3 = (int)(var0 * 6.0F) % 6;
      float var4 = var0 * 6.0F - (float)var3;
      float var5 = var2 * (1.0F - var1);
      float var6 = var2 * (1.0F - var4 * var1);
      float var7 = var2 * (1.0F - (1.0F - var4) * var1);
      float var8;
      float var9;
      float var10;
      switch(var3) {
         case 0:
            var8 = var2;
            var9 = var7;
            var10 = var5;
            break;
         case 1:
            var8 = var6;
            var9 = var2;
            var10 = var5;
            break;
         case 2:
            var8 = var5;
            var9 = var2;
            var10 = var7;
            break;
         case 3:
            var8 = var5;
            var9 = var6;
            var10 = var2;
            break;
         case 4:
            var8 = var7;
            var9 = var5;
            var10 = var2;
            break;
         case 5:
            var8 = var2;
            var9 = var5;
            var10 = var6;
            break;
         default:
            throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + var0 + ", " + var1 + ", " + var2);
      }

      int var11 = clamp((int)(var8 * 255.0F), 0, 255);
      int var12 = clamp((int)(var9 * 255.0F), 0, 255);
      int var13 = clamp((int)(var10 * 255.0F), 0, 255);
      return var11 << 16 | var12 << 8 | var13;
   }

   public static int murmurHash3Mixer(int var0) {
      var0 ^= var0 >>> 16;
      var0 *= -2048144789;
      var0 ^= var0 >>> 13;
      var0 *= -1028477387;
      return var0 ^ var0 >>> 16;
   }

   public static long murmurHash3Mixer(long var0) {
      var0 ^= var0 >>> 33;
      var0 *= -49064778989728563L;
      var0 ^= var0 >>> 33;
      var0 *= -4265267296055464877L;
      return var0 ^ var0 >>> 33;
   }

   public static double[] cumulativeSum(double... var0) {
      double var1 = 0.0;

      for(double var6 : var0) {
         var1 += var6;
      }

      for(int var8 = 0; var8 < var0.length; ++var8) {
         var0[var8] /= var1;
      }

      for(int var9 = 0; var9 < var0.length; ++var9) {
         var0[var9] += var9 == 0 ? 0.0 : var0[var9 - 1];
      }

      return var0;
   }

   public static int getRandomForDistributionIntegral(RandomSource var0, double[] var1) {
      double var2 = var0.nextDouble();

      for(int var4 = 0; var4 < var1.length; ++var4) {
         if (var2 < var1[var4]) {
            return var4;
         }
      }

      return var1.length;
   }

   public static double[] binNormalDistribution(double var0, double var2, double var4, int var6, int var7) {
      double[] var8 = new double[var7 - var6 + 1];
      int var9 = 0;

      for(int var10 = var6; var10 <= var7; ++var10) {
         var8[var9] = Math.max(0.0, var0 * StrictMath.exp(-((double)var10 - var4) * ((double)var10 - var4) / (2.0 * var2 * var2)));
         ++var9;
      }

      return var8;
   }

   public static double[] binBiModalNormalDistribution(double var0, double var2, double var4, double var6, double var8, double var10, int var12, int var13) {
      double[] var14 = new double[var13 - var12 + 1];
      int var15 = 0;

      for(int var16 = var12; var16 <= var13; ++var16) {
         var14[var15] = Math.max(
            0.0,
            var0 * StrictMath.exp(-((double)var16 - var4) * ((double)var16 - var4) / (2.0 * var2 * var2))
               + var6 * StrictMath.exp(-((double)var16 - var10) * ((double)var16 - var10) / (2.0 * var8 * var8))
         );
         ++var15;
      }

      return var14;
   }

   public static double[] binLogDistribution(double var0, double var2, int var4, int var5) {
      double[] var6 = new double[var5 - var4 + 1];
      int var7 = 0;

      for(int var8 = var4; var8 <= var5; ++var8) {
         var6[var7] = Math.max(var0 * StrictMath.log((double)var8) + var2, 0.0);
         ++var7;
      }

      return var6;
   }

   public static int binarySearch(int var0, int var1, IntPredicate var2) {
      int var3 = var1 - var0;

      while(var3 > 0) {
         int var4 = var3 / 2;
         int var5 = var0 + var4;
         if (var2.test(var5)) {
            var3 = var4;
         } else {
            var0 = var5 + 1;
            var3 -= var4 + 1;
         }
      }

      return var0;
   }

   public static float lerp(float var0, float var1, float var2) {
      return var1 + var0 * (var2 - var1);
   }

   public static double lerp(double var0, double var2, double var4) {
      return var2 + var0 * (var4 - var2);
   }

   public static double lerp2(double var0, double var2, double var4, double var6, double var8, double var10) {
      return lerp(var2, lerp(var0, var4, var6), lerp(var0, var8, var10));
   }

   public static double lerp3(
      double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20
   ) {
      return lerp(var4, lerp2(var0, var2, var6, var8, var10, var12), lerp2(var0, var2, var14, var16, var18, var20));
   }

   public static float catmullrom(float var0, float var1, float var2, float var3, float var4) {
      return 0.5F
         * (
            2.0F * var2
               + (var3 - var1) * var0
               + (2.0F * var1 - 5.0F * var2 + 4.0F * var3 - var4) * var0 * var0
               + (3.0F * var2 - var1 - 3.0F * var3 + var4) * var0 * var0 * var0
         );
   }

   public static double smoothstep(double var0) {
      return var0 * var0 * var0 * (var0 * (var0 * 6.0 - 15.0) + 10.0);
   }

   public static double smoothstepDerivative(double var0) {
      return 30.0 * var0 * var0 * (var0 - 1.0) * (var0 - 1.0);
   }

   public static int sign(double var0) {
      if (var0 == 0.0) {
         return 0;
      } else {
         return var0 > 0.0 ? 1 : -1;
      }
   }

   public static float rotLerp(float var0, float var1, float var2) {
      return var1 + var0 * wrapDegrees(var2 - var1);
   }

   public static float diffuseLight(float var0, float var1, float var2) {
      return Math.min(var0 * var0 * 0.6F + var1 * var1 * ((3.0F + var1) / 4.0F) + var2 * var2 * 0.8F, 1.0F);
   }

   @Deprecated
   public static float rotlerp(float var0, float var1, float var2) {
      float var3 = var1 - var0;

      while(var3 < -180.0F) {
         var3 += 360.0F;
      }

      while(var3 >= 180.0F) {
         var3 -= 360.0F;
      }

      return var0 + var2 * var3;
   }

   @Deprecated
   public static float rotWrap(double var0) {
      while(var0 >= 180.0) {
         var0 -= 360.0;
      }

      while(var0 < -180.0) {
         var0 += 360.0;
      }

      return (float)var0;
   }

   public static float triangleWave(float var0, float var1) {
      return (Math.abs(var0 % var1 - var1 * 0.5F) - var1 * 0.25F) / (var1 * 0.25F);
   }

   public static float square(float var0) {
      return var0 * var0;
   }

   public static double square(double var0) {
      return var0 * var0;
   }

   public static int square(int var0) {
      return var0 * var0;
   }

   public static long square(long var0) {
      return var0 * var0;
   }

   public static float cube(float var0) {
      return var0 * var0 * var0;
   }

   public static double clampedMap(double var0, double var2, double var4, double var6, double var8) {
      return clampedLerp(var6, var8, inverseLerp(var0, var2, var4));
   }

   public static float clampedMap(float var0, float var1, float var2, float var3, float var4) {
      return clampedLerp(var3, var4, inverseLerp(var0, var1, var2));
   }

   public static double map(double var0, double var2, double var4, double var6, double var8) {
      return lerp(inverseLerp(var0, var2, var4), var6, var8);
   }

   public static float map(float var0, float var1, float var2, float var3, float var4) {
      return lerp(inverseLerp(var0, var1, var2), var3, var4);
   }

   public static double wobble(double var0) {
      return var0 + (2.0 * RandomSource.create((long)floor(var0 * 3000.0)).nextDouble() - 1.0) * 1.0E-7 / 2.0;
   }

   public static int roundToward(int var0, int var1) {
      return positiveCeilDiv(var0, var1) * var1;
   }

   public static int positiveCeilDiv(int var0, int var1) {
      return -Math.floorDiv(-var0, var1);
   }

   public static int randomBetweenInclusive(RandomSource var0, int var1, int var2) {
      return var0.nextInt(var2 - var1 + 1) + var1;
   }

   public static float randomBetween(RandomSource var0, float var1, float var2) {
      return var0.nextFloat() * (var2 - var1) + var1;
   }

   public static float normal(RandomSource var0, float var1, float var2) {
      return var1 + (float)var0.nextGaussian() * var2;
   }

   public static double lengthSquared(double var0, double var2) {
      return var0 * var0 + var2 * var2;
   }

   public static double length(double var0, double var2) {
      return Math.sqrt(lengthSquared(var0, var2));
   }

   public static double lengthSquared(double var0, double var2, double var4) {
      return var0 * var0 + var2 * var2 + var4 * var4;
   }

   public static double length(double var0, double var2, double var4) {
      return Math.sqrt(lengthSquared(var0, var2, var4));
   }

   public static int quantize(double var0, int var2) {
      return floor(var0 / (double)var2) * var2;
   }

   public static IntStream outFromOrigin(int var0, int var1, int var2) {
      return outFromOrigin(var0, var1, var2, 1);
   }

   public static IntStream outFromOrigin(int var0, int var1, int var2, int var3) {
      if (var1 > var2) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "upperbound %d expected to be > lowerBound %d", var2, var1));
      } else if (var3 < 1) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "steps expected to be >= 1, was %d", var3));
      } else {
         return var0 >= var1 && var0 <= var2 ? IntStream.iterate(var0, var3x -> {
            int var4 = Math.abs(var0 - var3x);
            return var0 - var4 >= var1 || var0 + var4 <= var2;
         }, var4 -> {
            boolean var5 = var4 <= var0;
            int var6 = Math.abs(var0 - var4);
            boolean var7 = var0 + var6 + var3 <= var2;
            if (!var5 || !var7) {
               int var8 = var0 - var6 - (var5 ? var3 : 0);
               if (var8 >= var1) {
                  return var8;
               }
            }

            return var0 + var6 + var3;
         }) : IntStream.empty();
      }
   }

   static {
      for(int var0 = 0; var0 < 257; ++var0) {
         double var1 = (double)var0 / 256.0;
         double var3 = Math.asin(var1);
         COS_TAB[var0] = Math.cos(var3);
         ASIN_TAB[var0] = var3;
      }
   }
}
