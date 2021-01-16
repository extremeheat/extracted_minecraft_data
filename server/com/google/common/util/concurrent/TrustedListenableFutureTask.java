package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;
import javax.annotation.Nullable;

@GwtCompatible
class TrustedListenableFutureTask<V> extends AbstractFuture.TrustedFuture<V> implements RunnableFuture<V> {
   private TrustedListenableFutureTask<V>.TrustedFutureInterruptibleTask task;

   static <V> TrustedListenableFutureTask<V> create(Callable<V> var0) {
      return new TrustedListenableFutureTask(var0);
   }

   static <V> TrustedListenableFutureTask<V> create(Runnable var0, @Nullable V var1) {
      return new TrustedListenableFutureTask(Executors.callable(var0, var1));
   }

   TrustedListenableFutureTask(Callable<V> var1) {
      super();
      this.task = new TrustedListenableFutureTask.TrustedFutureInterruptibleTask(var1);
   }

   public void run() {
      TrustedListenableFutureTask.TrustedFutureInterruptibleTask var1 = this.task;
      if (var1 != null) {
         var1.run();
      }

   }

   protected void afterDone() {
      super.afterDone();
      if (this.wasInterrupted()) {
         TrustedListenableFutureTask.TrustedFutureInterruptibleTask var1 = this.task;
         if (var1 != null) {
            var1.interruptTask();
         }
      }

      this.task = null;
   }

   public String toString() {
      return super.toString() + " (delegate = " + this.task + ")";
   }

   private final class TrustedFutureInterruptibleTask extends InterruptibleTask {
      private final Callable<V> callable;

      TrustedFutureInterruptibleTask(Callable<V> var2) {
         super();
         this.callable = (Callable)Preconditions.checkNotNull(var2);
      }

      void runInterruptibly() {
         if (!TrustedListenableFutureTask.this.isDone()) {
            try {
               TrustedListenableFutureTask.this.set(this.callable.call());
            } catch (Throwable var2) {
               TrustedListenableFutureTask.this.setException(var2);
            }
         }

      }

      boolean wasInterrupted() {
         return TrustedListenableFutureTask.this.wasInterrupted();
      }

      public String toString() {
         return this.callable.toString();
      }
   }
}
