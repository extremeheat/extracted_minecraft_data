package io.netty.resolver.dns;

import java.net.InetSocketAddress;

abstract class DefaultDnsServerAddresses extends DnsServerAddresses {
   protected final InetSocketAddress[] addresses;
   private final String strVal;

   DefaultDnsServerAddresses(String var1, InetSocketAddress[] var2) {
      super();
      this.addresses = var2;
      StringBuilder var3 = new StringBuilder(var1.length() + 2 + var2.length * 16);
      var3.append(var1).append('(');
      InetSocketAddress[] var4 = var2;
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         InetSocketAddress var7 = var4[var6];
         var3.append(var7).append(", ");
      }

      var3.setLength(var3.length() - 2);
      var3.append(')');
      this.strVal = var3.toString();
   }

   public String toString() {
      return this.strVal;
   }
}
