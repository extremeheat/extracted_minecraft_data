package net.minecraft.client.resources.language;

import java.util.IllegalFormatException;
import java.util.Locale;
import net.minecraft.locale.Language;

public class I18n {
   private static volatile Language language = Language.getInstance();

   private I18n() {
      super();
   }

   static void setLanguage(final Language locale) {
      language = locale;
   }

   public static String get(final String id, final Object... args) {
      String value = language.getOrDefault(id);

      try {
         return String.format(Locale.ROOT, value, args);
      } catch (IllegalFormatException var4) {
         return "Format error: " + value;
      }
   }

   public static boolean exists(final String id) {
      return language.has(id);
   }
}
