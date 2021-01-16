package io.netty.channel.kqueue;

import io.netty.channel.DefaultSelectStrategyFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.SelectStrategyFactory;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public final class KQueueEventLoopGroup extends MultithreadEventLoopGroup {
   public KQueueEventLoopGroup() {
      this(0);
   }

   public KQueueEventLoopGroup(int var1) {
      this(var1, (ThreadFactory)null);
   }

   public KQueueEventLoopGroup(int var1, SelectStrategyFactory var2) {
      this(var1, (ThreadFactory)null, var2);
   }

   public KQueueEventLoopGroup(int var1, ThreadFactory var2) {
      this(var1, var2, 0);
   }

   public KQueueEventLoopGroup(int var1, Executor var2) {
      this(var1, var2, DefaultSelectStrategyFactory.INSTANCE);
   }

   public KQueueEventLoopGroup(int var1, ThreadFactory var2, SelectStrategyFactory var3) {
      this(var1, var2, 0, var3);
   }

   /** @deprecated */
   @Deprecated
   public KQueueEventLoopGroup(int var1, ThreadFactory var2, int var3) {
      this(var1, var2, var3, DefaultSelectStrategyFactory.INSTANCE);
   }

   /** @deprecated */
   @Deprecated
   public KQueueEventLoopGroup(int var1, ThreadFactory var2, int var3, SelectStrategyFactory var4) {
      super(var1, var2, var3, var4, RejectedExecutionHandlers.reject());
      KQueue.ensureAvailability();
   }

   public KQueueEventLoopGroup(int var1, Executor var2, SelectStrategyFactory var3) {
      super(var1, var2, 0, var3, RejectedExecutionHandlers.reject());
      KQueue.ensureAvailability();
   }

   public KQueueEventLoopGroup(int var1, Executor var2, EventExecutorChooserFactory var3, SelectStrategyFactory var4) {
      super(var1, var2, var3, 0, var4, RejectedExecutionHandlers.reject());
      KQueue.ensureAvailability();
   }

   public KQueueEventLoopGroup(int var1, Executor var2, EventExecutorChooserFactory var3, SelectStrategyFactory var4, RejectedExecutionHandler var5) {
      super(var1, var2, var3, 0, var4, var5);
      KQueue.ensureAvailability();
   }

   public void setIoRatio(int var1) {
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         EventExecutor var3 = (EventExecutor)var2.next();
         ((KQueueEventLoop)var3).setIoRatio(var1);
      }

   }

   protected EventLoop newChild(Executor var1, Object... var2) throws Exception {
      return new KQueueEventLoop(this, var1, (Integer)var2[0], ((SelectStrategyFactory)var2[1]).newSelectStrategy(), (RejectedExecutionHandler)var2[2]);
   }
}
