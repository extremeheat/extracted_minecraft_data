package net.minecraft.entity.boss;

public final class BossStatus {
   public static float field_82828_a;
   public static int field_82826_b;
   public static String field_82827_c;
   public static boolean field_82825_d;

   public static void func_82824_a(IBossDisplayData var0, boolean var1) {
      field_82828_a = var0.func_110143_aJ() / var0.func_110138_aP();
      field_82826_b = 100;
      field_82827_c = var0.func_145748_c_().func_150254_d();
      field_82825_d = var1;
   }
}
