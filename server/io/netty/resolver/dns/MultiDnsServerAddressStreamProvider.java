package io.netty.resolver.dns;

import java.util.List;

public final class MultiDnsServerAddressStreamProvider implements DnsServerAddressStreamProvider {
   private final DnsServerAddressStreamProvider[] providers;

   public MultiDnsServerAddressStreamProvider(List<DnsServerAddressStreamProvider> var1) {
      super();
      this.providers = (DnsServerAddressStreamProvider[])var1.toArray(new DnsServerAddressStreamProvider[0]);
   }

   public MultiDnsServerAddressStreamProvider(DnsServerAddressStreamProvider... var1) {
      super();
      this.providers = (DnsServerAddressStreamProvider[])var1.clone();
   }

   public DnsServerAddressStream nameServerAddressStream(String var1) {
      DnsServerAddressStreamProvider[] var2 = this.providers;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         DnsServerAddressStreamProvider var5 = var2[var4];
         DnsServerAddressStream var6 = var5.nameServerAddressStream(var1);
         if (var6 != null) {
            return var6;
         }
      }

      return null;
   }
}
