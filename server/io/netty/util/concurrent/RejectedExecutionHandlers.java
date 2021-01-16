package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public final class RejectedExecutionHandlers {
   private static final RejectedExecutionHandler REJECT = new RejectedExecutionHandler() {
      public void rejected(Runnable var1, SingleThreadEventExecutor var2) {
         throw new RejectedExecutionException();
      }
   };

   private RejectedExecutionHandlers() {
      super();
   }

   public static RejectedExecutionHandler reject() {
      return REJECT;
   }

   public static RejectedExecutionHandler backoff(final int var0, long var1, TimeUnit var3) {
      ObjectUtil.checkPositive(var0, "retries");
      final long var4 = var3.toNanos(var1);
      return new RejectedExecutionHandler() {
         public void rejected(Runnable var1, SingleThreadEventExecutor var2) {
            if (!var2.inEventLoop()) {
               for(int var3 = 0; var3 < var0; ++var3) {
                  var2.wakeup(false);
                  LockSupport.parkNanos(var4);
                  if (var2.offerTask(var1)) {
                     return;
                  }
               }
            }

            throw new RejectedExecutionException();
         }
      };
   }
}
