package io.netty.resolver;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.PlatformDependent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoundRobinInetAddressResolver extends InetNameResolver {
   private final NameResolver<InetAddress> nameResolver;

   public RoundRobinInetAddressResolver(EventExecutor var1, NameResolver<InetAddress> var2) {
      super(var1);
      this.nameResolver = var2;
   }

   protected void doResolve(final String var1, final Promise<InetAddress> var2) throws Exception {
      this.nameResolver.resolveAll(var1).addListener(new FutureListener<List<InetAddress>>() {
         public void operationComplete(Future<List<InetAddress>> var1x) throws Exception {
            if (var1x.isSuccess()) {
               List var2x = (List)var1x.getNow();
               int var3 = var2x.size();
               if (var3 > 0) {
                  var2.setSuccess(var2x.get(RoundRobinInetAddressResolver.randomIndex(var3)));
               } else {
                  var2.setFailure(new UnknownHostException(var1));
               }
            } else {
               var2.setFailure(var1x.cause());
            }

         }
      });
   }

   protected void doResolveAll(String var1, final Promise<List<InetAddress>> var2) throws Exception {
      this.nameResolver.resolveAll(var1).addListener(new FutureListener<List<InetAddress>>() {
         public void operationComplete(Future<List<InetAddress>> var1) throws Exception {
            if (var1.isSuccess()) {
               List var2x = (List)var1.getNow();
               if (!var2x.isEmpty()) {
                  ArrayList var3 = new ArrayList(var2x);
                  Collections.rotate(var3, RoundRobinInetAddressResolver.randomIndex(var2x.size()));
                  var2.setSuccess(var3);
               } else {
                  var2.setSuccess(var2x);
               }
            } else {
               var2.setFailure(var1.cause());
            }

         }
      });
   }

   private static int randomIndex(int var0) {
      return var0 == 1 ? 0 : PlatformDependent.threadLocalRandom().nextInt(var0);
   }
}
