package io.netty.handler.codec.socksx.v5;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;

public interface Socks5AddressDecoder {
   Socks5AddressDecoder DEFAULT = new Socks5AddressDecoder() {
      private static final int IPv6_LEN = 16;

      public String decodeAddress(Socks5AddressType var1, ByteBuf var2) throws Exception {
         if (var1 == Socks5AddressType.IPv4) {
            return NetUtil.intToIpAddress(var2.readInt());
         } else if (var1 == Socks5AddressType.DOMAIN) {
            short var6 = var2.readUnsignedByte();
            String var4 = var2.toString(var2.readerIndex(), var6, CharsetUtil.US_ASCII);
            var2.skipBytes(var6);
            return var4;
         } else if (var1 == Socks5AddressType.IPv6) {
            if (var2.hasArray()) {
               int var5 = var2.readerIndex();
               var2.readerIndex(var5 + 16);
               return NetUtil.bytesToIpAddress(var2.array(), var2.arrayOffset() + var5, 16);
            } else {
               byte[] var3 = new byte[16];
               var2.readBytes(var3);
               return NetUtil.bytesToIpAddress(var3);
            }
         } else {
            throw new DecoderException("unsupported address type: " + (var1.byteValue() & 255));
         }
      }
   };

   String decodeAddress(Socks5AddressType var1, ByteBuf var2) throws Exception;
}
