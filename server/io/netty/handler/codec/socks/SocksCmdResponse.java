package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import java.net.IDN;

public final class SocksCmdResponse extends SocksResponse {
   private final SocksCmdStatus cmdStatus;
   private final SocksAddressType addressType;
   private final String host;
   private final int port;
   private static final byte[] DOMAIN_ZEROED = new byte[]{0};
   private static final byte[] IPv4_HOSTNAME_ZEROED = new byte[]{0, 0, 0, 0};
   private static final byte[] IPv6_HOSTNAME_ZEROED = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

   public SocksCmdResponse(SocksCmdStatus var1, SocksAddressType var2) {
      this(var1, var2, (String)null, 0);
   }

   public SocksCmdResponse(SocksCmdStatus var1, SocksAddressType var2, String var3, int var4) {
      super(SocksResponseType.CMD);
      if (var1 == null) {
         throw new NullPointerException("cmdStatus");
      } else if (var2 == null) {
         throw new NullPointerException("addressType");
      } else {
         if (var3 != null) {
            switch(var2) {
            case IPv4:
               if (!NetUtil.isValidIpV4Address(var3)) {
                  throw new IllegalArgumentException(var3 + " is not a valid IPv4 address");
               }
               break;
            case DOMAIN:
               String var5 = IDN.toASCII(var3);
               if (var5.length() > 255) {
                  throw new IllegalArgumentException(var3 + " IDN: " + var5 + " exceeds 255 char limit");
               }

               var3 = var5;
               break;
            case IPv6:
               if (!NetUtil.isValidIpV6Address(var3)) {
                  throw new IllegalArgumentException(var3 + " is not a valid IPv6 address");
               }
            case UNKNOWN:
            }
         }

         if (var4 >= 0 && var4 <= 65535) {
            this.cmdStatus = var1;
            this.addressType = var2;
            this.host = var3;
            this.port = var4;
         } else {
            throw new IllegalArgumentException(var4 + " is not in bounds 0 <= x <= 65535");
         }
      }
   }

   public SocksCmdStatus cmdStatus() {
      return this.cmdStatus;
   }

   public SocksAddressType addressType() {
      return this.addressType;
   }

   public String host() {
      return this.host != null && this.addressType == SocksAddressType.DOMAIN ? IDN.toUnicode(this.host) : this.host;
   }

   public int port() {
      return this.port;
   }

   public void encodeAsByteBuf(ByteBuf var1) {
      var1.writeByte(this.protocolVersion().byteValue());
      var1.writeByte(this.cmdStatus.byteValue());
      var1.writeByte(0);
      var1.writeByte(this.addressType.byteValue());
      byte[] var2;
      switch(this.addressType) {
      case IPv4:
         var2 = this.host == null ? IPv4_HOSTNAME_ZEROED : NetUtil.createByteArrayFromIpAddressString(this.host);
         var1.writeBytes(var2);
         var1.writeShort(this.port);
         break;
      case DOMAIN:
         if (this.host != null) {
            var1.writeByte(this.host.length());
            var1.writeCharSequence(this.host, CharsetUtil.US_ASCII);
         } else {
            var1.writeByte(DOMAIN_ZEROED.length);
            var1.writeBytes(DOMAIN_ZEROED);
         }

         var1.writeShort(this.port);
         break;
      case IPv6:
         var2 = this.host == null ? IPv6_HOSTNAME_ZEROED : NetUtil.createByteArrayFromIpAddressString(this.host);
         var1.writeBytes(var2);
         var1.writeShort(this.port);
      }

   }
}
