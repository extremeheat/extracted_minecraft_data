package io.netty.buffer;

final class HeapByteBufUtil {
   static byte getByte(byte[] var0, int var1) {
      return var0[var1];
   }

   static short getShort(byte[] var0, int var1) {
      return (short)(var0[var1] << 8 | var0[var1 + 1] & 255);
   }

   static short getShortLE(byte[] var0, int var1) {
      return (short)(var0[var1] & 255 | var0[var1 + 1] << 8);
   }

   static int getUnsignedMedium(byte[] var0, int var1) {
      return (var0[var1] & 255) << 16 | (var0[var1 + 1] & 255) << 8 | var0[var1 + 2] & 255;
   }

   static int getUnsignedMediumLE(byte[] var0, int var1) {
      return var0[var1] & 255 | (var0[var1 + 1] & 255) << 8 | (var0[var1 + 2] & 255) << 16;
   }

   static int getInt(byte[] var0, int var1) {
      return (var0[var1] & 255) << 24 | (var0[var1 + 1] & 255) << 16 | (var0[var1 + 2] & 255) << 8 | var0[var1 + 3] & 255;
   }

   static int getIntLE(byte[] var0, int var1) {
      return var0[var1] & 255 | (var0[var1 + 1] & 255) << 8 | (var0[var1 + 2] & 255) << 16 | (var0[var1 + 3] & 255) << 24;
   }

   static long getLong(byte[] var0, int var1) {
      return ((long)var0[var1] & 255L) << 56 | ((long)var0[var1 + 1] & 255L) << 48 | ((long)var0[var1 + 2] & 255L) << 40 | ((long)var0[var1 + 3] & 255L) << 32 | ((long)var0[var1 + 4] & 255L) << 24 | ((long)var0[var1 + 5] & 255L) << 16 | ((long)var0[var1 + 6] & 255L) << 8 | (long)var0[var1 + 7] & 255L;
   }

   static long getLongLE(byte[] var0, int var1) {
      return (long)var0[var1] & 255L | ((long)var0[var1 + 1] & 255L) << 8 | ((long)var0[var1 + 2] & 255L) << 16 | ((long)var0[var1 + 3] & 255L) << 24 | ((long)var0[var1 + 4] & 255L) << 32 | ((long)var0[var1 + 5] & 255L) << 40 | ((long)var0[var1 + 6] & 255L) << 48 | ((long)var0[var1 + 7] & 255L) << 56;
   }

   static void setByte(byte[] var0, int var1, int var2) {
      var0[var1] = (byte)var2;
   }

   static void setShort(byte[] var0, int var1, int var2) {
      var0[var1] = (byte)(var2 >>> 8);
      var0[var1 + 1] = (byte)var2;
   }

   static void setShortLE(byte[] var0, int var1, int var2) {
      var0[var1] = (byte)var2;
      var0[var1 + 1] = (byte)(var2 >>> 8);
   }

   static void setMedium(byte[] var0, int var1, int var2) {
      var0[var1] = (byte)(var2 >>> 16);
      var0[var1 + 1] = (byte)(var2 >>> 8);
      var0[var1 + 2] = (byte)var2;
   }

   static void setMediumLE(byte[] var0, int var1, int var2) {
      var0[var1] = (byte)var2;
      var0[var1 + 1] = (byte)(var2 >>> 8);
      var0[var1 + 2] = (byte)(var2 >>> 16);
   }

   static void setInt(byte[] var0, int var1, int var2) {
      var0[var1] = (byte)(var2 >>> 24);
      var0[var1 + 1] = (byte)(var2 >>> 16);
      var0[var1 + 2] = (byte)(var2 >>> 8);
      var0[var1 + 3] = (byte)var2;
   }

   static void setIntLE(byte[] var0, int var1, int var2) {
      var0[var1] = (byte)var2;
      var0[var1 + 1] = (byte)(var2 >>> 8);
      var0[var1 + 2] = (byte)(var2 >>> 16);
      var0[var1 + 3] = (byte)(var2 >>> 24);
   }

   static void setLong(byte[] var0, int var1, long var2) {
      var0[var1] = (byte)((int)(var2 >>> 56));
      var0[var1 + 1] = (byte)((int)(var2 >>> 48));
      var0[var1 + 2] = (byte)((int)(var2 >>> 40));
      var0[var1 + 3] = (byte)((int)(var2 >>> 32));
      var0[var1 + 4] = (byte)((int)(var2 >>> 24));
      var0[var1 + 5] = (byte)((int)(var2 >>> 16));
      var0[var1 + 6] = (byte)((int)(var2 >>> 8));
      var0[var1 + 7] = (byte)((int)var2);
   }

   static void setLongLE(byte[] var0, int var1, long var2) {
      var0[var1] = (byte)((int)var2);
      var0[var1 + 1] = (byte)((int)(var2 >>> 8));
      var0[var1 + 2] = (byte)((int)(var2 >>> 16));
      var0[var1 + 3] = (byte)((int)(var2 >>> 24));
      var0[var1 + 4] = (byte)((int)(var2 >>> 32));
      var0[var1 + 5] = (byte)((int)(var2 >>> 40));
      var0[var1 + 6] = (byte)((int)(var2 >>> 48));
      var0[var1 + 7] = (byte)((int)(var2 >>> 56));
   }

   private HeapByteBufUtil() {
      super();
   }
}
