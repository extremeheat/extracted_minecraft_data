package io.netty.channel.embedded;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.AbstractScheduledEventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

final class EmbeddedEventLoop extends AbstractScheduledEventExecutor implements EventLoop {
   private final Queue<Runnable> tasks = new ArrayDeque(2);

   EmbeddedEventLoop() {
      super();
   }

   public EventLoopGroup parent() {
      return (EventLoopGroup)super.parent();
   }

   public EventLoop next() {
      return (EventLoop)super.next();
   }

   public void execute(Runnable var1) {
      if (var1 == null) {
         throw new NullPointerException("command");
      } else {
         this.tasks.add(var1);
      }
   }

   void runTasks() {
      while(true) {
         Runnable var1 = (Runnable)this.tasks.poll();
         if (var1 == null) {
            return;
         }

         var1.run();
      }
   }

   long runScheduledTasks() {
      long var1 = AbstractScheduledEventExecutor.nanoTime();

      while(true) {
         Runnable var3 = this.pollScheduledTask(var1);
         if (var3 == null) {
            return this.nextScheduledTaskNano();
         }

         var3.run();
      }
   }

   long nextScheduledTask() {
      return this.nextScheduledTaskNano();
   }

   protected void cancelScheduledTasks() {
      super.cancelScheduledTasks();
   }

   public Future<?> shutdownGracefully(long var1, long var3, TimeUnit var5) {
      throw new UnsupportedOperationException();
   }

   public Future<?> terminationFuture() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public void shutdown() {
      throw new UnsupportedOperationException();
   }

   public boolean isShuttingDown() {
      return false;
   }

   public boolean isShutdown() {
      return false;
   }

   public boolean isTerminated() {
      return false;
   }

   public boolean awaitTermination(long var1, TimeUnit var3) {
      return false;
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
      var1.unsafe().register(this, var2);
      return var2;
   }

   public boolean inEventLoop() {
      return true;
   }

   public boolean inEventLoop(Thread var1) {
      return true;
   }
}
