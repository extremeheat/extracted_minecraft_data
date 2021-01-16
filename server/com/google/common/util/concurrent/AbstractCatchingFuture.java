package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.ForOverride;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractCatchingFuture<V, X extends Throwable, F, T> extends AbstractFuture.TrustedFuture<V> implements Runnable {
   @Nullable
   ListenableFuture<? extends V> inputFuture;
   @Nullable
   Class<X> exceptionType;
   @Nullable
   F fallback;

   static <X extends Throwable, V> ListenableFuture<V> create(ListenableFuture<? extends V> var0, Class<X> var1, Function<? super X, ? extends V> var2) {
      AbstractCatchingFuture.CatchingFuture var3 = new AbstractCatchingFuture.CatchingFuture(var0, var1, var2);
      var0.addListener(var3, MoreExecutors.directExecutor());
      return var3;
   }

   static <V, X extends Throwable> ListenableFuture<V> create(ListenableFuture<? extends V> var0, Class<X> var1, Function<? super X, ? extends V> var2, Executor var3) {
      AbstractCatchingFuture.CatchingFuture var4 = new AbstractCatchingFuture.CatchingFuture(var0, var1, var2);
      var0.addListener(var4, MoreExecutors.rejectionPropagatingExecutor(var3, var4));
      return var4;
   }

   static <X extends Throwable, V> ListenableFuture<V> create(ListenableFuture<? extends V> var0, Class<X> var1, AsyncFunction<? super X, ? extends V> var2) {
      AbstractCatchingFuture.AsyncCatchingFuture var3 = new AbstractCatchingFuture.AsyncCatchingFuture(var0, var1, var2);
      var0.addListener(var3, MoreExecutors.directExecutor());
      return var3;
   }

   static <X extends Throwable, V> ListenableFuture<V> create(ListenableFuture<? extends V> var0, Class<X> var1, AsyncFunction<? super X, ? extends V> var2, Executor var3) {
      AbstractCatchingFuture.AsyncCatchingFuture var4 = new AbstractCatchingFuture.AsyncCatchingFuture(var0, var1, var2);
      var0.addListener(var4, MoreExecutors.rejectionPropagatingExecutor(var3, var4));
      return var4;
   }

   AbstractCatchingFuture(ListenableFuture<? extends V> var1, Class<X> var2, F var3) {
      super();
      this.inputFuture = (ListenableFuture)Preconditions.checkNotNull(var1);
      this.exceptionType = (Class)Preconditions.checkNotNull(var2);
      this.fallback = Preconditions.checkNotNull(var3);
   }

   public final void run() {
      ListenableFuture var1 = this.inputFuture;
      Class var2 = this.exceptionType;
      Object var3 = this.fallback;
      if (!(var1 == null | var2 == null | var3 == null | this.isCancelled())) {
         this.inputFuture = null;
         this.exceptionType = null;
         this.fallback = null;
         Object var4 = null;
         Throwable var5 = null;

         try {
            var4 = Futures.getDone(var1);
         } catch (ExecutionException var10) {
            var5 = (Throwable)Preconditions.checkNotNull(var10.getCause());
         } catch (Throwable var11) {
            var5 = var11;
         }

         if (var5 == null) {
            this.set(var4);
         } else if (!Platform.isInstanceOfThrowableClass(var5, var2)) {
            this.setException(var5);
         } else {
            Throwable var6 = var5;

            Object var7;
            try {
               var7 = this.doFallback(var3, var6);
            } catch (Throwable var9) {
               this.setException(var9);
               return;
            }

            this.setResult(var7);
         }
      }
   }

   @Nullable
   @ForOverride
   abstract T doFallback(F var1, X var2) throws Exception;

   @ForOverride
   abstract void setResult(@Nullable T var1);

   protected final void afterDone() {
      this.maybePropagateCancellation(this.inputFuture);
      this.inputFuture = null;
      this.exceptionType = null;
      this.fallback = null;
   }

   private static final class CatchingFuture<V, X extends Throwable> extends AbstractCatchingFuture<V, X, Function<? super X, ? extends V>, V> {
      CatchingFuture(ListenableFuture<? extends V> var1, Class<X> var2, Function<? super X, ? extends V> var3) {
         super(var1, var2, var3);
      }

      @Nullable
      V doFallback(Function<? super X, ? extends V> var1, X var2) throws Exception {
         return var1.apply(var2);
      }

      void setResult(@Nullable V var1) {
         this.set(var1);
      }
   }

   private static final class AsyncCatchingFuture<V, X extends Throwable> extends AbstractCatchingFuture<V, X, AsyncFunction<? super X, ? extends V>, ListenableFuture<? extends V>> {
      AsyncCatchingFuture(ListenableFuture<? extends V> var1, Class<X> var2, AsyncFunction<? super X, ? extends V> var3) {
         super(var1, var2, var3);
      }

      ListenableFuture<? extends V> doFallback(AsyncFunction<? super X, ? extends V> var1, X var2) throws Exception {
         ListenableFuture var3 = var1.apply(var2);
         Preconditions.checkNotNull(var3, "AsyncFunction.apply returned null instead of a Future. Did you mean to return immediateFuture(null)?");
         return var3;
      }

      void setResult(ListenableFuture<? extends V> var1) {
         this.setFuture(var1);
      }
   }
}
