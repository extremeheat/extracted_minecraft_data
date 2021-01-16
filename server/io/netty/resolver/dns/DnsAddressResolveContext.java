package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

final class DnsAddressResolveContext extends DnsResolveContext<InetAddress> {
   private final DnsCache resolveCache;

   DnsAddressResolveContext(DnsNameResolver var1, String var2, DnsRecord[] var3, DnsServerAddressStream var4, DnsCache var5) {
      super(var1, var2, 1, var1.resolveRecordTypes(), var3, var4);
      this.resolveCache = var5;
   }

   DnsResolveContext<InetAddress> newResolverContext(DnsNameResolver var1, String var2, int var3, DnsRecordType[] var4, DnsRecord[] var5, DnsServerAddressStream var6) {
      return new DnsAddressResolveContext(var1, var2, var5, var6, this.resolveCache);
   }

   InetAddress convertRecord(DnsRecord var1, String var2, DnsRecord[] var3, EventLoop var4) {
      return DnsAddressDecoder.decodeAddress(var1, var2, this.parent.isDecodeIdn());
   }

   List<InetAddress> filterResults(List<InetAddress> var1) {
      Class var2 = this.parent.preferredAddressType().addressType();
      int var3 = var1.size();
      int var4 = 0;

      for(int var5 = 0; var5 < var3; ++var5) {
         InetAddress var6 = (InetAddress)var1.get(var5);
         if (var2.isInstance(var6)) {
            ++var4;
         }
      }

      if (var4 != var3 && var4 != 0) {
         ArrayList var8 = new ArrayList(var4);

         for(int var9 = 0; var9 < var3; ++var9) {
            InetAddress var7 = (InetAddress)var1.get(var9);
            if (var2.isInstance(var7)) {
               var8.add(var7);
            }
         }

         return var8;
      } else {
         return var1;
      }
   }

   void cache(String var1, DnsRecord[] var2, DnsRecord var3, InetAddress var4) {
      this.resolveCache.cache(var1, var2, var4, var3.timeToLive(), this.parent.ch.eventLoop());
   }

   void cache(String var1, DnsRecord[] var2, UnknownHostException var3) {
      this.resolveCache.cache(var1, var2, var3, this.parent.ch.eventLoop());
   }
}
