package net.minecraft.util.text;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public enum TextFormatting {
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

   private static final Map<String, TextFormatting> field_96331_x = (Map)Arrays.stream(values()).collect(Collectors.toMap((var0) -> {
      return func_175745_c(var0.field_175748_y);
   }, (var0) -> {
      return var0;
   }));
   private static final Pattern field_96330_y = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");
   private final String field_175748_y;
   private final char field_96329_z;
   private final boolean field_96303_A;
   private final String field_96304_B;
   private final int field_175747_C;
   @Nullable
   private final Integer field_211167_D;

   private static String func_175745_c(String var0) {
      return var0.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
   }

   private TextFormatting(String var3, char var4, int var5, Integer var6) {
      this(var3, var4, false, var5, var6);
   }

   private TextFormatting(String var3, char var4, boolean var5) {
      this(var3, var4, var5, -1, (Integer)null);
   }

   private TextFormatting(String var3, char var4, boolean var5, int var6, Integer var7) {
      this.field_175748_y = var3;
      this.field_96329_z = var4;
      this.field_96303_A = var5;
      this.field_175747_C = var6;
      this.field_211167_D = var7;
      this.field_96304_B = "\u00a7" + var4;
   }

   public static String func_211164_a(String var0) {
      StringBuilder var1 = new StringBuilder();
      int var2 = -1;
      int var3 = var0.length();

      while((var2 = var0.indexOf(167, var2 + 1)) != -1) {
         if (var2 < var3 - 1) {
            TextFormatting var4 = func_211165_a(var0.charAt(var2 + 1));
            if (var4 != null) {
               if (var4.func_211166_f()) {
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

   public int func_175746_b() {
      return this.field_175747_C;
   }

   public boolean func_96301_b() {
      return this.field_96303_A;
   }

   public boolean func_96302_c() {
      return !this.field_96303_A && this != RESET;
   }

   @Nullable
   public Integer func_211163_e() {
      return this.field_211167_D;
   }

   public boolean func_211166_f() {
      return !this.field_96303_A;
   }

   public String func_96297_d() {
      return this.name().toLowerCase(Locale.ROOT);
   }

   public String toString() {
      return this.field_96304_B;
   }

   @Nullable
   public static String func_110646_a(@Nullable String var0) {
      return var0 == null ? null : field_96330_y.matcher(var0).replaceAll("");
   }

   @Nullable
   public static TextFormatting func_96300_b(@Nullable String var0) {
      return var0 == null ? null : (TextFormatting)field_96331_x.get(func_175745_c(var0));
   }

   @Nullable
   public static TextFormatting func_175744_a(int var0) {
      if (var0 < 0) {
         return RESET;
      } else {
         TextFormatting[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            TextFormatting var4 = var1[var3];
            if (var4.func_175746_b() == var0) {
               return var4;
            }
         }

         return null;
      }
   }

   @Nullable
   public static TextFormatting func_211165_a(char var0) {
      char var1 = Character.toString(var0).toLowerCase(Locale.ROOT).charAt(0);
      TextFormatting[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         TextFormatting var5 = var2[var4];
         if (var5.field_96329_z == var1) {
            return var5;
         }
      }

      return null;
   }

   public static Collection<String> func_96296_a(boolean var0, boolean var1) {
      ArrayList var2 = Lists.newArrayList();
      TextFormatting[] var3 = values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         TextFormatting var6 = var3[var5];
         if ((!var6.func_96302_c() || var0) && (!var6.func_96301_b() || var1)) {
            var2.add(var6.func_96297_d());
         }
      }

      return var2;
   }
}
