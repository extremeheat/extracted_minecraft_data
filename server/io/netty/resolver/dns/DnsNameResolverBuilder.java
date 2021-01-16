package io.netty.resolver.dns;

import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.resolver.HostsFileEntriesResolver;
import io.netty.resolver.ResolvedAddressTypes;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public final class DnsNameResolverBuilder {
   private EventLoop eventLoop;
   private ChannelFactory<? extends DatagramChannel> channelFactory;
   private DnsCache resolveCache;
   private DnsCache authoritativeDnsServerCache;
   private Integer minTtl;
   private Integer maxTtl;
   private Integer negativeTtl;
   private long queryTimeoutMillis = 5000L;
   private ResolvedAddressTypes resolvedAddressTypes;
   private boolean recursionDesired;
   private int maxQueriesPerResolve;
   private boolean traceEnabled;
   private int maxPayloadSize;
   private boolean optResourceEnabled;
   private HostsFileEntriesResolver hostsFileEntriesResolver;
   private DnsServerAddressStreamProvider dnsServerAddressStreamProvider;
   private DnsQueryLifecycleObserverFactory dnsQueryLifecycleObserverFactory;
   private String[] searchDomains;
   private int ndots;
   private boolean decodeIdn;

   public DnsNameResolverBuilder() {
      super();
      this.resolvedAddressTypes = DnsNameResolver.DEFAULT_RESOLVE_ADDRESS_TYPES;
      this.recursionDesired = true;
      this.maxQueriesPerResolve = 16;
      this.maxPayloadSize = 4096;
      this.optResourceEnabled = true;
      this.hostsFileEntriesResolver = HostsFileEntriesResolver.DEFAULT;
      this.dnsServerAddressStreamProvider = DnsServerAddressStreamProviders.platformDefault();
      this.dnsQueryLifecycleObserverFactory = NoopDnsQueryLifecycleObserverFactory.INSTANCE;
      this.ndots = -1;
      this.decodeIdn = true;
   }

   public DnsNameResolverBuilder(EventLoop var1) {
      super();
      this.resolvedAddressTypes = DnsNameResolver.DEFAULT_RESOLVE_ADDRESS_TYPES;
      this.recursionDesired = true;
      this.maxQueriesPerResolve = 16;
      this.maxPayloadSize = 4096;
      this.optResourceEnabled = true;
      this.hostsFileEntriesResolver = HostsFileEntriesResolver.DEFAULT;
      this.dnsServerAddressStreamProvider = DnsServerAddressStreamProviders.platformDefault();
      this.dnsQueryLifecycleObserverFactory = NoopDnsQueryLifecycleObserverFactory.INSTANCE;
      this.ndots = -1;
      this.decodeIdn = true;
      this.eventLoop(var1);
   }

   public DnsNameResolverBuilder eventLoop(EventLoop var1) {
      this.eventLoop = var1;
      return this;
   }

   protected ChannelFactory<? extends DatagramChannel> channelFactory() {
      return this.channelFactory;
   }

   public DnsNameResolverBuilder channelFactory(ChannelFactory<? extends DatagramChannel> var1) {
      this.channelFactory = var1;
      return this;
   }

   public DnsNameResolverBuilder channelType(Class<? extends DatagramChannel> var1) {
      return this.channelFactory(new ReflectiveChannelFactory(var1));
   }

   public DnsNameResolverBuilder resolveCache(DnsCache var1) {
      this.resolveCache = var1;
      return this;
   }

   public DnsNameResolverBuilder dnsQueryLifecycleObserverFactory(DnsQueryLifecycleObserverFactory var1) {
      this.dnsQueryLifecycleObserverFactory = (DnsQueryLifecycleObserverFactory)ObjectUtil.checkNotNull(var1, "lifecycleObserverFactory");
      return this;
   }

   public DnsNameResolverBuilder authoritativeDnsServerCache(DnsCache var1) {
      this.authoritativeDnsServerCache = var1;
      return this;
   }

   public DnsNameResolverBuilder ttl(int var1, int var2) {
      this.maxTtl = var2;
      this.minTtl = var1;
      return this;
   }

   public DnsNameResolverBuilder negativeTtl(int var1) {
      this.negativeTtl = var1;
      return this;
   }

   public DnsNameResolverBuilder queryTimeoutMillis(long var1) {
      this.queryTimeoutMillis = var1;
      return this;
   }

   public static ResolvedAddressTypes computeResolvedAddressTypes(InternetProtocolFamily... var0) {
      if (var0 != null && var0.length != 0) {
         if (var0.length > 2) {
            throw new IllegalArgumentException("No more than 2 InternetProtocolFamilies");
         } else {
            switch(var0[0]) {
            case IPv4:
               return var0.length >= 2 && var0[1] == InternetProtocolFamily.IPv6 ? ResolvedAddressTypes.IPV4_PREFERRED : ResolvedAddressTypes.IPV4_ONLY;
            case IPv6:
               return var0.length >= 2 && var0[1] == InternetProtocolFamily.IPv4 ? ResolvedAddressTypes.IPV6_PREFERRED : ResolvedAddressTypes.IPV6_ONLY;
            default:
               throw new IllegalArgumentException("Couldn't resolve ResolvedAddressTypes from InternetProtocolFamily array");
            }
         }
      } else {
         return DnsNameResolver.DEFAULT_RESOLVE_ADDRESS_TYPES;
      }
   }

   public DnsNameResolverBuilder resolvedAddressTypes(ResolvedAddressTypes var1) {
      this.resolvedAddressTypes = var1;
      return this;
   }

   public DnsNameResolverBuilder recursionDesired(boolean var1) {
      this.recursionDesired = var1;
      return this;
   }

   public DnsNameResolverBuilder maxQueriesPerResolve(int var1) {
      this.maxQueriesPerResolve = var1;
      return this;
   }

   public DnsNameResolverBuilder traceEnabled(boolean var1) {
      this.traceEnabled = var1;
      return this;
   }

   public DnsNameResolverBuilder maxPayloadSize(int var1) {
      this.maxPayloadSize = var1;
      return this;
   }

   public DnsNameResolverBuilder optResourceEnabled(boolean var1) {
      this.optResourceEnabled = var1;
      return this;
   }

   public DnsNameResolverBuilder hostsFileEntriesResolver(HostsFileEntriesResolver var1) {
      this.hostsFileEntriesResolver = var1;
      return this;
   }

   protected DnsServerAddressStreamProvider nameServerProvider() {
      return this.dnsServerAddressStreamProvider;
   }

   public DnsNameResolverBuilder nameServerProvider(DnsServerAddressStreamProvider var1) {
      this.dnsServerAddressStreamProvider = (DnsServerAddressStreamProvider)ObjectUtil.checkNotNull(var1, "dnsServerAddressStreamProvider");
      return this;
   }

   public DnsNameResolverBuilder searchDomains(Iterable<String> var1) {
      ObjectUtil.checkNotNull(var1, "searchDomains");
      ArrayList var2 = new ArrayList(4);
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         if (var4 == null) {
            break;
         }

         if (!var2.contains(var4)) {
            var2.add(var4);
         }
      }

      this.searchDomains = (String[])var2.toArray(new String[var2.size()]);
      return this;
   }

   public DnsNameResolverBuilder ndots(int var1) {
      this.ndots = var1;
      return this;
   }

   private DnsCache newCache() {
      return new DefaultDnsCache(ObjectUtil.intValue(this.minTtl, 0), ObjectUtil.intValue(this.maxTtl, 2147483647), ObjectUtil.intValue(this.negativeTtl, 0));
   }

   public DnsNameResolverBuilder decodeIdn(boolean var1) {
      this.decodeIdn = var1;
      return this;
   }

   public DnsNameResolver build() {
      if (this.eventLoop == null) {
         throw new IllegalStateException("eventLoop should be specified to build a DnsNameResolver.");
      } else if (this.resolveCache != null && (this.minTtl != null || this.maxTtl != null || this.negativeTtl != null)) {
         throw new IllegalStateException("resolveCache and TTLs are mutually exclusive");
      } else if (this.authoritativeDnsServerCache != null && (this.minTtl != null || this.maxTtl != null || this.negativeTtl != null)) {
         throw new IllegalStateException("authoritativeDnsServerCache and TTLs are mutually exclusive");
      } else {
         DnsCache var1 = this.resolveCache != null ? this.resolveCache : this.newCache();
         DnsCache var2 = this.authoritativeDnsServerCache != null ? this.authoritativeDnsServerCache : this.newCache();
         return new DnsNameResolver(this.eventLoop, this.channelFactory, var1, var2, this.dnsQueryLifecycleObserverFactory, this.queryTimeoutMillis, this.resolvedAddressTypes, this.recursionDesired, this.maxQueriesPerResolve, this.traceEnabled, this.maxPayloadSize, this.optResourceEnabled, this.hostsFileEntriesResolver, this.dnsServerAddressStreamProvider, this.searchDomains, this.ndots, this.decodeIdn);
      }
   }

   public DnsNameResolverBuilder copy() {
      DnsNameResolverBuilder var1 = new DnsNameResolverBuilder();
      if (this.eventLoop != null) {
         var1.eventLoop(this.eventLoop);
      }

      if (this.channelFactory != null) {
         var1.channelFactory(this.channelFactory);
      }

      if (this.resolveCache != null) {
         var1.resolveCache(this.resolveCache);
      }

      if (this.maxTtl != null && this.minTtl != null) {
         var1.ttl(this.minTtl, this.maxTtl);
      }

      if (this.negativeTtl != null) {
         var1.negativeTtl(this.negativeTtl);
      }

      if (this.authoritativeDnsServerCache != null) {
         var1.authoritativeDnsServerCache(this.authoritativeDnsServerCache);
      }

      if (this.dnsQueryLifecycleObserverFactory != null) {
         var1.dnsQueryLifecycleObserverFactory(this.dnsQueryLifecycleObserverFactory);
      }

      var1.queryTimeoutMillis(this.queryTimeoutMillis);
      var1.resolvedAddressTypes(this.resolvedAddressTypes);
      var1.recursionDesired(this.recursionDesired);
      var1.maxQueriesPerResolve(this.maxQueriesPerResolve);
      var1.traceEnabled(this.traceEnabled);
      var1.maxPayloadSize(this.maxPayloadSize);
      var1.optResourceEnabled(this.optResourceEnabled);
      var1.hostsFileEntriesResolver(this.hostsFileEntriesResolver);
      if (this.dnsServerAddressStreamProvider != null) {
         var1.nameServerProvider(this.dnsServerAddressStreamProvider);
      }

      if (this.searchDomains != null) {
         var1.searchDomains(Arrays.asList(this.searchDomains));
      }

      var1.ndots(this.ndots);
      var1.decodeIdn(this.decodeIdn);
      return var1;
   }
}
