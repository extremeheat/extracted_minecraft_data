package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import javax.annotation.Nullable;

@GwtCompatible
final class CombinedFuture<V> extends AggregateFuture<Object, V> {
   CombinedFuture(ImmutableCollection<? extends ListenableFuture<?>> var1, boolean var2, Executor var3, AsyncCallable<V> var4) {
      super();
      this.init(new CombinedFuture.CombinedFutureRunningState(var1, var2, new CombinedFuture.AsyncCallableInterruptibleTask(var4, var3)));
   }

   CombinedFuture(ImmutableCollection<? extends ListenableFuture<?>> var1, boolean var2, Executor var3, Callable<V> var4) {
      super();
      this.init(new CombinedFuture.CombinedFutureRunningState(var1, var2, new CombinedFuture.CallableInterruptibleTask(var4, var3)));
   }

   private final class CallableInterruptibleTask extends CombinedFuture<V>.CombinedFutureInterruptibleTask {
      private final Callable<V> callable;

      public CallableInterruptibleTask(Callable<V> var2, Executor var3) {
         super(var3);
         this.callable = (Callable)Preconditions.checkNotNull(var2);
      }

      void setValue() throws Exception {
         CombinedFuture.this.set(this.callable.call());
      }
   }

   private final class AsyncCallableInterruptibleTask extends CombinedFuture<V>.CombinedFutureInterruptibleTask {
      private final AsyncCallable<V> callable;

      public AsyncCallableInterruptibleTask(AsyncCallable<V> var2, Executor var3) {
         super(var3);
         this.callable = (AsyncCallable)Preconditions.checkNotNull(var2);
      }

      void setValue() throws Exception {
         CombinedFuture.this.setFuture(this.callable.call());
      }
   }

   private abstract class CombinedFutureInterruptibleTask extends InterruptibleTask {
      private final Executor listenerExecutor;
      volatile boolean thrownByExecute = true;

      public CombinedFutureInterruptibleTask(Executor var2) {
         super();
         this.listenerExecutor = (Executor)Preconditions.checkNotNull(var2);
      }

      final void runInterruptibly() {
         this.thrownByExecute = false;
         if (!CombinedFuture.this.isDone()) {
            try {
               this.setValue();
            } catch (ExecutionException var2) {
               CombinedFuture.this.setException(var2.getCause());
            } catch (CancellationException var3) {
               CombinedFuture.this.cancel(false);
            } catch (Throwable var4) {
               CombinedFuture.this.setException(var4);
            }
         }

      }

      final boolean wasInterrupted() {
         return CombinedFuture.this.wasInterrupted();
      }

      final void execute() {
         try {
            this.listenerExecutor.execute(this);
         } catch (RejectedExecutionException var2) {
            if (this.thrownByExecute) {
               CombinedFuture.this.setException(var2);
            }
         }

      }

      abstract void setValue() throws Exception;
   }

   private final class CombinedFutureRunningState extends AggregateFuture<Object, V>.RunningState {
      private CombinedFuture<V>.CombinedFutureInterruptibleTask task;

      CombinedFutureRunningState(ImmutableCollection<? extends ListenableFuture<? extends Object>> var2, boolean var3, CombinedFuture<V>.CombinedFutureInterruptibleTask var4) {
         super(var2, var3, false);
         this.task = var4;
      }

      void collectOneValue(boolean var1, int var2, @Nullable Object var3) {
      }

      void handleAllCompleted() {
         CombinedFuture.CombinedFutureInterruptibleTask var1 = this.task;
         if (var1 != null) {
            var1.execute();
         } else {
            Preconditions.checkState(CombinedFuture.this.isDone());
         }

      }

      void releaseResourcesAfterFailure() {
         super.releaseResourcesAfterFailure();
         this.task = null;
      }

      void interruptTask() {
         CombinedFuture.CombinedFutureInterruptibleTask var1 = this.task;
         if (var1 != null) {
            var1.interruptTask();
         }

      }
   }
}
