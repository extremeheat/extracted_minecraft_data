package net.minecraft.util;

import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class StringUtils {
   private static final Pattern field_76339_a = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

   public static String func_76337_a(int var0) {
      int var1 = var0 / 20;
      int var2 = var1 / 60;
      var1 %= 60;
      return var1 < 10 ? var2 + ":0" + var1 : var2 + ":" + var1;
   }

   public static String func_76338_a(String var0) {
      return field_76339_a.matcher(var0).replaceAll("");
   }

   public static boolean func_151246_b(@Nullable String var0) {
      return org.apache.commons.lang3.StringUtils.isEmpty(var0);
   }
}
