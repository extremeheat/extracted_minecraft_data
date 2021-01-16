package org.apache.logging.log4j.core.util;

public final class JsonUtils {
   private static final char[] HC = "0123456789ABCDEF".toCharArray();
   private static final int[] ESC_CODES;
   private static final ThreadLocal<char[]> _qbufLocal;

   public JsonUtils() {
      super();
   }

   private static char[] getQBuf() {
      char[] var0 = (char[])_qbufLocal.get();
      if (var0 == null) {
         var0 = new char[]{'\\', '\u0000', '0', '0', '\u0000', '\u0000'};
         _qbufLocal.set(var0);
      }

      return var0;
   }

   public static void quoteAsString(CharSequence var0, StringBuilder var1) {
      char[] var2 = getQBuf();
      int var3 = ESC_CODES.length;
      int var4 = 0;
      int var5 = var0.length();

      while(var4 < var5) {
         while(true) {
            char var6 = var0.charAt(var4);
            if (var6 < var3 && ESC_CODES[var6] != 0) {
               var6 = var0.charAt(var4++);
               int var7 = ESC_CODES[var6];
               int var8 = var7 < 0 ? _appendNumeric(var6, var2) : _appendNamed(var7, var2);
               var1.append(var2, 0, var8);
            } else {
               var1.append(var6);
               ++var4;
               if (var4 >= var5) {
                  return;
               }
            }
         }
      }

   }

   private static int _appendNumeric(int var0, char[] var1) {
      var1[1] = 'u';
      var1[4] = HC[var0 >> 4];
      var1[5] = HC[var0 & 15];
      return 6;
   }

   private static int _appendNamed(int var0, char[] var1) {
      var1[1] = (char)var0;
      return 2;
   }

   static {
      int[] var0 = new int[128];

      for(int var1 = 0; var1 < 32; ++var1) {
         var0[var1] = -1;
      }

      var0[34] = 34;
      var0[92] = 92;
      var0[8] = 98;
      var0[9] = 116;
      var0[12] = 102;
      var0[10] = 110;
      var0[13] = 114;
      ESC_CODES = var0;
      _qbufLocal = new ThreadLocal();
   }
}
