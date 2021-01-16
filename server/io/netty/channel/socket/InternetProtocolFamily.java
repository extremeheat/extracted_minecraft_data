package io.netty.channel.socket;

import io.netty.util.NetUtil;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

public enum InternetProtocolFamily {
   IPv4(Inet4Address.class, 1, NetUtil.LOCALHOST4),
   IPv6(Inet6Address.class, 2, NetUtil.LOCALHOST6);

   private final Class<? extends InetAddress> addressType;
   private final int addressNumber;
   private final InetAddress localHost;

   private InternetProtocolFamily(Class<? extends InetAddress> var3, int var4, InetAddress var5) {
      this.addressType = var3;
      this.addressNumber = var4;
      this.localHost = var5;
   }

   public Class<? extends InetAddress> addressType() {
      return this.addressType;
   }

   public int addressNumber() {
      return this.addressNumber;
   }

   public InetAddress localhost() {
      return this.localHost;
   }

   public static InternetProtocolFamily of(InetAddress var0) {
      if (var0 instanceof Inet4Address) {
         return IPv4;
      } else if (var0 instanceof Inet6Address) {
         return IPv6;
      } else {
         throw new IllegalArgumentException("address " + var0 + " not supported");
      }
   }
}
