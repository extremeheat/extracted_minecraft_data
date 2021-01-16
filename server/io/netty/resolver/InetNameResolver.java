package io.netty.resolver;

import io.netty.util.concurrent.EventExecutor;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public abstract class InetNameResolver extends SimpleNameResolver<InetAddress> {
   private volatile AddressResolver<InetSocketAddress> addressResolver;

   protected InetNameResolver(EventExecutor var1) {
      super(var1);
   }

   public AddressResolver<InetSocketAddress> asAddressResolver() {
      Object var1 = this.addressResolver;
      if (var1 == null) {
         synchronized(this) {
            var1 = this.addressResolver;
            if (var1 == null) {
               this.addressResolver = (AddressResolver)(var1 = new InetSocketAddressResolver(this.executor(), this));
            }
         }
      }

      return (AddressResolver)var1;
   }
}
