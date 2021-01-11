package net.minecraft.realms;

import java.util.Random;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.StringUtils;

public class RealmsMth {
   public RealmsMth() {
      super();
   }

   public static float sin(float var0) {
      return MathHelper.func_76126_a(var0);
   }

   public static double nextDouble(Random var0, double var1, double var3) {
      return MathHelper.func_82716_a(var0, var1, var3);
   }

   public static int ceil(float var0) {
      return MathHelper.func_76123_f(var0);
   }

   public static int floor(double var0) {
      return MathHelper.func_76128_c(var0);
   }

   public static int intFloorDiv(int var0, int var1) {
      return MathHelper.func_76137_a(var0, var1);
   }

   public static float abs(float var0) {
      return MathHelper.func_76135_e(var0);
   }

   public static int clamp(int var0, int var1, int var2) {
      return MathHelper.func_76125_a(var0, var1, var2);
   }

   public static double clampedLerp(double var0, double var2, double var4) {
      return MathHelper.func_151238_b(var0, var2, var4);
   }

   public static int ceil(double var0) {
      return MathHelper.func_76143_f(var0);
   }

   public static boolean isEmpty(String var0) {
      return StringUtils.isEmpty(var0);
   }

   public static long lfloor(double var0) {
      return MathHelper.func_76124_d(var0);
   }

   public static float sqrt(double var0) {
      return MathHelper.func_76133_a(var0);
   }

   public static double clamp(double var0, double var2, double var4) {
      return MathHelper.func_151237_a(var0, var2, var4);
   }

   public static int getInt(String var0, int var1) {
      return MathHelper.func_82715_a(var0, var1);
   }

   public static double getDouble(String var0, double var1) {
      return MathHelper.func_82712_a(var0, var1);
   }

   public static int log2(int var0) {
      return MathHelper.func_151239_c(var0);
   }

   public static int absFloor(double var0) {
      return MathHelper.func_154353_e(var0);
   }

   public static int smallestEncompassingPowerOfTwo(int var0) {
      return MathHelper.func_151236_b(var0);
   }

   public static float sqrt(float var0) {
      return MathHelper.func_76129_c(var0);
   }

   public static float cos(float var0) {
      return MathHelper.func_76134_b(var0);
   }

   public static int getInt(String var0, int var1, int var2) {
      return MathHelper.func_82714_a(var0, var1, var2);
   }

   public static int fastFloor(double var0) {
      return MathHelper.func_76140_b(var0);
   }

   public static double absMax(double var0, double var2) {
      return MathHelper.func_76132_a(var0, var2);
   }

   public static float nextFloat(Random var0, float var1, float var2) {
      return MathHelper.func_151240_a(var0, var1, var2);
   }

   public static double wrapDegrees(double var0) {
      return MathHelper.func_76138_g(var0);
   }

   public static float wrapDegrees(float var0) {
      return MathHelper.func_76142_g(var0);
   }

   public static float clamp(float var0, float var1, float var2) {
      return MathHelper.func_76131_a(var0, var1, var2);
   }

   public static double getDouble(String var0, double var1, double var3) {
      return MathHelper.func_82713_a(var0, var1, var3);
   }

   public static int roundUp(int var0, int var1) {
      return MathHelper.func_154354_b(var0, var1);
   }

   public static double average(long[] var0) {
      return MathHelper.func_76127_a(var0);
   }

   public static int floor(float var0) {
      return MathHelper.func_76141_d(var0);
   }

   public static int abs(int var0) {
      return MathHelper.func_76130_a(var0);
   }

   public static int nextInt(Random var0, int var1, int var2) {
      return MathHelper.func_76136_a(var0, var1, var2);
   }
}
