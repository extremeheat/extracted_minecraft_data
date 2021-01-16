package io.netty.resolver.dns;

import io.netty.util.internal.PlatformDependent;
import java.net.InetSocketAddress;
import java.util.Random;

final class ShuffledDnsServerAddressStream implements DnsServerAddressStream {
   private final InetSocketAddress[] addresses;
   private int i;

   ShuffledDnsServerAddressStream(InetSocketAddress[] var1) {
      super();
      this.addresses = var1;
      this.shuffle();
   }

   private ShuffledDnsServerAddressStream(InetSocketAddress[] var1, int var2) {
      super();
      this.addresses = var1;
      this.i = var2;
   }

   private void shuffle() {
      InetSocketAddress[] var1 = this.addresses;
      Random var2 = PlatformDependent.threadLocalRandom();

      for(int var3 = var1.length - 1; var3 >= 0; --var3) {
         InetSocketAddress var4 = var1[var3];
         int var5 = var2.nextInt(var3 + 1);
         var1[var3] = var1[var5];
         var1[var5] = var4;
      }

   }

   public InetSocketAddress next() {
      int var1 = this.i;
      InetSocketAddress var2 = this.addresses[var1];
      ++var1;
      if (var1 < this.addresses.length) {
         this.i = var1;
      } else {
         this.i = 0;
         this.shuffle();
      }

      return var2;
   }

   public int size() {
      return this.addresses.length;
   }

   public ShuffledDnsServerAddressStream duplicate() {
      return new ShuffledDnsServerAddressStream(this.addresses, this.i);
   }

   public String toString() {
      return SequentialDnsServerAddressStream.toString("shuffled", this.i, this.addresses);
   }
}
