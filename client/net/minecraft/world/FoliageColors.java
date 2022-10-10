package net.minecraft.world;

public class FoliageColors {
   private static int[] field_77471_a = new int[65536];

   public static void func_77467_a(int[] var0) {
      field_77471_a = var0;
   }

   public static int func_77470_a(double var0, double var2) {
      var2 *= var0;
      int var4 = (int)((1.0D - var0) * 255.0D);
      int var5 = (int)((1.0D - var2) * 255.0D);
      return field_77471_a[var5 << 8 | var4];
   }

   public static int func_77466_a() {
      return 6396257;
   }

   public static int func_77469_b() {
      return 8431445;
   }

   public static int func_77468_c() {
      return 4764952;
   }
}
