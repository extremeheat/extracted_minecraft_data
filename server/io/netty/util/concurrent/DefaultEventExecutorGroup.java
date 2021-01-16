package io.netty.util.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class DefaultEventExecutorGroup extends MultithreadEventExecutorGroup {
   public DefaultEventExecutorGroup(int var1) {
      this(var1, (ThreadFactory)null);
   }

   public DefaultEventExecutorGroup(int var1, ThreadFactory var2) {
      this(var1, var2, SingleThreadEventExecutor.DEFAULT_MAX_PENDING_EXECUTOR_TASKS, RejectedExecutionHandlers.reject());
   }

   public DefaultEventExecutorGroup(int var1, ThreadFactory var2, int var3, RejectedExecutionHandler var4) {
      super(var1, var2, var3, var4);
   }

   protected EventExecutor newChild(Executor var1, Object... var2) throws Exception {
      return new DefaultEventExecutor(this, var1, (Integer)var2[0], (RejectedExecutionHandler)var2[1]);
   }
}
