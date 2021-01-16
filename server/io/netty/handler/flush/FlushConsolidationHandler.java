package io.netty.handler.flush;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.util.concurrent.Future;

public class FlushConsolidationHandler extends ChannelDuplexHandler {
   private final int explicitFlushAfterFlushes;
   private final boolean consolidateWhenNoReadInProgress;
   private final Runnable flushTask;
   private int flushPendingCount;
   private boolean readInProgress;
   private ChannelHandlerContext ctx;
   private Future<?> nextScheduledFlush;

   public FlushConsolidationHandler() {
      this(256, false);
   }

   public FlushConsolidationHandler(int var1) {
      this(var1, false);
   }

   public FlushConsolidationHandler(int var1, boolean var2) {
      super();
      if (var1 <= 0) {
         throw new IllegalArgumentException("explicitFlushAfterFlushes: " + var1 + " (expected: > 0)");
      } else {
         this.explicitFlushAfterFlushes = var1;
         this.consolidateWhenNoReadInProgress = var2;
         this.flushTask = var2 ? new Runnable() {
            public void run() {
               if (FlushConsolidationHandler.this.flushPendingCount > 0 && !FlushConsolidationHandler.this.readInProgress) {
                  FlushConsolidationHandler.this.flushPendingCount = 0;
                  FlushConsolidationHandler.this.ctx.flush();
                  FlushConsolidationHandler.this.nextScheduledFlush = null;
               }

            }
         } : null;
      }
   }

   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.ctx = var1;
   }

   public void flush(ChannelHandlerContext var1) throws Exception {
      if (this.readInProgress) {
         if (++this.flushPendingCount == this.explicitFlushAfterFlushes) {
            this.flushNow(var1);
         }
      } else if (this.consolidateWhenNoReadInProgress) {
         if (++this.flushPendingCount == this.explicitFlushAfterFlushes) {
            this.flushNow(var1);
         } else {
            this.scheduleFlush(var1);
         }
      } else {
         this.flushNow(var1);
      }

   }

   public void channelReadComplete(ChannelHandlerContext var1) throws Exception {
      this.resetReadAndFlushIfNeeded(var1);
      var1.fireChannelReadComplete();
   }

   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      this.readInProgress = true;
      var1.fireChannelRead(var2);
   }

   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      this.resetReadAndFlushIfNeeded(var1);
      var1.fireExceptionCaught(var2);
   }

   public void disconnect(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      this.resetReadAndFlushIfNeeded(var1);
      var1.disconnect(var2);
   }

   public void close(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      this.resetReadAndFlushIfNeeded(var1);
      var1.close(var2);
   }

   public void channelWritabilityChanged(ChannelHandlerContext var1) throws Exception {
      if (!var1.channel().isWritable()) {
         this.flushIfNeeded(var1);
      }

      var1.fireChannelWritabilityChanged();
   }

   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      this.flushIfNeeded(var1);
   }

   private void resetReadAndFlushIfNeeded(ChannelHandlerContext var1) {
      this.readInProgress = false;
      this.flushIfNeeded(var1);
   }

   private void flushIfNeeded(ChannelHandlerContext var1) {
      if (this.flushPendingCount > 0) {
         this.flushNow(var1);
      }

   }

   private void flushNow(ChannelHandlerContext var1) {
      this.cancelScheduledFlush();
      this.flushPendingCount = 0;
      var1.flush();
   }

   private void scheduleFlush(ChannelHandlerContext var1) {
      if (this.nextScheduledFlush == null) {
         this.nextScheduledFlush = var1.channel().eventLoop().submit(this.flushTask);
      }

   }

   private void cancelScheduledFlush() {
      if (this.nextScheduledFlush != null) {
         this.nextScheduledFlush.cancel(false);
         this.nextScheduledFlush = null;
      }

   }
}
