package io.netty.handler.codec.socksx.v5;

import io.netty.handler.codec.DecoderResult;
import io.netty.util.NetUtil;
import io.netty.util.internal.StringUtil;
import java.net.IDN;

public final class DefaultSocks5CommandResponse extends AbstractSocks5Message implements Socks5CommandResponse {
   private final Socks5CommandStatus status;
   private final Socks5AddressType bndAddrType;
   private final String bndAddr;
   private final int bndPort;

   public DefaultSocks5CommandResponse(Socks5CommandStatus var1, Socks5AddressType var2) {
      this(var1, var2, (String)null, 0);
   }

   public DefaultSocks5CommandResponse(Socks5CommandStatus var1, Socks5AddressType var2, String var3, int var4) {
      super();
      if (var1 == null) {
         throw new NullPointerException("status");
      } else if (var2 == null) {
         throw new NullPointerException("bndAddrType");
      } else {
         if (var3 != null) {
            if (var2 == Socks5AddressType.IPv4) {
               if (!NetUtil.isValidIpV4Address(var3)) {
                  throw new IllegalArgumentException("bndAddr: " + var3 + " (expected: a valid IPv4 address)");
               }
            } else if (var2 == Socks5AddressType.DOMAIN) {
               var3 = IDN.toASCII(var3);
               if (var3.length() > 255) {
                  throw new IllegalArgumentException("bndAddr: " + var3 + " (expected: less than 256 chars)");
               }
            } else if (var2 == Socks5AddressType.IPv6 && !NetUtil.isValidIpV6Address(var3)) {
               throw new IllegalArgumentException("bndAddr: " + var3 + " (expected: a valid IPv6 address)");
            }
         }

         if (var4 >= 0 && var4 <= 65535) {
            this.status = var1;
            this.bndAddrType = var2;
            this.bndAddr = var3;
            this.bndPort = var4;
         } else {
            throw new IllegalArgumentException("bndPort: " + var4 + " (expected: 0~65535)");
         }
      }
   }

   public Socks5CommandStatus status() {
      return this.status;
   }

   public Socks5AddressType bndAddrType() {
      return this.bndAddrType;
   }

   public String bndAddr() {
      return this.bndAddr;
   }

   public int bndPort() {
      return this.bndPort;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(128);
      var1.append(StringUtil.simpleClassName((Object)this));
      DecoderResult var2 = this.decoderResult();
      if (!var2.isSuccess()) {
         var1.append("(decoderResult: ");
         var1.append(var2);
         var1.append(", status: ");
      } else {
         var1.append("(status: ");
      }

      var1.append(this.status());
      var1.append(", bndAddrType: ");
      var1.append(this.bndAddrType());
      var1.append(", bndAddr: ");
      var1.append(this.bndAddr());
      var1.append(", bndPort: ");
      var1.append(this.bndPort());
      var1.append(')');
      return var1.toString();
   }
}
