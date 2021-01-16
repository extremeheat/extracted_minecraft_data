package io.netty.handler.codec.redis;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.PlatformDependent;

final class RedisCodecUtil {
   private RedisCodecUtil() {
      super();
   }

   static byte[] longToAsciiBytes(long var0) {
      return Long.toString(var0).getBytes(CharsetUtil.US_ASCII);
   }

   static short makeShort(char var0, char var1) {
      return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? (short)(var1 << 8 | var0) : (short)(var0 << 8 | var1);
   }

   static byte[] shortToBytes(short var0) {
      byte[] var1 = new byte[2];
      if (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER) {
         var1[1] = (byte)(var0 >> 8 & 255);
         var1[0] = (byte)(var0 & 255);
      } else {
         var1[0] = (byte)(var0 >> 8 & 255);
         var1[1] = (byte)(var0 & 255);
      }

      return var1;
   }
}
