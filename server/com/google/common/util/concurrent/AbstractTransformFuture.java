package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.ForOverride;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractTransformFuture<I, O, F, T> extends AbstractFuture.TrustedFuture<O> implements Runnable {
   @Nullable
   ListenableFuture<? extends I> inputFuture;
   @Nullable
   F function;

   static <I, O> ListenableFuture<O> create(ListenableFuture<I> var0, AsyncFunction<? super I, ? extends O> var1) {
      AbstractTransformFuture.AsyncTransformFuture var2 = new AbstractTransformFuture.AsyncTransformFuture(var0, var1);
      var0.addListener(var2, MoreExecutors.directExecutor());
      return var2;
   }

   static <I, O> ListenableFuture<O> create(ListenableFuture<I> var0, AsyncFunction<? super I, ? extends O> var1, Executor var2) {
      Preconditions.checkNotNull(var2);
      AbstractTransformFuture.AsyncTransformFuture var3 = new AbstractTransformFuture.AsyncTransformFuture(var0, var1);
      var0.addListener(var3, MoreExecutors.rejectionPropagatingExecutor(var2, var3));
      return var3;
   }

   static <I, O> ListenableFuture<O> create(ListenableFuture<I> var0, Function<? super I, ? extends O> var1) {
      Preconditions.checkNotNull(var1);
      AbstractTransformFuture.TransformFuture var2 = new AbstractTransformFuture.TransformFuture(var0, var1);
      var0.addListener(var2, MoreExecutors.directExecutor());
      return var2;
   }

   static <I, O> ListenableFuture<O> create(ListenableFuture<I> var0, Function<? super I, ? extends O> var1, Executor var2) {
      Preconditions.checkNotNull(var1);
      AbstractTransformFuture.TransformFuture var3 = new AbstractTransformFuture.TransformFuture(var0, var1);
      var0.addListener(var3, MoreExecutors.rejectionPropagatingExecutor(var2, var3));
      return var3;
   }

   AbstractTransformFuture(ListenableFuture<? extends I> var1, F var2) {
      super();
      this.inputFuture = (ListenableFuture)Preconditions.checkNotNull(var1);
      this.function = Preconditions.checkNotNull(var2);
   }

   public final void run() {
      ListenableFuture var1 = this.inputFuture;
      Object var2 = this.function;
      if (!(this.isCancelled() | var1 == null | var2 == null)) {
         this.inputFuture = null;
         this.function = null;

         Object var3;
         try {
            var3 = Futures.getDone(var1);
         } catch (CancellationException var8) {
            this.cancel(false);
            return;
         } catch (ExecutionException var9) {
            this.setException(var9.getCause());
            return;
         } catch (RuntimeException var10) {
            this.setException(var10);
            return;
         } catch (Error var11) {
            this.setException(var11);
            return;
         }

         Object var4;
         try {
            var4 = this.doTransform(var2, var3);
         } catch (UndeclaredThrowableException var6) {
            this.setException(var6.getCause());
            return;
         } catch (Throwable var7) {
            this.setException(var7);
            return;
         }

         this.setResult(var4);
      }
   }

   @Nullable
   @ForOverride
   abstract T doTransform(F var1, @Nullable I var2) throws Exception;

   @ForOverride
   abstract void setResult(@Nullable T var1);

   protected final void afterDone() {
      this.maybePropagateCancellation(this.inputFuture);
      this.inputFuture = null;
      this.function = null;
   }

   private static final class TransformFuture<I, O> extends AbstractTransformFuture<I, O, Function<? super I, ? extends O>, O> {
      TransformFuture(ListenableFuture<? extends I> var1, Function<? super I, ? extends O> var2) {
         super(var1, var2);
      }

      @Nullable
      O doTransform(Function<? super I, ? extends O> var1, @Nullable I var2) {
         return var1.apply(var2);
      }

      void setResult(@Nullable O var1) {
         this.set(var1);
      }
   }

   private static final class AsyncTransformFuture<I, O> extends AbstractTransformFuture<I, O, AsyncFunction<? super I, ? extends O>, ListenableFuture<? extends O>> {
      AsyncTransformFuture(ListenableFuture<? extends I> var1, AsyncFunction<? super I, ? extends O> var2) {
         super(var1, var2);
      }

      ListenableFuture<? extends O> doTransform(AsyncFunction<? super I, ? extends O> var1, @Nullable I var2) throws Exception {
         ListenableFuture var3 = var1.apply(var2);
         Preconditions.checkNotNull(var3, "AsyncFunction.apply returned null instead of a Future. Did you mean to return immediateFuture(null)?");
         return var3;
      }

      void setResult(ListenableFuture<? extends O> var1) {
         this.setFuture(var1);
      }
   }
}
