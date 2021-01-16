package io.netty.resolver.dns;

import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.DatagramChannel;
import io.netty.resolver.AddressResolver;
import io.netty.resolver.AddressResolverGroup;
import io.netty.resolver.InetSocketAddressResolver;
import io.netty.resolver.NameResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class DnsAddressResolverGroup extends AddressResolverGroup<InetSocketAddress> {
   private final DnsNameResolverBuilder dnsResolverBuilder;
   private final ConcurrentMap<String, Promise<InetAddress>> resolvesInProgress;
   private final ConcurrentMap<String, Promise<List<InetAddress>>> resolveAllsInProgress;

   public DnsAddressResolverGroup(DnsNameResolverBuilder var1) {
      super();
      this.resolvesInProgress = PlatformDependent.newConcurrentHashMap();
      this.resolveAllsInProgress = PlatformDependent.newConcurrentHashMap();
      this.dnsResolverBuilder = var1.copy();
   }

   public DnsAddressResolverGroup(Class<? extends DatagramChannel> var1, DnsServerAddressStreamProvider var2) {
      this(new DnsNameResolverBuilder());
      this.dnsResolverBuilder.channelType(var1).nameServerProvider(var2);
   }

   public DnsAddressResolverGroup(ChannelFactory<? extends DatagramChannel> var1, DnsServerAddressStreamProvider var2) {
      this(new DnsNameResolverBuilder());
      this.dnsResolverBuilder.channelFactory(var1).nameServerProvider(var2);
   }

   protected final AddressResolver<InetSocketAddress> newResolver(EventExecutor var1) throws Exception {
      if (!(var1 instanceof EventLoop)) {
         throw new IllegalStateException("unsupported executor type: " + StringUtil.simpleClassName((Object)var1) + " (expected: " + StringUtil.simpleClassName(EventLoop.class));
      } else {
         return this.newResolver((EventLoop)var1, this.dnsResolverBuilder.channelFactory(), this.dnsResolverBuilder.nameServerProvider());
      }
   }

   /** @deprecated */
   @Deprecated
   protected AddressResolver<InetSocketAddress> newResolver(EventLoop var1, ChannelFactory<? extends DatagramChannel> var2, DnsServerAddressStreamProvider var3) throws Exception {
      InflightNameResolver var4 = new InflightNameResolver(var1, this.newNameResolver(var1, var2, var3), this.resolvesInProgress, this.resolveAllsInProgress);
      return this.newAddressResolver(var1, var4);
   }

   protected NameResolver<InetAddress> newNameResolver(EventLoop var1, ChannelFactory<? extends DatagramChannel> var2, DnsServerAddressStreamProvider var3) throws Exception {
      return this.dnsResolverBuilder.eventLoop(var1).channelFactory(var2).nameServerProvider(var3).build();
   }

   protected AddressResolver<InetSocketAddress> newAddressResolver(EventLoop var1, NameResolver<InetAddress> var2) throws Exception {
      return new InetSocketAddressResolver(var1, var2);
   }
}
