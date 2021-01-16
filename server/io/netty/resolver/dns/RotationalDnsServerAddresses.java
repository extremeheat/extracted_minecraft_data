package io.netty.resolver.dns;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

final class RotationalDnsServerAddresses extends DefaultDnsServerAddresses {
   private static final AtomicIntegerFieldUpdater<RotationalDnsServerAddresses> startIdxUpdater = AtomicIntegerFieldUpdater.newUpdater(RotationalDnsServerAddresses.class, "startIdx");
   private volatile int startIdx;

   RotationalDnsServerAddresses(InetSocketAddress[] var1) {
      super("rotational", var1);
   }

   public DnsServerAddressStream stream() {
      int var1;
      int var2;
      do {
         var1 = this.startIdx;
         var2 = var1 + 1;
         if (var2 >= this.addresses.length) {
            var2 = 0;
         }
      } while(!startIdxUpdater.compareAndSet(this, var1, var2));

      return new SequentialDnsServerAddressStream(this.addresses, var1);
   }
}
