package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;

final class SocksCommonUtils {
   public static final SocksRequest UNKNOWN_SOCKS_REQUEST = new UnknownSocksRequest();
   public static final SocksResponse UNKNOWN_SOCKS_RESPONSE = new UnknownSocksResponse();
   private static final char ipv6hextetSeparator = ':';

   private SocksCommonUtils() {
      super();
   }

   public static String ipv6toStr(byte[] var0) {
      assert var0.length == 16;

      StringBuilder var1 = new StringBuilder(39);
      ipv6toStr(var1, var0, 0, 8);
      return var1.toString();
   }

   private static void ipv6toStr(StringBuilder var0, byte[] var1, int var2, int var3) {
      --var3;

      int var4;
      for(var4 = var2; var4 < var3; ++var4) {
         appendHextet(var0, var1, var4);
         var0.append(':');
      }

      appendHextet(var0, var1, var4);
   }

   private static void appendHextet(StringBuilder var0, byte[] var1, int var2) {
      StringUtil.toHexString(var0, var1, var2 << 1, 2);
   }

   static String readUsAscii(ByteBuf var0, int var1) {
      String var2 = var0.toString(var0.readerIndex(), var1, CharsetUtil.US_ASCII);
      var0.skipBytes(var1);
      return var2;
   }
}
