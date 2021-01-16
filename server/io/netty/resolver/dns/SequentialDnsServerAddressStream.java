package io.netty.resolver.dns;

import java.net.InetSocketAddress;

final class SequentialDnsServerAddressStream implements DnsServerAddressStream {
   private final InetSocketAddress[] addresses;
   private int i;

   SequentialDnsServerAddressStream(InetSocketAddress[] var1, int var2) {
      super();
      this.addresses = var1;
      this.i = var2;
   }

   public InetSocketAddress next() {
      int var1 = this.i;
      InetSocketAddress var2 = this.addresses[var1];
      ++var1;
      if (var1 < this.addresses.length) {
         this.i = var1;
      } else {
         this.i = 0;
      }

      return var2;
   }

   public int size() {
      return this.addresses.length;
   }

   public SequentialDnsServerAddressStream duplicate() {
      return new SequentialDnsServerAddressStream(this.addresses, this.i);
   }

   public String toString() {
      return toString("sequential", this.i, this.addresses);
   }

   static String toString(String var0, int var1, InetSocketAddress[] var2) {
      StringBuilder var3 = new StringBuilder(var0.length() + 2 + var2.length * 16);
      var3.append(var0).append("(index: ").append(var1);
      var3.append(", addrs: (");
      InetSocketAddress[] var4 = var2;
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         InetSocketAddress var7 = var4[var6];
         var3.append(var7).append(", ");
      }

      var3.setLength(var3.length() - 2);
      var3.append("))");
      return var3.toString();
   }
}
