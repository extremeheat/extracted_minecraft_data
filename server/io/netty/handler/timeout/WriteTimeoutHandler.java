package io.netty.handler.timeout;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class WriteTimeoutHandler extends ChannelOutboundHandlerAdapter {
   private static final long MIN_TIMEOUT_NANOS;
   private final long timeoutNanos;
   private WriteTimeoutHandler.WriteTimeoutTask lastTask;
   private boolean closed;

   public WriteTimeoutHandler(int var1) {
      this((long)var1, TimeUnit.SECONDS);
   }

   public WriteTimeoutHandler(long var1, TimeUnit var3) {
      super();
      if (var3 == null) {
         throw new NullPointerException("unit");
      } else {
         if (var1 <= 0L) {
            this.timeoutNanos = 0L;
         } else {
            this.timeoutNanos = Math.max(var3.toNanos(var1), MIN_TIMEOUT_NANOS);
         }

      }
   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      if (this.timeoutNanos > 0L) {
         var3 = var3.unvoid();
         this.scheduleTimeout(var1, var3);
      }

      var1.write(var2, var3);
   }

   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      WriteTimeoutHandler.WriteTimeoutTask var2 = this.lastTask;

      WriteTimeoutHandler.WriteTimeoutTask var3;
      for(this.lastTask = null; var2 != null; var2 = var3) {
         var2.scheduledFuture.cancel(false);
         var3 = var2.prev;
         var2.prev = null;
         var2.next = null;
      }

   }

   private void scheduleTimeout(ChannelHandlerContext var1, ChannelPromise var2) {
      WriteTimeoutHandler.WriteTimeoutTask var3 = new WriteTimeoutHandler.WriteTimeoutTask(var1, var2);
      var3.scheduledFuture = var1.executor().schedule(var3, this.timeoutNanos, TimeUnit.NANOSECONDS);
      if (!var3.scheduledFuture.isDone()) {
         this.addWriteTimeoutTask(var3);
         var2.addListener(var3);
      }

   }

   private void addWriteTimeoutTask(WriteTimeoutHandler.WriteTimeoutTask var1) {
      if (this.lastTask != null) {
         this.lastTask.next = var1;
         var1.prev = this.lastTask;
      }

      this.lastTask = var1;
   }

   private void removeWriteTimeoutTask(WriteTimeoutHandler.WriteTimeoutTask var1) {
      if (var1 == this.lastTask) {
         assert var1.next == null;

         this.lastTask = this.lastTask.prev;
         if (this.lastTask != null) {
            this.lastTask.next = null;
         }
      } else {
         if (var1.prev == null && var1.next == null) {
            return;
         }

         if (var1.prev == null) {
            var1.next.prev = null;
         } else {
            var1.prev.next = var1.next;
            var1.next.prev = var1.prev;
         }
      }

      var1.prev = null;
      var1.next = null;
   }

   protected void writeTimedOut(ChannelHandlerContext var1) throws Exception {
      if (!this.closed) {
         var1.fireExceptionCaught(WriteTimeoutException.INSTANCE);
         var1.close();
         this.closed = true;
      }

   }

   static {
      MIN_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(1L);
   }

   private final class WriteTimeoutTask implements Runnable, ChannelFutureListener {
      private final ChannelHandlerContext ctx;
      private final ChannelPromise promise;
      WriteTimeoutHandler.WriteTimeoutTask prev;
      WriteTimeoutHandler.WriteTimeoutTask next;
      ScheduledFuture<?> scheduledFuture;

      WriteTimeoutTask(ChannelHandlerContext var2, ChannelPromise var3) {
         super();
         this.ctx = var2;
         this.promise = var3;
      }

      public void run() {
         if (!this.promise.isDone()) {
            try {
               WriteTimeoutHandler.this.writeTimedOut(this.ctx);
            } catch (Throwable var2) {
               this.ctx.fireExceptionCaught(var2);
            }
         }

         WriteTimeoutHandler.this.removeWriteTimeoutTask(this);
      }

      public void operationComplete(ChannelFuture var1) throws Exception {
         this.scheduledFuture.cancel(false);
         WriteTimeoutHandler.this.removeWriteTimeoutTask(this);
      }
   }
}
