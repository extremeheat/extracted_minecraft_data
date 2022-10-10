package net.minecraft.world;

public class GrassColors {
   private static int[] field_77481_a = new int[65536];

   public static void func_77479_a(int[] var0) {
      field_77481_a = var0;
   }

   public static int func_77480_a(double var0, double var2) {
      var2 *= var0;
      int var4 = (int)((1.0D - var0) * 255.0D);
      int var5 = (int)((1.0D - var2) * 255.0D);
      int var6 = var5 << 8 | var4;
      return var6 > field_77481_a.length ? -65281 : field_77481_a[var6];
   }
}
