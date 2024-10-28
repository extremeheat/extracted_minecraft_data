package net.minecraft.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class StringUtil {
   private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
   private static final Pattern LINE_PATTERN = Pattern.compile("\\r\\n|\\v");
   private static final Pattern LINE_END_PATTERN = Pattern.compile("(?:\\r\\n|\\v)$");

   public StringUtil() {
      super();
   }

   public static String formatTickDuration(int var0, float var1) {
      int var2 = Mth.floor((float)var0 / var1);
      int var3 = var2 / 60;
      var2 %= 60;
      int var4 = var3 / 60;
      var3 %= 60;
      return var4 > 0 ? String.format(Locale.ROOT, "%02d:%02d:%02d", var4, var3, var2) : String.format(Locale.ROOT, "%02d:%02d", var3, var2);
   }

   public static String stripColor(String var0) {
      return STRIP_COLOR_PATTERN.matcher(var0).replaceAll("");
   }

   public static boolean isNullOrEmpty(@Nullable String var0) {
      return StringUtils.isEmpty(var0);
   }

   public static String truncateStringIfNecessary(String var0, int var1, boolean var2) {
      if (var0.length() <= var1) {
         return var0;
      } else if (var2 && var1 > 3) {
         String var10000 = var0.substring(0, var1 - 3);
         return var10000 + "...";
      } else {
         return var0.substring(0, var1);
      }
   }

   public static int lineCount(String var0) {
      if (var0.isEmpty()) {
         return 0;
      } else {
         Matcher var1 = LINE_PATTERN.matcher(var0);

         int var2;
         for(var2 = 1; var1.find(); ++var2) {
         }

         return var2;
      }
   }

   public static boolean endsWithNewLine(String var0) {
      return LINE_END_PATTERN.matcher(var0).find();
   }

   public static String trimChatMessage(String var0) {
      return truncateStringIfNecessary(var0, 256, false);
   }

   public static boolean isAllowedChatCharacter(char var0) {
      return var0 != 167 && var0 >= ' ' && var0 != 127;
   }

   public static boolean isValidPlayerName(String var0) {
      return var0.length() > 16 ? false : var0.chars().filter((var0x) -> {
         return var0x <= 32 || var0x >= 127;
      }).findAny().isEmpty();
   }

   public static String filterText(String var0) {
      return filterText(var0, false);
   }

   public static String filterText(String var0, boolean var1) {
      StringBuilder var2 = new StringBuilder();
      char[] var3 = var0.toCharArray();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         char var6 = var3[var5];
         if (isAllowedChatCharacter(var6)) {
            var2.append(var6);
         } else if (var1 && var6 == '\n') {
            var2.append(var6);
         }
      }

      return var2.toString();
   }

   public static boolean isWhitespace(int var0) {
      return Character.isWhitespace(var0) || Character.isSpaceChar(var0);
   }

   public static boolean isBlank(@Nullable String var0) {
      return var0 != null && var0.length() != 0 ? var0.chars().allMatch(StringUtil::isWhitespace) : true;
   }
}
