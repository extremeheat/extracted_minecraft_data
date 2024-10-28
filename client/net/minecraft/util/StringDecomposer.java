package net.minecraft.util;

import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

public class StringDecomposer {
   private static final char REPLACEMENT_CHAR = '\ufffd';
   private static final Optional<Object> STOP_ITERATION;

   public StringDecomposer() {
      super();
   }

   private static boolean feedChar(Style var0, FormattedCharSink var1, int var2, char var3) {
      return Character.isSurrogate(var3) ? var1.accept(var2, var0, 65533) : var1.accept(var2, var0, var3);
   }

   public static boolean iterate(String var0, Style var1, FormattedCharSink var2) {
      int var3 = var0.length();

      for(int var4 = 0; var4 < var3; ++var4) {
         char var5 = var0.charAt(var4);
         if (Character.isHighSurrogate(var5)) {
            if (var4 + 1 >= var3) {
               if (!var2.accept(var4, var1, 65533)) {
                  return false;
               }
               break;
            }

            char var6 = var0.charAt(var4 + 1);
            if (Character.isLowSurrogate(var6)) {
               if (!var2.accept(var4, var1, Character.toCodePoint(var5, var6))) {
                  return false;
               }

               ++var4;
            } else if (!var2.accept(var4, var1, 65533)) {
               return false;
            }
         } else if (!feedChar(var1, var2, var4, var5)) {
            return false;
         }
      }

      return true;
   }

   public static boolean iterateBackwards(String var0, Style var1, FormattedCharSink var2) {
      int var3 = var0.length();

      for(int var4 = var3 - 1; var4 >= 0; --var4) {
         char var5 = var0.charAt(var4);
         if (Character.isLowSurrogate(var5)) {
            if (var4 - 1 < 0) {
               if (!var2.accept(0, var1, 65533)) {
                  return false;
               }
               break;
            }

            char var6 = var0.charAt(var4 - 1);
            if (Character.isHighSurrogate(var6)) {
               --var4;
               if (!var2.accept(var4, var1, Character.toCodePoint(var6, var5))) {
                  return false;
               }
            } else if (!var2.accept(var4, var1, 65533)) {
               return false;
            }
         } else if (!feedChar(var1, var2, var4, var5)) {
            return false;
         }
      }

      return true;
   }

   public static boolean iterateFormatted(String var0, Style var1, FormattedCharSink var2) {
      return iterateFormatted(var0, 0, var1, var2);
   }

   public static boolean iterateFormatted(String var0, int var1, Style var2, FormattedCharSink var3) {
      return iterateFormatted(var0, var1, var2, var2, var3);
   }

   public static boolean iterateFormatted(String var0, int var1, Style var2, Style var3, FormattedCharSink var4) {
      int var5 = var0.length();
      Style var6 = var2;

      for(int var7 = var1; var7 < var5; ++var7) {
         char var8 = var0.charAt(var7);
         char var9;
         if (var8 == 167) {
            if (var7 + 1 >= var5) {
               break;
            }

            var9 = var0.charAt(var7 + 1);
            ChatFormatting var10 = ChatFormatting.getByCode(var9);
            if (var10 != null) {
               var6 = var10 == ChatFormatting.RESET ? var3 : var6.applyLegacyFormat(var10);
            }

            ++var7;
         } else if (Character.isHighSurrogate(var8)) {
            if (var7 + 1 >= var5) {
               if (!var4.accept(var7, var6, 65533)) {
                  return false;
               }
               break;
            }

            var9 = var0.charAt(var7 + 1);
            if (Character.isLowSurrogate(var9)) {
               if (!var4.accept(var7, var6, Character.toCodePoint(var8, var9))) {
                  return false;
               }

               ++var7;
            } else if (!var4.accept(var7, var6, 65533)) {
               return false;
            }
         } else if (!feedChar(var6, var4, var7, var8)) {
            return false;
         }
      }

      return true;
   }

   public static boolean iterateFormatted(FormattedText var0, Style var1, FormattedCharSink var2) {
      return var0.visit((var1x, var2x) -> {
         return iterateFormatted(var2x, 0, var1x, var2) ? Optional.empty() : STOP_ITERATION;
      }, var1).isEmpty();
   }

   public static String filterBrokenSurrogates(String var0) {
      StringBuilder var1 = new StringBuilder();
      iterate(var0, Style.EMPTY, (var1x, var2, var3) -> {
         var1.appendCodePoint(var3);
         return true;
      });
      return var1.toString();
   }

   public static String getPlainText(FormattedText var0) {
      StringBuilder var1 = new StringBuilder();
      iterateFormatted(var0, Style.EMPTY, (var1x, var2, var3) -> {
         var1.appendCodePoint(var3);
         return true;
      });
      return var1.toString();
   }

   static {
      STOP_ITERATION = Optional.of(Unit.INSTANCE);
   }
}
