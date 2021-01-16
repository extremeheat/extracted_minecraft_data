package io.netty.resolver;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.SocketUtils;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

public class DefaultNameResolver extends InetNameResolver {
   public DefaultNameResolver(EventExecutor var1) {
      super(var1);
   }

   protected void doResolve(String var1, Promise<InetAddress> var2) throws Exception {
      try {
         var2.setSuccess(SocketUtils.addressByName(var1));
      } catch (UnknownHostException var4) {
         var2.setFailure(var4);
      }

   }

   protected void doResolveAll(String var1, Promise<List<InetAddress>> var2) throws Exception {
      try {
         var2.setSuccess(Arrays.asList(SocketUtils.allAddressesByName(var1)));
      } catch (UnknownHostException var4) {
         var2.setFailure(var4);
      }

   }
}
