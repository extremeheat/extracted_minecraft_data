package com.google.common.base;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public final class Ascii {
   public static final byte NUL = 0;
   public static final byte SOH = 1;
   public static final byte STX = 2;
   public static final byte ETX = 3;
   public static final byte EOT = 4;
   public static final byte ENQ = 5;
   public static final byte ACK = 6;
   public static final byte BEL = 7;
   public static final byte BS = 8;
   public static final byte HT = 9;
   public static final byte LF = 10;
   public static final byte NL = 10;
   public static final byte VT = 11;
   public static final byte FF = 12;
   public static final byte CR = 13;
   public static final byte SO = 14;
   public static final byte SI = 15;
   public static final byte DLE = 16;
   public static final byte DC1 = 17;
   public static final byte XON = 17;
   public static final byte DC2 = 18;
   public static final byte DC3 = 19;
   public static final byte XOFF = 19;
   public static final byte DC4 = 20;
   public static final byte NAK = 21;
   public static final byte SYN = 22;
   public static final byte ETB = 23;
   public static final byte CAN = 24;
   public static final byte EM = 25;
   public static final byte SUB = 26;
   public static final byte ESC = 27;
   public static final byte FS = 28;
   public static final byte GS = 29;
   public static final byte RS = 30;
   public static final byte US = 31;
   public static final byte SP = 32;
   public static final byte SPACE = 32;
   public static final byte DEL = 127;
   public static final char MIN = '\u0000';
   public static final char MAX = '\u007f';

   private Ascii() {
      super();
   }

   public static String toLowerCase(String var0) {
      int var1 = var0.length();

      for(int var2 = 0; var2 < var1; ++var2) {
         if (isUpperCase(var0.charAt(var2))) {
            char[] var3;
            for(var3 = var0.toCharArray(); var2 < var1; ++var2) {
               char var4 = var3[var2];
               if (isUpperCase(var4)) {
                  var3[var2] = (char)(var4 ^ 32);
               }
            }

            return String.valueOf(var3);
         }
      }

      return var0;
   }

   public static String toLowerCase(CharSequence var0) {
      if (var0 instanceof String) {
         return toLowerCase((String)var0);
      } else {
         char[] var1 = new char[var0.length()];

         for(int var2 = 0; var2 < var1.length; ++var2) {
            var1[var2] = toLowerCase(var0.charAt(var2));
         }

         return String.valueOf(var1);
      }
   }

   public static char toLowerCase(char var0) {
      return isUpperCase(var0) ? (char)(var0 ^ 32) : var0;
   }

   public static String toUpperCase(String var0) {
      int var1 = var0.length();

      for(int var2 = 0; var2 < var1; ++var2) {
         if (isLowerCase(var0.charAt(var2))) {
            char[] var3;
            for(var3 = var0.toCharArray(); var2 < var1; ++var2) {
               char var4 = var3[var2];
               if (isLowerCase(var4)) {
                  var3[var2] = (char)(var4 & 95);
               }
            }

            return String.valueOf(var3);
         }
      }

      return var0;
   }

   public static String toUpperCase(CharSequence var0) {
      if (var0 instanceof String) {
         return toUpperCase((String)var0);
      } else {
         char[] var1 = new char[var0.length()];

         for(int var2 = 0; var2 < var1.length; ++var2) {
            var1[var2] = toUpperCase(var0.charAt(var2));
         }

         return String.valueOf(var1);
      }
   }

   public static char toUpperCase(char var0) {
      return isLowerCase(var0) ? (char)(var0 & 95) : var0;
   }

   public static boolean isLowerCase(char var0) {
      return var0 >= 'a' && var0 <= 'z';
   }

   public static boolean isUpperCase(char var0) {
      return var0 >= 'A' && var0 <= 'Z';
   }

   public static String truncate(CharSequence var0, int var1, String var2) {
      Preconditions.checkNotNull(var0);
      int var3 = var1 - var2.length();
      Preconditions.checkArgument(var3 >= 0, "maxLength (%s) must be >= length of the truncation indicator (%s)", var1, var2.length());
      if (((CharSequence)var0).length() <= var1) {
         String var4 = ((CharSequence)var0).toString();
         if (var4.length() <= var1) {
            return var4;
         }

         var0 = var4;
      }

      return (new StringBuilder(var1)).append((CharSequence)var0, 0, var3).append(var2).toString();
   }

   public static boolean equalsIgnoreCase(CharSequence var0, CharSequence var1) {
      int var2 = var0.length();
      if (var0 == var1) {
         return true;
      } else if (var2 != var1.length()) {
         return false;
      } else {
         for(int var3 = 0; var3 < var2; ++var3) {
            char var4 = var0.charAt(var3);
            char var5 = var1.charAt(var3);
            if (var4 != var5) {
               int var6 = getAlphaIndex(var4);
               if (var6 >= 26 || var6 != getAlphaIndex(var5)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   private static int getAlphaIndex(char var0) {
      return (char)((var0 | 32) - 97);
   }
}
