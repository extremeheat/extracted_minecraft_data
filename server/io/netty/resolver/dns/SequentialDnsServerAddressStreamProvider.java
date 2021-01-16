package io.netty.resolver.dns;

import java.net.InetSocketAddress;

public final class SequentialDnsServerAddressStreamProvider extends UniSequentialDnsServerAddressStreamProvider {
   public SequentialDnsServerAddressStreamProvider(InetSocketAddress... var1) {
      super(DnsServerAddresses.sequential(var1));
   }

   public SequentialDnsServerAddressStreamProvider(Iterable<? extends InetSocketAddress> var1) {
      super(DnsServerAddresses.sequential(var1));
   }
}
