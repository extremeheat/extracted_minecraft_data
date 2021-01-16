package io.netty.resolver;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InetSocketAddressResolver extends AbstractAddressResolver<InetSocketAddress> {
   final NameResolver<InetAddress> nameResolver;

   public InetSocketAddressResolver(EventExecutor var1, NameResolver<InetAddress> var2) {
      super(var1, InetSocketAddress.class);
      this.nameResolver = var2;
   }

   protected boolean doIsResolved(InetSocketAddress var1) {
      return !var1.isUnresolved();
   }

   protected void doResolve(final InetSocketAddress var1, final Promise<InetSocketAddress> var2) throws Exception {
      this.nameResolver.resolve(var1.getHostName()).addListener(new FutureListener<InetAddress>() {
         public void operationComplete(Future<InetAddress> var1x) throws Exception {
            if (var1x.isSuccess()) {
               var2.setSuccess(new InetSocketAddress((InetAddress)var1x.getNow(), var1.getPort()));
            } else {
               var2.setFailure(var1x.cause());
            }

         }
      });
   }

   protected void doResolveAll(final InetSocketAddress var1, final Promise<List<InetSocketAddress>> var2) throws Exception {
      this.nameResolver.resolveAll(var1.getHostName()).addListener(new FutureListener<List<InetAddress>>() {
         public void operationComplete(Future<List<InetAddress>> var1x) throws Exception {
            if (var1x.isSuccess()) {
               List var2x = (List)var1x.getNow();
               ArrayList var3 = new ArrayList(var2x.size());
               Iterator var4 = var2x.iterator();

               while(var4.hasNext()) {
                  InetAddress var5 = (InetAddress)var4.next();
                  var3.add(new InetSocketAddress(var5, var1.getPort()));
               }

               var2.setSuccess(var3);
            } else {
               var2.setFailure(var1x.cause());
            }

         }
      });
   }

   public void close() {
      this.nameResolver.close();
   }
}
