package io.netty.handler.codec.socksx.v5;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EncoderException;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;

public interface Socks5AddressEncoder {
   Socks5AddressEncoder DEFAULT = new Socks5AddressEncoder() {
      public void encodeAddress(Socks5AddressType var1, String var2, ByteBuf var3) throws Exception {
         byte var4 = var1.byteValue();
         if (var4 == Socks5AddressType.IPv4.byteValue()) {
            if (var2 != null) {
               var3.writeBytes(NetUtil.createByteArrayFromIpAddressString(var2));
            } else {
               var3.writeInt(0);
            }
         } else if (var4 == Socks5AddressType.DOMAIN.byteValue()) {
            if (var2 != null) {
               var3.writeByte(var2.length());
               var3.writeCharSequence(var2, CharsetUtil.US_ASCII);
            } else {
               var3.writeByte(1);
               var3.writeByte(0);
            }
         } else {
            if (var4 != Socks5AddressType.IPv6.byteValue()) {
               throw new EncoderException("unsupported addrType: " + (var1.byteValue() & 255));
            }

            if (var2 != null) {
               var3.writeBytes(NetUtil.createByteArrayFromIpAddressString(var2));
            } else {
               var3.writeLong(0L);
               var3.writeLong(0L);
            }
         }

      }
   };

   void encodeAddress(Socks5AddressType var1, String var2, ByteBuf var3) throws Exception;
}
