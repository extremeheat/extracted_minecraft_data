package io.netty.resolver.dns;

import java.net.InetSocketAddress;

final class SingletonDnsServerAddresses extends DnsServerAddresses {
   private final InetSocketAddress address;
   private final DnsServerAddressStream stream = new DnsServerAddressStream() {
      public InetSocketAddress next() {
         return SingletonDnsServerAddresses.this.address;
      }

      public int size() {
         return 1;
      }

      public DnsServerAddressStream duplicate() {
         return this;
      }

      public String toString() {
         return SingletonDnsServerAddresses.this.toString();
      }
   };

   SingletonDnsServerAddresses(InetSocketAddress var1) {
      super();
      this.address = var1;
   }

   public DnsServerAddressStream stream() {
      return this.stream;
   }

   public String toString() {
      return "singleton(" + this.address + ")";
   }
}
