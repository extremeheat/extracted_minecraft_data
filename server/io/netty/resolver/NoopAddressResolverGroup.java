package io.netty.resolver;

import io.netty.util.concurrent.EventExecutor;
import java.net.SocketAddress;

public final class NoopAddressResolverGroup extends AddressResolverGroup<SocketAddress> {
   public static final NoopAddressResolverGroup INSTANCE = new NoopAddressResolverGroup();

   private NoopAddressResolverGroup() {
      super();
   }

   protected AddressResolver<SocketAddress> newResolver(EventExecutor var1) throws Exception {
      return new NoopAddressResolver(var1);
   }
}
