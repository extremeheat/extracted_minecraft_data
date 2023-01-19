package net.minecraft.client.resources.language;

import java.util.IllegalFormatException;
import net.minecraft.locale.Language;

public class I18n {
   private static volatile Language language = Language.getInstance();

   private I18n() {
      super();
   }

   static void setLanguage(Language var0) {
      language = var0;
   }

   public static String get(String var0, Object... var1) {
      String var2 = language.getOrDefault(var0);

      try {
         return String.format(var2, var1);
      } catch (IllegalFormatException var4) {
         return "Format error: " + var2;
      }
   }

   public static boolean exists(String var0) {
      return language.has(var0);
   }
}
