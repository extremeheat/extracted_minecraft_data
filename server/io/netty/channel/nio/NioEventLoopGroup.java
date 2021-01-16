package io.netty.channel.nio;

import io.netty.channel.DefaultSelectStrategyFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.SelectStrategyFactory;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class NioEventLoopGroup extends MultithreadEventLoopGroup {
   public NioEventLoopGroup() {
      this(0);
   }

   public NioEventLoopGroup(int var1) {
      this(var1, (Executor)null);
   }

   public NioEventLoopGroup(int var1, ThreadFactory var2) {
      this(var1, var2, SelectorProvider.provider());
   }

   public NioEventLoopGroup(int var1, Executor var2) {
      this(var1, var2, SelectorProvider.provider());
   }

   public NioEventLoopGroup(int var1, ThreadFactory var2, SelectorProvider var3) {
      this(var1, var2, var3, DefaultSelectStrategyFactory.INSTANCE);
   }

   public NioEventLoopGroup(int var1, ThreadFactory var2, SelectorProvider var3, SelectStrategyFactory var4) {
      super(var1, var2, var3, var4, RejectedExecutionHandlers.reject());
   }

   public NioEventLoopGroup(int var1, Executor var2, SelectorProvider var3) {
      this(var1, var2, var3, DefaultSelectStrategyFactory.INSTANCE);
   }

   public NioEventLoopGroup(int var1, Executor var2, SelectorProvider var3, SelectStrategyFactory var4) {
      super(var1, var2, var3, var4, RejectedExecutionHandlers.reject());
   }

   public NioEventLoopGroup(int var1, Executor var2, EventExecutorChooserFactory var3, SelectorProvider var4, SelectStrategyFactory var5) {
      super(var1, var2, var3, var4, var5, RejectedExecutionHandlers.reject());
   }

   public NioEventLoopGroup(int var1, Executor var2, EventExecutorChooserFactory var3, SelectorProvider var4, SelectStrategyFactory var5, RejectedExecutionHandler var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public void setIoRatio(int var1) {
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         EventExecutor var3 = (EventExecutor)var2.next();
         ((NioEventLoop)var3).setIoRatio(var1);
      }

   }

   public void rebuildSelectors() {
      Iterator var1 = this.iterator();

      while(var1.hasNext()) {
         EventExecutor var2 = (EventExecutor)var1.next();
         ((NioEventLoop)var2).rebuildSelector();
      }

   }

   protected EventLoop newChild(Executor var1, Object... var2) throws Exception {
      return new NioEventLoop(this, var1, (SelectorProvider)var2[0], ((SelectStrategyFactory)var2[1]).newSelectStrategy(), (RejectedExecutionHandler)var2[2]);
   }
}
