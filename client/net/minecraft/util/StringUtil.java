package net.minecraft.util;

import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class StringUtil {
   private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

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
}
