package io.netty.util.internal;

public final class ConstantTimeUtils {
   private ConstantTimeUtils() {
      super();
   }

   public static int equalsConstantTime(int var0, int var1) {
      int var2 = ~(var0 ^ var1);
      var2 &= var2 >> 16;
      var2 &= var2 >> 8;
      var2 &= var2 >> 4;
      var2 &= var2 >> 2;
      var2 &= var2 >> 1;
      return var2 & 1;
   }

   public static int equalsConstantTime(long var0, long var2) {
      long var4 = ~(var0 ^ var2);
      var4 &= var4 >> 32;
      var4 &= var4 >> 16;
      var4 &= var4 >> 8;
      var4 &= var4 >> 4;
      var4 &= var4 >> 2;
      var4 &= var4 >> 1;
      return (int)(var4 & 1L);
   }

   public static int equalsConstantTime(byte[] var0, int var1, byte[] var2, int var3, int var4) {
      int var5 = 0;

      for(int var6 = var1 + var4; var1 < var6; ++var3) {
         var5 |= var0[var1] ^ var2[var3];
         ++var1;
      }

      return equalsConstantTime(var5, 0);
   }

   public static int equalsConstantTime(CharSequence var0, CharSequence var1) {
      if (var0.length() != var1.length()) {
         return 0;
      } else {
         int var2 = 0;

         for(int var3 = 0; var3 < var0.length(); ++var3) {
            var2 |= var0.charAt(var3) ^ var1.charAt(var3);
         }

         return equalsConstantTime(var2, 0);
      }
   }
}
