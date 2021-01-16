package io.netty.resolver.dns;

import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.DatagramChannel;
import io.netty.resolver.AddressResolver;
import io.netty.resolver.NameResolver;
import io.netty.resolver.RoundRobinInetAddressResolver;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class RoundRobinDnsAddressResolverGroup extends DnsAddressResolverGroup {
   public RoundRobinDnsAddressResolverGroup(DnsNameResolverBuilder var1) {
      super(var1);
   }

   public RoundRobinDnsAddressResolverGroup(Class<? extends DatagramChannel> var1, DnsServerAddressStreamProvider var2) {
      super(var1, var2);
   }

   public RoundRobinDnsAddressResolverGroup(ChannelFactory<? extends DatagramChannel> var1, DnsServerAddressStreamProvider var2) {
      super(var1, var2);
   }

   protected final AddressResolver<InetSocketAddress> newAddressResolver(EventLoop var1, NameResolver<InetAddress> var2) throws Exception {
      return (new RoundRobinInetAddressResolver(var1, var2)).asAddressResolver();
   }
}
