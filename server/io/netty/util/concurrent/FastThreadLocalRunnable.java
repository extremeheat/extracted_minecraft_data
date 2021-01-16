package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;

final class FastThreadLocalRunnable implements Runnable {
   private final Runnable runnable;

   private FastThreadLocalRunnable(Runnable var1) {
      super();
      this.runnable = (Runnable)ObjectUtil.checkNotNull(var1, "runnable");
   }

   public void run() {
      try {
         this.runnable.run();
      } finally {
         FastThreadLocal.removeAll();
      }

   }

   static Runnable wrap(Runnable var0) {
      return (Runnable)(var0 instanceof FastThreadLocalRunnable ? var0 : new FastThreadLocalRunnable(var0));
   }
}
