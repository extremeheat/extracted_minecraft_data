package io.netty.resolver;

import io.netty.util.concurrent.EventExecutor;
import java.net.InetSocketAddress;

public final class DefaultAddressResolverGroup extends AddressResolverGroup<InetSocketAddress> {
   public static final DefaultAddressResolverGroup INSTANCE = new DefaultAddressResolverGroup();

   private DefaultAddressResolverGroup() {
      super();
   }

   protected AddressResolver<InetSocketAddress> newResolver(EventExecutor var1) throws Exception {
      return (new DefaultNameResolver(var1)).asAddressResolver();
   }
}
