package io.netty.resolver;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import java.util.Arrays;
import java.util.List;

public final class CompositeNameResolver<T> extends SimpleNameResolver<T> {
   private final NameResolver<T>[] resolvers;

   public CompositeNameResolver(EventExecutor var1, NameResolver<T>... var2) {
      super(var1);
      ObjectUtil.checkNotNull(var2, "resolvers");

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var2[var3] == null) {
            throw new NullPointerException("resolvers[" + var3 + ']');
         }
      }

      if (var2.length < 2) {
         throw new IllegalArgumentException("resolvers: " + Arrays.asList(var2) + " (expected: at least 2 resolvers)");
      } else {
         this.resolvers = (NameResolver[])var2.clone();
      }
   }

   protected void doResolve(String var1, Promise<T> var2) throws Exception {
      this.doResolveRec(var1, var2, 0, (Throwable)null);
   }

   private void doResolveRec(final String var1, final Promise<T> var2, final int var3, Throwable var4) throws Exception {
      if (var3 >= this.resolvers.length) {
         var2.setFailure(var4);
      } else {
         NameResolver var5 = this.resolvers[var3];
         var5.resolve(var1).addListener(new FutureListener<T>() {
            public void operationComplete(Future<T> var1x) throws Exception {
               if (var1x.isSuccess()) {
                  var2.setSuccess(var1x.getNow());
               } else {
                  CompositeNameResolver.this.doResolveRec(var1, var2, var3 + 1, var1x.cause());
               }

            }
         });
      }

   }

   protected void doResolveAll(String var1, Promise<List<T>> var2) throws Exception {
      this.doResolveAllRec(var1, var2, 0, (Throwable)null);
   }

   private void doResolveAllRec(final String var1, final Promise<List<T>> var2, final int var3, Throwable var4) throws Exception {
      if (var3 >= this.resolvers.length) {
         var2.setFailure(var4);
      } else {
         NameResolver var5 = this.resolvers[var3];
         var5.resolveAll(var1).addListener(new FutureListener<List<T>>() {
            public void operationComplete(Future<List<T>> var1x) throws Exception {
               if (var1x.isSuccess()) {
                  var2.setSuccess(var1x.getNow());
               } else {
                  CompositeNameResolver.this.doResolveAllRec(var1, var2, var3 + 1, var1x.cause());
               }

            }
         });
      }

   }
}
