package io.netty.resolver.dns;

import io.netty.resolver.NameResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

final class InflightNameResolver<T> implements NameResolver<T> {
   private final EventExecutor executor;
   private final NameResolver<T> delegate;
   private final ConcurrentMap<String, Promise<T>> resolvesInProgress;
   private final ConcurrentMap<String, Promise<List<T>>> resolveAllsInProgress;

   InflightNameResolver(EventExecutor var1, NameResolver<T> var2, ConcurrentMap<String, Promise<T>> var3, ConcurrentMap<String, Promise<List<T>>> var4) {
      super();
      this.executor = (EventExecutor)ObjectUtil.checkNotNull(var1, "executor");
      this.delegate = (NameResolver)ObjectUtil.checkNotNull(var2, "delegate");
      this.resolvesInProgress = (ConcurrentMap)ObjectUtil.checkNotNull(var3, "resolvesInProgress");
      this.resolveAllsInProgress = (ConcurrentMap)ObjectUtil.checkNotNull(var4, "resolveAllsInProgress");
   }

   public Future<T> resolve(String var1) {
      return this.resolve(var1, this.executor.newPromise());
   }

   public Future<List<T>> resolveAll(String var1) {
      return this.resolveAll(var1, this.executor.newPromise());
   }

   public void close() {
      this.delegate.close();
   }

   public Promise<T> resolve(String var1, Promise<T> var2) {
      return this.resolve(this.resolvesInProgress, var1, var2, false);
   }

   public Promise<List<T>> resolveAll(String var1, Promise<List<T>> var2) {
      return this.resolve(this.resolveAllsInProgress, var1, var2, true);
   }

   private <U> Promise<U> resolve(final ConcurrentMap<String, Promise<U>> var1, final String var2, final Promise<U> var3, boolean var4) {
      Promise var5 = (Promise)var1.putIfAbsent(var2, var3);
      if (var5 != null) {
         if (var5.isDone()) {
            transferResult(var5, var3);
         } else {
            var5.addListener(new FutureListener<U>() {
               public void operationComplete(Future<U> var1) throws Exception {
                  InflightNameResolver.transferResult(var1, var3);
               }
            });
         }
      } else {
         try {
            if (var4) {
               this.delegate.resolveAll(var2, var3);
            } else {
               this.delegate.resolve(var2, var3);
            }
         } finally {
            if (var3.isDone()) {
               var1.remove(var2);
            } else {
               var3.addListener(new FutureListener<U>() {
                  public void operationComplete(Future<U> var1x) throws Exception {
                     var1.remove(var2);
                  }
               });
            }

         }
      }

      return var3;
   }

   private static <T> void transferResult(Future<T> var0, Promise<T> var1) {
      if (var0.isSuccess()) {
         var1.trySuccess(var0.getNow());
      } else {
         var1.tryFailure(var0.cause());
      }

   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + '(' + this.delegate + ')';
   }
}
