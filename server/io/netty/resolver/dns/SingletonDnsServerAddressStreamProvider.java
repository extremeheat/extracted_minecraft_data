package io.netty.resolver.dns;

import java.net.InetSocketAddress;

public final class SingletonDnsServerAddressStreamProvider extends UniSequentialDnsServerAddressStreamProvider {
   public SingletonDnsServerAddressStreamProvider(InetSocketAddress var1) {
      super(DnsServerAddresses.singleton(var1));
   }
}
