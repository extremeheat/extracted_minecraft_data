package io.netty.handler.codec.socksx.v5;

import io.netty.handler.codec.DecoderResult;
import io.netty.util.NetUtil;
import io.netty.util.internal.StringUtil;
import java.net.IDN;

public final class DefaultSocks5CommandRequest extends AbstractSocks5Message implements Socks5CommandRequest {
   private final Socks5CommandType type;
   private final Socks5AddressType dstAddrType;
   private final String dstAddr;
   private final int dstPort;

   public DefaultSocks5CommandRequest(Socks5CommandType var1, Socks5AddressType var2, String var3, int var4) {
      super();
      if (var1 == null) {
         throw new NullPointerException("type");
      } else if (var2 == null) {
         throw new NullPointerException("dstAddrType");
      } else if (var3 == null) {
         throw new NullPointerException("dstAddr");
      } else {
         if (var2 == Socks5AddressType.IPv4) {
            if (!NetUtil.isValidIpV4Address(var3)) {
               throw new IllegalArgumentException("dstAddr: " + var3 + " (expected: a valid IPv4 address)");
            }
         } else if (var2 == Socks5AddressType.DOMAIN) {
            var3 = IDN.toASCII(var3);
            if (var3.length() > 255) {
               throw new IllegalArgumentException("dstAddr: " + var3 + " (expected: less than 256 chars)");
            }
         } else if (var2 == Socks5AddressType.IPv6 && !NetUtil.isValidIpV6Address(var3)) {
            throw new IllegalArgumentException("dstAddr: " + var3 + " (expected: a valid IPv6 address");
         }

         if (var4 >= 0 && var4 <= 65535) {
            this.type = var1;
            this.dstAddrType = var2;
            this.dstAddr = var3;
            this.dstPort = var4;
         } else {
            throw new IllegalArgumentException("dstPort: " + var4 + " (expected: 0~65535)");
         }
      }
   }

   public Socks5CommandType type() {
      return this.type;
   }

   public Socks5AddressType dstAddrType() {
      return this.dstAddrType;
   }

   public String dstAddr() {
      return this.dstAddr;
   }

   public int dstPort() {
      return this.dstPort;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(128);
      var1.append(StringUtil.simpleClassName((Object)this));
      DecoderResult var2 = this.decoderResult();
      if (!var2.isSuccess()) {
         var1.append("(decoderResult: ");
         var1.append(var2);
         var1.append(", type: ");
      } else {
         var1.append("(type: ");
      }

      var1.append(this.type());
      var1.append(", dstAddrType: ");
      var1.append(this.dstAddrType());
      var1.append(", dstAddr: ");
      var1.append(this.dstAddr());
      var1.append(", dstPort: ");
      var1.append(this.dstPort());
      var1.append(')');
      return var1.toString();
   }
}
