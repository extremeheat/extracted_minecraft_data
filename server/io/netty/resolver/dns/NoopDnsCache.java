package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsRecord;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;

public final class NoopDnsCache implements DnsCache {
   public static final NoopDnsCache INSTANCE = new NoopDnsCache();

   private NoopDnsCache() {
      super();
   }

   public void clear() {
   }

   public boolean clear(String var1) {
      return false;
   }

   public List<? extends DnsCacheEntry> get(String var1, DnsRecord[] var2) {
      return Collections.emptyList();
   }

   public DnsCacheEntry cache(String var1, DnsRecord[] var2, InetAddress var3, long var4, EventLoop var6) {
      return new NoopDnsCache.NoopDnsCacheEntry(var3);
   }

   public DnsCacheEntry cache(String var1, DnsRecord[] var2, Throwable var3, EventLoop var4) {
      return null;
   }

   public String toString() {
      return NoopDnsCache.class.getSimpleName();
   }

   private static final class NoopDnsCacheEntry implements DnsCacheEntry {
      private final InetAddress address;

      NoopDnsCacheEntry(InetAddress var1) {
         super();
         this.address = var1;
      }

      public InetAddress address() {
         return this.address;
      }

      public Throwable cause() {
         return null;
      }

      public String toString() {
         return this.address.toString();
      }
   }
}
