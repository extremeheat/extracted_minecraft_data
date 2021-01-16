package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.util.ReferenceCountUtil;
import java.net.UnknownHostException;
import java.util.List;

final class DnsRecordResolveContext extends DnsResolveContext<DnsRecord> {
   DnsRecordResolveContext(DnsNameResolver var1, DnsQuestion var2, DnsRecord[] var3, DnsServerAddressStream var4) {
      this(var1, var2.name(), var2.dnsClass(), new DnsRecordType[]{var2.type()}, var3, var4);
   }

   private DnsRecordResolveContext(DnsNameResolver var1, String var2, int var3, DnsRecordType[] var4, DnsRecord[] var5, DnsServerAddressStream var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   DnsResolveContext<DnsRecord> newResolverContext(DnsNameResolver var1, String var2, int var3, DnsRecordType[] var4, DnsRecord[] var5, DnsServerAddressStream var6) {
      return new DnsRecordResolveContext(var1, var2, var3, var4, var5, var6);
   }

   DnsRecord convertRecord(DnsRecord var1, String var2, DnsRecord[] var3, EventLoop var4) {
      return (DnsRecord)ReferenceCountUtil.retain(var1);
   }

   List<DnsRecord> filterResults(List<DnsRecord> var1) {
      return var1;
   }

   void cache(String var1, DnsRecord[] var2, DnsRecord var3, DnsRecord var4) {
   }

   void cache(String var1, DnsRecord[] var2, UnknownHostException var3) {
   }
}
