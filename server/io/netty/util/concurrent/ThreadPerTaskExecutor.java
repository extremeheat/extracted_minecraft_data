package io.netty.util.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public final class ThreadPerTaskExecutor implements Executor {
   private final ThreadFactory threadFactory;

   public ThreadPerTaskExecutor(ThreadFactory var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("threadFactory");
      } else {
         this.threadFactory = var1;
      }
   }

   public void execute(Runnable var1) {
      this.threadFactory.newThread(var1).start();
   }
}
