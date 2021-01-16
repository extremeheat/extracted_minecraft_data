package io.netty.channel;

import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SystemPropertyUtil;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public abstract class SingleThreadEventLoop extends SingleThreadEventExecutor implements EventLoop {
   protected static final int DEFAULT_MAX_PENDING_TASKS = Math.max(16, SystemPropertyUtil.getInt("io.netty.eventLoop.maxPendingTasks", 2147483647));
   private final Queue<Runnable> tailTasks;

   protected SingleThreadEventLoop(EventLoopGroup var1, ThreadFactory var2, boolean var3) {
      this(var1, var2, var3, DEFAULT_MAX_PENDING_TASKS, RejectedExecutionHandlers.reject());
   }

   protected SingleThreadEventLoop(EventLoopGroup var1, Executor var2, boolean var3) {
      this(var1, var2, var3, DEFAULT_MAX_PENDING_TASKS, RejectedExecutionHandlers.reject());
   }

   protected SingleThreadEventLoop(EventLoopGroup var1, ThreadFactory var2, boolean var3, int var4, RejectedExecutionHandler var5) {
      super(var1, (ThreadFactory)var2, var3, var4, var5);
      this.tailTasks = this.newTaskQueue(var4);
   }

   protected SingleThreadEventLoop(EventLoopGroup var1, Executor var2, boolean var3, int var4, RejectedExecutionHandler var5) {
      super(var1, (Executor)var2, var3, var4, var5);
      this.tailTasks = this.newTaskQueue(var4);
   }

   public EventLoopGroup parent() {
      return (EventLoopGroup)super.parent();
   }

   public EventLoop next() {
      return (EventLoop)super.next();
   }

   public ChannelFuture register(Channel var1) {
      return this.register((ChannelPromise)(new DefaultChannelPromise(var1, this)));
   }

   public ChannelFuture register(ChannelPromise var1) {
      ObjectUtil.checkNotNull(var1, "promise");
      var1.channel().unsafe().register(this, var1);
      return var1;
   }

   /** @deprecated */
   @Deprecated
   public ChannelFuture register(Channel var1, ChannelPromise var2) {
      if (var1 == null) {
         throw new NullPointerException("channel");
      } else if (var2 == null) {
         throw new NullPointerException("promise");
      } else {
         var1.unsafe().register(this, var2);
         return var2;
      }
   }

   public final void executeAfterEventLoopIteration(Runnable var1) {
      ObjectUtil.checkNotNull(var1, "task");
      if (this.isShutdown()) {
         reject();
      }

      if (!this.tailTasks.offer(var1)) {
         this.reject(var1);
      }

      if (this.wakesUpForTask(var1)) {
         this.wakeup(this.inEventLoop());
      }

   }

   final boolean removeAfterEventLoopIterationTask(Runnable var1) {
      return this.tailTasks.remove(ObjectUtil.checkNotNull(var1, "task"));
   }

   protected boolean wakesUpForTask(Runnable var1) {
      return !(var1 instanceof SingleThreadEventLoop.NonWakeupRunnable);
   }

   protected void afterRunningAllTasks() {
      this.runAllTasksFrom(this.tailTasks);
   }

   protected boolean hasTasks() {
      return super.hasTasks() || !this.tailTasks.isEmpty();
   }

   public int pendingTasks() {
      return super.pendingTasks() + this.tailTasks.size();
   }

   interface NonWakeupRunnable extends Runnable {
   }
}
