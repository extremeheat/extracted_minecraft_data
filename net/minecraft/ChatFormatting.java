package net.minecraft;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public enum ChatFormatting {
   BLACK("BLACK", '0', 0, 0),
   DARK_BLUE("DARK_BLUE", '1', 1, 170),
   DARK_GREEN("DARK_GREEN", '2', 2, 43520),
   DARK_AQUA("DARK_AQUA", '3', 3, 43690),
   DARK_RED("DARK_RED", '4', 4, 11141120),
   DARK_PURPLE("DARK_PURPLE", '5', 5, 11141290),
   GOLD("GOLD", '6', 6, 16755200),
   GRAY("GRAY", '7', 7, 11184810),
   DARK_GRAY("DARK_GRAY", '8', 8, 5592405),
   BLUE("BLUE", '9', 9, 5592575),
   GREEN("GREEN", 'a', 10, 5635925),
   AQUA("AQUA", 'b', 11, 5636095),
   RED("RED", 'c', 12, 16733525),
   LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13, 16733695),
   YELLOW("YELLOW", 'e', 14, 16777045),
   WHITE("WHITE", 'f', 15, 16777215),
   OBFUSCATED("OBFUSCATED", 'k', true),
   BOLD("BOLD", 'l', true),
   STRIKETHROUGH("STRIKETHROUGH", 'm', true),
   UNDERLINE("UNDERLINE", 'n', true),
   ITALIC("ITALIC", 'o', true),
   RESET("RESET", 'r', -1, (Integer)null);

   private static final Map FORMATTING_BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap((var0) -> {
      return cleanName(var0.name);
   }, (var0) -> {
      return var0;
   }));
   private static final Pattern STRIP_FORMATTING_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
   private final String name;
   private final char code;
   private final boolean isFormat;
   private final String toString;
   private final int id;
   @Nullable
   private final Integer color;

   private static String cleanName(String var0) {
      return var0.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
   }

   private ChatFormatting(String var3, char var4, int var5, Integer var6) {
      this(var3, var4, false, var5, var6);
   }

   private ChatFormatting(String var3, char var4, boolean var5) {
      this(var3, var4, var5, -1, (Integer)null);
   }

   private ChatFormatting(String var3, char var4, boolean var5, int var6, Integer var7) {
      this.name = var3;
      this.code = var4;
      this.isFormat = var5;
      this.id = var6;
      this.color = var7;
      this.toString = "§" + var4;
   }

   public static String getLastColors(String var0) {
      StringBuilder var1 = new StringBuilder();
      int var2 = -1;
      int var3 = var0.length();

      while((var2 = var0.indexOf(167, var2 + 1)) != -1) {
         if (var2 < var3 - 1) {
            ChatFormatting var4 = getByCode(var0.charAt(var2 + 1));
            if (var4 != null) {
               if (var4.shouldReset()) {
                  var1.setLength(0);
               }

               if (var4 != RESET) {
                  var1.append(var4);
               }
            }
         }
      }

      return var1.toString();
   }

   public int getId() {
      return this.id;
   }

   public boolean isFormat() {
      return this.isFormat;
   }

   public boolean isColor() {
      return !this.isFormat && this != RESET;
   }

   @Nullable
   public Integer getColor() {
      return this.color;
   }

   public boolean shouldReset() {
      return !this.isFormat;
   }

   public String getName() {
      return this.name().toLowerCase(Locale.ROOT);
   }

   public String toString() {
      return this.toString;
   }

   @Nullable
   public static String stripFormatting(@Nullable String var0) {
      return var0 == null ? null : STRIP_FORMATTING_PATTERN.matcher(var0).replaceAll("");
   }

   @Nullable
   public static ChatFormatting getByName(@Nullable String var0) {
      return var0 == null ? null : (ChatFormatting)FORMATTING_BY_NAME.get(cleanName(var0));
   }

   @Nullable
   public static ChatFormatting getById(int var0) {
      if (var0 < 0) {
         return RESET;
      } else {
         ChatFormatting[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            ChatFormatting var4 = var1[var3];
            if (var4.getId() == var0) {
               return var4;
            }
         }

         return null;
      }
   }

   @Nullable
   public static ChatFormatting getByCode(char var0) {
      char var1 = Character.toString(var0).toLowerCase(Locale.ROOT).charAt(0);
      ChatFormatting[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ChatFormatting var5 = var2[var4];
         if (var5.code == var1) {
            return var5;
         }
      }

      return null;
   }

   public static Collection getNames(boolean var0, boolean var1) {
      ArrayList var2 = Lists.newArrayList();
      ChatFormatting[] var3 = values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ChatFormatting var6 = var3[var5];
         if ((!var6.isColor() || var0) && (!var6.isFormat() || var1)) {
            var2.add(var6.getName());
         }
      }

      return var2;
   }
}
