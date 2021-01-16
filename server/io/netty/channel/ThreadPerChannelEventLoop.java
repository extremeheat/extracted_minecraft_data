package io.netty.channel;

import java.util.concurrent.Executor;

public class ThreadPerChannelEventLoop extends SingleThreadEventLoop {
   private final ThreadPerChannelEventLoopGroup parent;
   private Channel ch;

   public ThreadPerChannelEventLoop(ThreadPerChannelEventLoopGroup var1) {
      super(var1, (Executor)var1.executor, true);
      this.parent = var1;
   }

   public ChannelFuture register(ChannelPromise var1) {
      return super.register(var1).addListener(new ChannelFutureListener() {
         public void operationComplete(ChannelFuture var1) throws Exception {
            if (var1.isSuccess()) {
               ThreadPerChannelEventLoop.this.ch = var1.channel();
            } else {
               ThreadPerChannelEventLoop.this.deregister();
            }

         }
      });
   }

   /** @deprecated */
   @Deprecated
   public ChannelFuture register(Channel var1, ChannelPromise var2) {
      return super.register(var1, var2).addListener(new ChannelFutureListener() {
         public void operationComplete(ChannelFuture var1) throws Exception {
            if (var1.isSuccess()) {
               ThreadPerChannelEventLoop.this.ch = var1.channel();
            } else {
               ThreadPerChannelEventLoop.this.deregister();
            }

         }
      });
   }

   protected void run() {
      while(true) {
         Runnable var1 = this.takeTask();
         if (var1 != null) {
            var1.run();
            this.updateLastExecutionTime();
         }

         Channel var2 = this.ch;
         if (this.isShuttingDown()) {
            if (var2 != null) {
               var2.unsafe().close(var2.unsafe().voidPromise());
            }

            if (this.confirmShutdown()) {
               return;
            }
         } else if (var2 != null && !var2.isRegistered()) {
            this.runAllTasks();
            this.deregister();
         }
      }
   }

   protected void deregister() {
      this.ch = null;
      this.parent.activeChildren.remove(this);
      this.parent.idleChildren.add(this);
   }
}
