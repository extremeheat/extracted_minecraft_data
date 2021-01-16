package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsRecord;
import java.net.InetAddress;
import java.util.List;

public interface DnsCache {
   void clear();

   boolean clear(String var1);

   List<? extends DnsCacheEntry> get(String var1, DnsRecord[] var2);

   DnsCacheEntry cache(String var1, DnsRecord[] var2, InetAddress var3, long var4, EventLoop var6);

   DnsCacheEntry cache(String var1, DnsRecord[] var2, Throwable var3, EventLoop var4);
}
