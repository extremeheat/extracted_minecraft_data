package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultDnsCache implements DnsCache {
   private final ConcurrentMap<String, DefaultDnsCache.Entries> resolveCache;
   private static final int MAX_SUPPORTED_TTL_SECS;
   private final int minTtl;
   private final int maxTtl;
   private final int negativeTtl;

   public DefaultDnsCache() {
      this(0, MAX_SUPPORTED_TTL_SECS, 0);
   }

   public DefaultDnsCache(int var1, int var2, int var3) {
      super();
      this.resolveCache = PlatformDependent.newConcurrentHashMap();
      this.minTtl = Math.min(MAX_SUPPORTED_TTL_SECS, ObjectUtil.checkPositiveOrZero(var1, "minTtl"));
      this.maxTtl = Math.min(MAX_SUPPORTED_TTL_SECS, ObjectUtil.checkPositiveOrZero(var2, "maxTtl"));
      if (var1 > var2) {
         throw new IllegalArgumentException("minTtl: " + var1 + ", maxTtl: " + var2 + " (expected: 0 <= minTtl <= maxTtl)");
      } else {
         this.negativeTtl = ObjectUtil.checkPositiveOrZero(var3, "negativeTtl");
      }
   }

   public int minTtl() {
      return this.minTtl;
   }

   public int maxTtl() {
      return this.maxTtl;
   }

   public int negativeTtl() {
      return this.negativeTtl;
   }

   public void clear() {
      label16:
      while(true) {
         if (!this.resolveCache.isEmpty()) {
            Iterator var1 = this.resolveCache.entrySet().iterator();

            while(true) {
               if (!var1.hasNext()) {
                  continue label16;
               }

               Entry var2 = (Entry)var1.next();
               var1.remove();
               ((DefaultDnsCache.Entries)var2.getValue()).clearAndCancel();
            }
         }

         return;
      }
   }

   public boolean clear(String var1) {
      ObjectUtil.checkNotNull(var1, "hostname");
      DefaultDnsCache.Entries var2 = (DefaultDnsCache.Entries)this.resolveCache.remove(var1);
      return var2 != null && var2.clearAndCancel();
   }

   private static boolean emptyAdditionals(DnsRecord[] var0) {
      return var0 == null || var0.length == 0;
   }

   public List<? extends DnsCacheEntry> get(String var1, DnsRecord[] var2) {
      ObjectUtil.checkNotNull(var1, "hostname");
      if (!emptyAdditionals(var2)) {
         return Collections.emptyList();
      } else {
         DefaultDnsCache.Entries var3 = (DefaultDnsCache.Entries)this.resolveCache.get(var1);
         return var3 == null ? null : (List)var3.get();
      }
   }

   public DnsCacheEntry cache(String var1, DnsRecord[] var2, InetAddress var3, long var4, EventLoop var6) {
      ObjectUtil.checkNotNull(var1, "hostname");
      ObjectUtil.checkNotNull(var3, "address");
      ObjectUtil.checkNotNull(var6, "loop");
      DefaultDnsCache.DefaultDnsCacheEntry var7 = new DefaultDnsCache.DefaultDnsCacheEntry(var1, var3);
      if (this.maxTtl != 0 && emptyAdditionals(var2)) {
         this.cache0(var7, Math.max(this.minTtl, Math.min(MAX_SUPPORTED_TTL_SECS, (int)Math.min((long)this.maxTtl, var4))), var6);
         return var7;
      } else {
         return var7;
      }
   }

   public DnsCacheEntry cache(String var1, DnsRecord[] var2, Throwable var3, EventLoop var4) {
      ObjectUtil.checkNotNull(var1, "hostname");
      ObjectUtil.checkNotNull(var3, "cause");
      ObjectUtil.checkNotNull(var4, "loop");
      DefaultDnsCache.DefaultDnsCacheEntry var5 = new DefaultDnsCache.DefaultDnsCacheEntry(var1, var3);
      if (this.negativeTtl != 0 && emptyAdditionals(var2)) {
         this.cache0(var5, Math.min(MAX_SUPPORTED_TTL_SECS, this.negativeTtl), var4);
         return var5;
      } else {
         return var5;
      }
   }

   private void cache0(DefaultDnsCache.DefaultDnsCacheEntry var1, int var2, EventLoop var3) {
      DefaultDnsCache.Entries var4 = (DefaultDnsCache.Entries)this.resolveCache.get(var1.hostname());
      if (var4 == null) {
         var4 = new DefaultDnsCache.Entries(var1);
         DefaultDnsCache.Entries var5 = (DefaultDnsCache.Entries)this.resolveCache.putIfAbsent(var1.hostname(), var4);
         if (var5 != null) {
            var4 = var5;
         }
      }

      var4.add(var1);
      this.scheduleCacheExpiration(var1, var2, var3);
   }

   private void scheduleCacheExpiration(final DefaultDnsCache.DefaultDnsCacheEntry var1, int var2, EventLoop var3) {
      var1.scheduleExpiration(var3, new Runnable() {
         public void run() {
            DefaultDnsCache.Entries var1x = (DefaultDnsCache.Entries)DefaultDnsCache.this.resolveCache.remove(var1.hostname);
            if (var1x != null) {
               var1x.clearAndCancel();
            }

         }
      }, (long)var2, TimeUnit.SECONDS);
   }

   public String toString() {
      return "DefaultDnsCache(minTtl=" + this.minTtl + ", maxTtl=" + this.maxTtl + ", negativeTtl=" + this.negativeTtl + ", cached resolved hostname=" + this.resolveCache.size() + ")";
   }

   static {
      MAX_SUPPORTED_TTL_SECS = (int)TimeUnit.DAYS.toSeconds(730L);
   }

   private static final class Entries extends AtomicReference<List<DefaultDnsCache.DefaultDnsCacheEntry>> {
      Entries(DefaultDnsCache.DefaultDnsCacheEntry var1) {
         super(Collections.singletonList(var1));
      }

      void add(DefaultDnsCache.DefaultDnsCacheEntry var1) {
         List var2;
         if (var1.cause() != null) {
            var2 = (List)this.getAndSet(Collections.singletonList(var1));
            cancelExpiration(var2);
         } else {
            DefaultDnsCache.DefaultDnsCacheEntry var3;
            label60:
            do {
               while(true) {
                  while(true) {
                     var2 = (List)this.get();
                     if (!var2.isEmpty()) {
                        var3 = (DefaultDnsCache.DefaultDnsCacheEntry)var2.get(0);
                        if (var3.cause() != null) {
                           assert var2.size() == 1;
                           continue label60;
                        }

                        ArrayList var4 = new ArrayList(var2.size() + 1);
                        DefaultDnsCache.DefaultDnsCacheEntry var5 = null;

                        for(int var6 = 0; var6 < var2.size(); ++var6) {
                           DefaultDnsCache.DefaultDnsCacheEntry var7 = (DefaultDnsCache.DefaultDnsCacheEntry)var2.get(var6);
                           if (!var1.address().equals(var7.address())) {
                              var4.add(var7);
                           } else {
                              assert var5 == null;

                              var5 = var7;
                           }
                        }

                        var4.add(var1);
                        if (this.compareAndSet(var2, var4)) {
                           if (var5 != null) {
                              var5.cancelExpiration();
                           }

                           return;
                        }
                     } else if (this.compareAndSet(var2, Collections.singletonList(var1))) {
                        return;
                     }
                  }
               }
            } while(!this.compareAndSet(var2, Collections.singletonList(var1)));

            var3.cancelExpiration();
         }
      }

      boolean clearAndCancel() {
         List var1 = (List)this.getAndSet(Collections.emptyList());
         if (var1.isEmpty()) {
            return false;
         } else {
            cancelExpiration(var1);
            return true;
         }
      }

      private static void cancelExpiration(List<DefaultDnsCache.DefaultDnsCacheEntry> var0) {
         int var1 = var0.size();

         for(int var2 = 0; var2 < var1; ++var2) {
            ((DefaultDnsCache.DefaultDnsCacheEntry)var0.get(var2)).cancelExpiration();
         }

      }
   }

   private static final class DefaultDnsCacheEntry implements DnsCacheEntry {
      private final String hostname;
      private final InetAddress address;
      private final Throwable cause;
      private volatile ScheduledFuture<?> expirationFuture;

      DefaultDnsCacheEntry(String var1, InetAddress var2) {
         super();
         this.hostname = (String)ObjectUtil.checkNotNull(var1, "hostname");
         this.address = (InetAddress)ObjectUtil.checkNotNull(var2, "address");
         this.cause = null;
      }

      DefaultDnsCacheEntry(String var1, Throwable var2) {
         super();
         this.hostname = (String)ObjectUtil.checkNotNull(var1, "hostname");
         this.cause = (Throwable)ObjectUtil.checkNotNull(var2, "cause");
         this.address = null;
      }

      public InetAddress address() {
         return this.address;
      }

      public Throwable cause() {
         return this.cause;
      }

      String hostname() {
         return this.hostname;
      }

      void scheduleExpiration(EventLoop var1, Runnable var2, long var3, TimeUnit var5) {
         assert this.expirationFuture == null : "expiration task scheduled already";

         this.expirationFuture = var1.schedule(var2, var3, var5);
      }

      void cancelExpiration() {
         ScheduledFuture var1 = this.expirationFuture;
         if (var1 != null) {
            var1.cancel(false);
         }

      }

      public String toString() {
         return this.cause != null ? this.hostname + '/' + this.cause : this.address.toString();
      }
   }
}
