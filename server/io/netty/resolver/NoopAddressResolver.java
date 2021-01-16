package io.netty.resolver;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.List;

public class NoopAddressResolver extends AbstractAddressResolver<SocketAddress> {
   public NoopAddressResolver(EventExecutor var1) {
      super(var1);
   }

   protected boolean doIsResolved(SocketAddress var1) {
      return true;
   }

   protected void doResolve(SocketAddress var1, Promise<SocketAddress> var2) throws Exception {
      var2.setSuccess(var1);
   }

   protected void doResolveAll(SocketAddress var1, Promise<List<SocketAddress>> var2) throws Exception {
      var2.setSuccess(Collections.singletonList(var1));
   }
}
