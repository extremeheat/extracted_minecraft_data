package net.minecraft.client.resources;

public class I18n {
   private static Locale field_135054_a;

   static void func_135051_a(Locale var0) {
      field_135054_a = var0;
   }

   public static String func_135052_a(String var0, Object... var1) {
      return field_135054_a.func_135023_a(var0, var1);
   }

   public static boolean func_188566_a(String var0) {
      return field_135054_a.func_188568_a(var0);
   }
}
