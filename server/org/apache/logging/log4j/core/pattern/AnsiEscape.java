package org.apache.logging.log4j.core.pattern;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.logging.log4j.core.util.Patterns;
import org.apache.logging.log4j.util.EnglishEnums;

public enum AnsiEscape {
   CSI("\u001b["),
   SUFFIX("m"),
   SEPARATOR(";"),
   NORMAL("0"),
   BRIGHT("1"),
   DIM("2"),
   UNDERLINE("3"),
   BLINK("5"),
   REVERSE("7"),
   HIDDEN("8"),
   BLACK("30"),
   FG_BLACK("30"),
   RED("31"),
   FG_RED("31"),
   GREEN("32"),
   FG_GREEN("32"),
   YELLOW("33"),
   FG_YELLOW("33"),
   BLUE("34"),
   FG_BLUE("34"),
   MAGENTA("35"),
   FG_MAGENTA("35"),
   CYAN("36"),
   FG_CYAN("36"),
   WHITE("37"),
   FG_WHITE("37"),
   DEFAULT("39"),
   FG_DEFAULT("39"),
   BG_BLACK("40"),
   BG_RED("41"),
   BG_GREEN("42"),
   BG_YELLOW("43"),
   BG_BLUE("44"),
   BG_MAGENTA("45"),
   BG_CYAN("46"),
   BG_WHITE("47");

   private static final String DEFAULT_STYLE = CSI.getCode() + SUFFIX.getCode();
   private final String code;

   private AnsiEscape(String var3) {
      this.code = var3;
   }

   public static String getDefaultStyle() {
      return DEFAULT_STYLE;
   }

   public String getCode() {
      return this.code;
   }

   public static Map<String, String> createMap(String var0, String[] var1) {
      return createMap(var0.split(Patterns.COMMA_SEPARATOR), var1);
   }

   public static Map<String, String> createMap(String[] var0, String[] var1) {
      String[] var2 = var1 != null ? (String[])var1.clone() : new String[0];
      Arrays.sort(var2);
      HashMap var3 = new HashMap();
      String[] var4 = var0;
      int var5 = var0.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String var7 = var4[var6];
         String[] var8 = var7.split(Patterns.toWhitespaceSeparator("="));
         if (var8.length > 1) {
            String var9 = var8[0].toUpperCase(Locale.ENGLISH);
            String var10 = var8[1];
            boolean var11 = Arrays.binarySearch(var2, var9) < 0;
            var3.put(var9, var11 ? createSequence(var10.split("\\s")) : var10);
         }
      }

      return var3;
   }

   public static String createSequence(String... var0) {
      if (var0 == null) {
         return getDefaultStyle();
      } else {
         StringBuilder var1 = new StringBuilder(CSI.getCode());
         boolean var2 = true;
         String[] var3 = var0;
         int var4 = var0.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];

            try {
               AnsiEscape var7 = (AnsiEscape)EnglishEnums.valueOf(AnsiEscape.class, var6.trim());
               if (!var2) {
                  var1.append(SEPARATOR.getCode());
               }

               var2 = false;
               var1.append(var7.getCode());
            } catch (Exception var8) {
            }
         }

         var1.append(SUFFIX.getCode());
         return var1.toString();
      }
   }
}
