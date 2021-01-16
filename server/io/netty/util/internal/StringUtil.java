package io.netty.util.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class StringUtil {
   public static final String EMPTY_STRING = "";
   public static final String NEWLINE = SystemPropertyUtil.get("line.separator", "\n");
   public static final char DOUBLE_QUOTE = '"';
   public static final char COMMA = ',';
   public static final char LINE_FEED = '\n';
   public static final char CARRIAGE_RETURN = '\r';
   public static final char TAB = '\t';
   public static final char SPACE = ' ';
   private static final String[] BYTE2HEX_PAD = new String[256];
   private static final String[] BYTE2HEX_NOPAD = new String[256];
   private static final int CSV_NUMBER_ESCAPE_CHARACTERS = 7;
   private static final char PACKAGE_SEPARATOR_CHAR = '.';

   private StringUtil() {
      super();
   }

   public static String substringAfter(String var0, char var1) {
      int var2 = var0.indexOf(var1);
      return var2 >= 0 ? var0.substring(var2 + 1) : null;
   }

   public static boolean commonSuffixOfLength(String var0, String var1, int var2) {
      return var0 != null && var1 != null && var2 >= 0 && var0.regionMatches(var0.length() - var2, var1, var1.length() - var2, var2);
   }

   public static String byteToHexStringPadded(int var0) {
      return BYTE2HEX_PAD[var0 & 255];
   }

   public static <T extends Appendable> T byteToHexStringPadded(T var0, int var1) {
      try {
         var0.append(byteToHexStringPadded(var1));
      } catch (IOException var3) {
         PlatformDependent.throwException(var3);
      }

      return var0;
   }

   public static String toHexStringPadded(byte[] var0) {
      return toHexStringPadded(var0, 0, var0.length);
   }

   public static String toHexStringPadded(byte[] var0, int var1, int var2) {
      return ((StringBuilder)toHexStringPadded(new StringBuilder(var2 << 1), var0, var1, var2)).toString();
   }

   public static <T extends Appendable> T toHexStringPadded(T var0, byte[] var1) {
      return toHexStringPadded(var0, var1, 0, var1.length);
   }

   public static <T extends Appendable> T toHexStringPadded(T var0, byte[] var1, int var2, int var3) {
      int var4 = var2 + var3;

      for(int var5 = var2; var5 < var4; ++var5) {
         byteToHexStringPadded(var0, var1[var5]);
      }

      return var0;
   }

   public static String byteToHexString(int var0) {
      return BYTE2HEX_NOPAD[var0 & 255];
   }

   public static <T extends Appendable> T byteToHexString(T var0, int var1) {
      try {
         var0.append(byteToHexString(var1));
      } catch (IOException var3) {
         PlatformDependent.throwException(var3);
      }

      return var0;
   }

   public static String toHexString(byte[] var0) {
      return toHexString(var0, 0, var0.length);
   }

   public static String toHexString(byte[] var0, int var1, int var2) {
      return ((StringBuilder)toHexString(new StringBuilder(var2 << 1), var0, var1, var2)).toString();
   }

   public static <T extends Appendable> T toHexString(T var0, byte[] var1) {
      return toHexString(var0, var1, 0, var1.length);
   }

   public static <T extends Appendable> T toHexString(T var0, byte[] var1, int var2, int var3) {
      assert var3 >= 0;

      if (var3 == 0) {
         return var0;
      } else {
         int var4 = var2 + var3;
         int var5 = var4 - 1;

         int var6;
         for(var6 = var2; var6 < var5 && var1[var6] == 0; ++var6) {
         }

         byteToHexString(var0, var1[var6++]);
         int var7 = var4 - var6;
         toHexStringPadded(var0, var1, var6, var7);
         return var0;
      }
   }

   public static int decodeHexNibble(char var0) {
      if (var0 >= '0' && var0 <= '9') {
         return var0 - 48;
      } else if (var0 >= 'A' && var0 <= 'F') {
         return var0 - 55;
      } else {
         return var0 >= 'a' && var0 <= 'f' ? var0 - 87 : -1;
      }
   }

   public static byte decodeHexByte(CharSequence var0, int var1) {
      int var2 = decodeHexNibble(var0.charAt(var1));
      int var3 = decodeHexNibble(var0.charAt(var1 + 1));
      if (var2 != -1 && var3 != -1) {
         return (byte)((var2 << 4) + var3);
      } else {
         throw new IllegalArgumentException(String.format("invalid hex byte '%s' at index %d of '%s'", var0.subSequence(var1, var1 + 2), var1, var0));
      }
   }

   public static byte[] decodeHexDump(CharSequence var0, int var1, int var2) {
      if (var2 >= 0 && (var2 & 1) == 0) {
         if (var2 == 0) {
            return EmptyArrays.EMPTY_BYTES;
         } else {
            byte[] var3 = new byte[var2 >>> 1];

            for(int var4 = 0; var4 < var2; var4 += 2) {
               var3[var4 >>> 1] = decodeHexByte(var0, var1 + var4);
            }

            return var3;
         }
      } else {
         throw new IllegalArgumentException("length: " + var2);
      }
   }

   public static byte[] decodeHexDump(CharSequence var0) {
      return decodeHexDump(var0, 0, var0.length());
   }

   public static String simpleClassName(Object var0) {
      return var0 == null ? "null_object" : simpleClassName(var0.getClass());
   }

   public static String simpleClassName(Class<?> var0) {
      String var1 = ((Class)ObjectUtil.checkNotNull(var0, "clazz")).getName();
      int var2 = var1.lastIndexOf(46);
      return var2 > -1 ? var1.substring(var2 + 1) : var1;
   }

   public static CharSequence escapeCsv(CharSequence var0) {
      return escapeCsv(var0, false);
   }

   public static CharSequence escapeCsv(CharSequence var0, boolean var1) {
      int var2 = ((CharSequence)ObjectUtil.checkNotNull(var0, "value")).length();
      int var3;
      int var4;
      if (var1) {
         var3 = indexOfFirstNonOwsChar(var0, var2);
         var4 = indexOfLastNonOwsChar(var0, var3, var2);
      } else {
         var3 = 0;
         var4 = var2 - 1;
      }

      if (var3 > var4) {
         return "";
      } else {
         int var5 = -1;
         boolean var6 = false;
         if (isDoubleQuote(var0.charAt(var3))) {
            var6 = isDoubleQuote(var0.charAt(var4)) && var4 > var3;
            if (var6) {
               ++var3;
               --var4;
            } else {
               var5 = var3;
            }
         }

         if (var5 < 0) {
            int var7;
            if (var6) {
               for(var7 = var3; var7 <= var4; ++var7) {
                  if (isDoubleQuote(var0.charAt(var7))) {
                     if (var7 == var4 || !isDoubleQuote(var0.charAt(var7 + 1))) {
                        var5 = var7;
                        break;
                     }

                     ++var7;
                  }
               }
            } else {
               for(var7 = var3; var7 <= var4; ++var7) {
                  char var8 = var0.charAt(var7);
                  if (var8 == '\n' || var8 == '\r' || var8 == ',') {
                     var5 = var7;
                     break;
                  }

                  if (isDoubleQuote(var8)) {
                     if (var7 == var4 || !isDoubleQuote(var0.charAt(var7 + 1))) {
                        var5 = var7;
                        break;
                     }

                     ++var7;
                  }
               }
            }

            if (var5 < 0) {
               return var6 ? var0.subSequence(var3 - 1, var4 + 2) : var0.subSequence(var3, var4 + 1);
            }
         }

         StringBuilder var11 = new StringBuilder(var4 - var3 + 1 + 7);
         var11.append('"').append(var0, var3, var5);

         for(int var10 = var5; var10 <= var4; ++var10) {
            char var9 = var0.charAt(var10);
            if (isDoubleQuote(var9)) {
               var11.append('"');
               if (var10 < var4 && isDoubleQuote(var0.charAt(var10 + 1))) {
                  ++var10;
               }
            }

            var11.append(var9);
         }

         return var11.append('"');
      }
   }

   public static CharSequence unescapeCsv(CharSequence var0) {
      int var1 = ((CharSequence)ObjectUtil.checkNotNull(var0, "value")).length();
      if (var1 == 0) {
         return var0;
      } else {
         int var2 = var1 - 1;
         boolean var3 = isDoubleQuote(var0.charAt(0)) && isDoubleQuote(var0.charAt(var2)) && var1 != 1;
         if (!var3) {
            validateCsvFormat(var0);
            return var0;
         } else {
            StringBuilder var4 = InternalThreadLocalMap.get().stringBuilder();

            for(int var5 = 1; var5 < var2; ++var5) {
               char var6 = var0.charAt(var5);
               if (var6 == '"') {
                  if (!isDoubleQuote(var0.charAt(var5 + 1)) || var5 + 1 == var2) {
                     throw newInvalidEscapedCsvFieldException(var0, var5);
                  }

                  ++var5;
               }

               var4.append(var6);
            }

            return var4.toString();
         }
      }
   }

   public static List<CharSequence> unescapeCsvFields(CharSequence var0) {
      ArrayList var1 = new ArrayList(2);
      StringBuilder var2 = InternalThreadLocalMap.get().stringBuilder();
      boolean var3 = false;
      int var4 = var0.length() - 1;

      for(int var5 = 0; var5 <= var4; ++var5) {
         char var6 = var0.charAt(var5);
         if (var3) {
            switch(var6) {
            case '"':
               if (var5 == var4) {
                  var1.add(var2.toString());
                  return var1;
               }

               ++var5;
               char var7 = var0.charAt(var5);
               if (var7 == '"') {
                  var2.append('"');
               } else {
                  if (var7 != ',') {
                     throw newInvalidEscapedCsvFieldException(var0, var5 - 1);
                  }

                  var3 = false;
                  var1.add(var2.toString());
                  var2.setLength(0);
               }
               break;
            default:
               var2.append(var6);
            }
         } else {
            switch(var6) {
            case '\n':
            case '\r':
               throw newInvalidEscapedCsvFieldException(var0, var5);
            case '"':
               if (var2.length() != 0) {
                  throw newInvalidEscapedCsvFieldException(var0, var5);
               }

               var3 = true;
               break;
            case ',':
               var1.add(var2.toString());
               var2.setLength(0);
               break;
            default:
               var2.append(var6);
            }
         }
      }

      if (var3) {
         throw newInvalidEscapedCsvFieldException(var0, var4);
      } else {
         var1.add(var2.toString());
         return var1;
      }
   }

   private static void validateCsvFormat(CharSequence var0) {
      int var1 = var0.length();
      int var2 = 0;

      while(var2 < var1) {
         switch(var0.charAt(var2)) {
         case '\n':
         case '\r':
         case '"':
         case ',':
            throw newInvalidEscapedCsvFieldException(var0, var2);
         default:
            ++var2;
         }
      }

   }

   private static IllegalArgumentException newInvalidEscapedCsvFieldException(CharSequence var0, int var1) {
      return new IllegalArgumentException("invalid escaped CSV field: " + var0 + " index: " + var1);
   }

   public static int length(String var0) {
      return var0 == null ? 0 : var0.length();
   }

   public static boolean isNullOrEmpty(String var0) {
      return var0 == null || var0.isEmpty();
   }

   public static int indexOfNonWhiteSpace(CharSequence var0, int var1) {
      while(var1 < var0.length()) {
         if (!Character.isWhitespace(var0.charAt(var1))) {
            return var1;
         }

         ++var1;
      }

      return -1;
   }

   public static boolean isSurrogate(char var0) {
      return var0 >= '\ud800' && var0 <= '\udfff';
   }

   private static boolean isDoubleQuote(char var0) {
      return var0 == '"';
   }

   public static boolean endsWith(CharSequence var0, char var1) {
      int var2 = var0.length();
      return var2 > 0 && var0.charAt(var2 - 1) == var1;
   }

   public static CharSequence trimOws(CharSequence var0) {
      int var1 = var0.length();
      if (var1 == 0) {
         return var0;
      } else {
         int var2 = indexOfFirstNonOwsChar(var0, var1);
         int var3 = indexOfLastNonOwsChar(var0, var2, var1);
         return var2 == 0 && var3 == var1 - 1 ? var0 : var0.subSequence(var2, var3 + 1);
      }
   }

   private static int indexOfFirstNonOwsChar(CharSequence var0, int var1) {
      int var2;
      for(var2 = 0; var2 < var1 && isOws(var0.charAt(var2)); ++var2) {
      }

      return var2;
   }

   private static int indexOfLastNonOwsChar(CharSequence var0, int var1, int var2) {
      int var3;
      for(var3 = var2 - 1; var3 > var1 && isOws(var0.charAt(var3)); --var3) {
      }

      return var3;
   }

   private static boolean isOws(char var0) {
      return var0 == ' ' || var0 == '\t';
   }

   static {
      for(int var0 = 0; var0 < BYTE2HEX_PAD.length; ++var0) {
         String var1 = Integer.toHexString(var0);
         BYTE2HEX_PAD[var0] = var0 > 15 ? var1 : '0' + var1;
         BYTE2HEX_NOPAD[var0] = var1;
      }

   }
}
