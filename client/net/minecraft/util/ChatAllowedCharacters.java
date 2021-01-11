package net.minecraft.util;

public class ChatAllowedCharacters {
   public static final char[] field_71567_b = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '"', ':'};

   public static boolean func_71566_a(char var0) {
      return var0 != 167 && var0 >= ' ' && var0 != 127;
   }

   public static String func_71565_a(String var0) {
      StringBuilder var1 = new StringBuilder();
      char[] var2 = var0.toCharArray();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         char var5 = var2[var4];
         if (func_71566_a(var5)) {
            var1.append(var5);
         }
      }

      return var1.toString();
   }
}
