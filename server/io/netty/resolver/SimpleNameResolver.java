package io.netty.resolver;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public abstract class SimpleNameResolver<T> implements NameResolver<T> {
   private final EventExecutor executor;

   protected SimpleNameResolver(EventExecutor var1) {
      super();
      this.executor = (EventExecutor)ObjectUtil.checkNotNull(var1, "executor");
   }

   protected EventExecutor executor() {
      return this.executor;
   }

   public final Future<T> resolve(String var1) {
      Promise var2 = this.executor().newPromise();
      return this.resolve(var1, var2);
   }

   public Future<T> resolve(String var1, Promise<T> var2) {
      ObjectUtil.checkNotNull(var2, "promise");

      try {
         this.doResolve(var1, var2);
         return var2;
      } catch (Exception var4) {
         return var2.setFailure(var4);
      }
   }

   public final Future<List<T>> resolveAll(String var1) {
      Promise var2 = this.executor().newPromise();
      return this.resolveAll(var1, var2);
   }

   public Future<List<T>> resolveAll(String var1, Promise<List<T>> var2) {
      ObjectUtil.checkNotNull(var2, "promise");

      try {
         this.doResolveAll(var1, var2);
         return var2;
      } catch (Exception var4) {
         return var2.setFailure(var4);
      }
   }

   protected abstract void doResolve(String var1, Promise<T> var2) throws Exception;

   protected abstract void doResolveAll(String var1, Promise<List<T>> var2) throws Exception;

   public void close() {
   }
}
