package io.netty.util.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public final class DefaultEventExecutor extends SingleThreadEventExecutor {
   public DefaultEventExecutor() {
      this((EventExecutorGroup)null);
   }

   public DefaultEventExecutor(ThreadFactory var1) {
      this((EventExecutorGroup)null, (ThreadFactory)var1);
   }

   public DefaultEventExecutor(Executor var1) {
      this((EventExecutorGroup)null, (Executor)var1);
   }

   public DefaultEventExecutor(EventExecutorGroup var1) {
      this(var1, (ThreadFactory)(new DefaultThreadFactory(DefaultEventExecutor.class)));
   }

   public DefaultEventExecutor(EventExecutorGroup var1, ThreadFactory var2) {
      super(var1, var2, true);
   }

   public DefaultEventExecutor(EventExecutorGroup var1, Executor var2) {
      super(var1, var2, true);
   }

   public DefaultEventExecutor(EventExecutorGroup var1, ThreadFactory var2, int var3, RejectedExecutionHandler var4) {
      super(var1, var2, true, var3, var4);
   }

   public DefaultEventExecutor(EventExecutorGroup var1, Executor var2, int var3, RejectedExecutionHandler var4) {
      super(var1, var2, true, var3, var4);
   }

   protected void run() {
      do {
         Runnable var1 = this.takeTask();
         if (var1 != null) {
            var1.run();
            this.updateLastExecutionTime();
         }
      } while(!this.confirmShutdown());

   }
}
