package io.netty.resolver.dns;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.dns.DnsRawRecord;
import io.netty.handler.codec.dns.DnsRecord;
import java.net.IDN;
import java.net.InetAddress;
import java.net.UnknownHostException;

final class DnsAddressDecoder {
   private static final int INADDRSZ4 = 4;
   private static final int INADDRSZ6 = 16;

   static InetAddress decodeAddress(DnsRecord var0, String var1, boolean var2) {
      if (!(var0 instanceof DnsRawRecord)) {
         return null;
      } else {
         ByteBuf var3 = ((ByteBufHolder)var0).content();
         int var4 = var3.readableBytes();
         if (var4 != 4 && var4 != 16) {
            return null;
         } else {
            byte[] var5 = new byte[var4];
            var3.getBytes(var3.readerIndex(), var5);

            try {
               return InetAddress.getByAddress(var2 ? IDN.toUnicode(var1) : var1, var5);
            } catch (UnknownHostException var7) {
               throw new Error(var7);
            }
         }
      }
   }

   private DnsAddressDecoder() {
      super();
   }
}
