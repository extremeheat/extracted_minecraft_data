package net.minecraft.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

public enum EnumChatFormatting {
   BLACK("BLACK", '0', 0),
   DARK_BLUE("DARK_BLUE", '1', 1),
   DARK_GREEN("DARK_GREEN", '2', 2),
   DARK_AQUA("DARK_AQUA", '3', 3),
   DARK_RED("DARK_RED", '4', 4),
   DARK_PURPLE("DARK_PURPLE", '5', 5),
   GOLD("GOLD", '6', 6),
   GRAY("GRAY", '7', 7),
   DARK_GRAY("DARK_GRAY", '8', 8),
   BLUE("BLUE", '9', 9),
   GREEN("GREEN", 'a', 10),
   AQUA("AQUA", 'b', 11),
   RED("RED", 'c', 12),
   LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13),
   YELLOW("YELLOW", 'e', 14),
   WHITE("WHITE", 'f', 15),
   OBFUSCATED("OBFUSCATED", 'k', true),
   BOLD("BOLD", 'l', true),
   STRIKETHROUGH("STRIKETHROUGH", 'm', true),
   UNDERLINE("UNDERLINE", 'n', true),
   ITALIC("ITALIC", 'o', true),
   RESET("RESET", 'r', -1);

   private static final Map<String, EnumChatFormatting> field_96331_x = Maps.newHashMap();
   private static final Pattern field_96330_y = Pattern.compile("(?i)" + String.valueOf('\u00a7') + "[0-9A-FK-OR]");
   private final String field_175748_y;
   private final char field_96329_z;
   private final boolean field_96303_A;
   private final String field_96304_B;
   private final int field_175747_C;

   private static String func_175745_c(String var0) {
      return var0.toLowerCase().replaceAll("[^a-z]", "");
   }

   private EnumChatFormatting(String var3, char var4, int var5) {
      this(var3, var4, false, var5);
   }

   private EnumChatFormatting(String var3, char var4, boolean var5) {
      this(var3, var4, var5, -1);
   }

   private EnumChatFormatting(String var3, char var4, boolean var5, int var6) {
      this.field_175748_y = var3;
      this.field_96329_z = var4;
      this.field_96303_A = var5;
      this.field_175747_C = var6;
      this.field_96304_B = "\u00a7" + var4;
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

   public String func_96297_d() {
      return this.name().toLowerCase();
   }

   public String toString() {
      return this.field_96304_B;
   }

   public static String func_110646_a(String var0) {
      return var0 == null ? null : field_96330_y.matcher(var0).replaceAll("");
   }

   public static EnumChatFormatting func_96300_b(String var0) {
      return var0 == null ? null : (EnumChatFormatting)field_96331_x.get(func_175745_c(var0));
   }

   public static EnumChatFormatting func_175744_a(int var0) {
      if (var0 < 0) {
         return RESET;
      } else {
         EnumChatFormatting[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            EnumChatFormatting var4 = var1[var3];
            if (var4.func_175746_b() == var0) {
               return var4;
            }
         }

         return null;
      }
   }

   public static Collection<String> func_96296_a(boolean var0, boolean var1) {
      ArrayList var2 = Lists.newArrayList();
      EnumChatFormatting[] var3 = values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EnumChatFormatting var6 = var3[var5];
         if ((!var6.func_96302_c() || var0) && (!var6.func_96301_b() || var1)) {
            var2.add(var6.func_96297_d());
         }
      }

      return var2;
   }

   static {
      EnumChatFormatting[] var0 = values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         EnumChatFormatting var3 = var0[var2];
         field_96331_x.put(func_175745_c(var3.field_175748_y), var3);
      }

   }
}
