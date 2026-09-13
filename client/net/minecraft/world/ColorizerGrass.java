package net.minecraft.world;

public class ColorizerGrass {
   private static int[] field_77481_a = new int[65536];

   public static void func_77479_a(int[] var0) {
      field_77481_a = var0;
   }

   public static int func_77480_a(double var0, double var2) {
      var2 *= var0;
      int var4 = (int)((1.0 - var0) * 255.0);
      int var5 = (int)((1.0 - var2) * 255.0);
      return field_77481_a[var5 << 8 | var4];
   }
}
