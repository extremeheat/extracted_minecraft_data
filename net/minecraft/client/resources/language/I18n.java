package net.minecraft.client.resources.language;

public class I18n {
   private static Locale locale;

   static void setLocale(Locale var0) {
      locale = var0;
   }

   public static String get(String var0, Object... var1) {
      return locale.get(var0, var1);
   }

   public static boolean exists(String var0) {
      return locale.has(var0);
   }
}
