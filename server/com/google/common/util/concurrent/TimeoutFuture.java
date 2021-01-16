package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nullable;

@GwtIncompatible
final class TimeoutFuture<V> extends AbstractFuture.TrustedFuture<V> {
   @Nullable
   private ListenableFuture<V> delegateRef;
   @Nullable
   private Future<?> timer;

   static <V> ListenableFuture<V> create(ListenableFuture<V> var0, long var1, TimeUnit var3, ScheduledExecutorService var4) {
      TimeoutFuture var5 = new TimeoutFuture(var0);
      TimeoutFuture.Fire var6 = new TimeoutFuture.Fire(var5);
      var5.timer = var4.schedule(var6, var1, var3);
      var0.addListener(var6, MoreExecutors.directExecutor());
      return var5;
   }

   private TimeoutFuture(ListenableFuture<V> var1) {
      super();
      this.delegateRef = (ListenableFuture)Preconditions.checkNotNull(var1);
   }

   protected void afterDone() {
      this.maybePropagateCancellation(this.delegateRef);
      Future var1 = this.timer;
      if (var1 != null) {
         var1.cancel(false);
      }

      this.delegateRef = null;
      this.timer = null;
   }

   private static final class Fire<V> implements Runnable {
      @Nullable
      TimeoutFuture<V> timeoutFutureRef;

      Fire(TimeoutFuture<V> var1) {
         super();
         this.timeoutFutureRef = var1;
      }

      public void run() {
         TimeoutFuture var1 = this.timeoutFutureRef;
         if (var1 != null) {
            ListenableFuture var2 = var1.delegateRef;
            if (var2 != null) {
               this.timeoutFutureRef = null;
               if (var2.isDone()) {
                  var1.setFuture(var2);
               } else {
                  try {
                     var1.setException(new TimeoutException("Future timed out: " + var2));
                  } finally {
                     var2.cancel(true);
                  }
               }

            }
         }
      }
   }
}
