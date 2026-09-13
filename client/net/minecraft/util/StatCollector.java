package net.minecraft.util;

public class StatCollector {
   private static StringTranslate field_74839_a = StringTranslate.func_74808_a();
   private static StringTranslate field_150828_b = new StringTranslate();

   public static String func_74838_a(String var0) {
      return field_74839_a.func_74805_b(var0);
   }

   public static String func_74837_a(String var0, Object... var1) {
      return field_74839_a.func_74803_a(var0, var1);
   }

   public static String func_150826_b(String var0) {
      return field_150828_b.func_74805_b(var0);
   }

   public static boolean func_94522_b(String var0) {
      return field_74839_a.func_94520_b(var0);
   }

   public static long func_150827_a() {
      return field_74839_a.func_150510_c();
   }
}
