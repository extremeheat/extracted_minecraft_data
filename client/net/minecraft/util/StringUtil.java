package net.minecraft.util;

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

   public static String formatTickDuration(int var0) {
      int var1 = var0 / 20;
      int var2 = var1 / 60;
      var1 %= 60;
      return var1 < 10 ? var2 + ":0" + var1 : var2 + ":" + var1;
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
      } else {
         return var2 && var1 > 3 ? var0.substring(0, var1 - 3) + "..." : var0.substring(0, var1);
      }
   }

   public static int lineCount(String var0) {
      if (var0.isEmpty()) {
         return 0;
      } else {
         Matcher var1 = LINE_PATTERN.matcher(var0);
         int var2 = 1;

         while(var1.find()) {
            ++var2;
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
}
