package io.netty.handler.stream;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;

public class ChunkedWriteHandler extends ChannelDuplexHandler {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChunkedWriteHandler.class);
   private final Queue<ChunkedWriteHandler.PendingWrite> queue = new ArrayDeque();
   private volatile ChannelHandlerContext ctx;
   private ChunkedWriteHandler.PendingWrite currentWrite;

   public ChunkedWriteHandler() {
      super();
   }

   /** @deprecated */
   @Deprecated
   public ChunkedWriteHandler(int var1) {
      super();
      if (var1 <= 0) {
         throw new IllegalArgumentException("maxPendingWrites: " + var1 + " (expected: > 0)");
      }
   }

   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.ctx = var1;
   }

   public void resumeTransfer() {
      final ChannelHandlerContext var1 = this.ctx;
      if (var1 != null) {
         if (var1.executor().inEventLoop()) {
            this.resumeTransfer0(var1);
         } else {
            var1.executor().execute(new Runnable() {
               public void run() {
                  ChunkedWriteHandler.this.resumeTransfer0(var1);
               }
            });
         }

      }
   }

   private void resumeTransfer0(ChannelHandlerContext var1) {
      try {
         this.doFlush(var1);
      } catch (Exception var3) {
         if (logger.isWarnEnabled()) {
            logger.warn("Unexpected exception while sending chunks.", (Throwable)var3);
         }
      }

   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      this.queue.add(new ChunkedWriteHandler.PendingWrite(var2, var3));
   }

   public void flush(ChannelHandlerContext var1) throws Exception {
      this.doFlush(var1);
   }

   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      this.doFlush(var1);
      var1.fireChannelInactive();
   }

   public void channelWritabilityChanged(ChannelHandlerContext var1) throws Exception {
      if (var1.channel().isWritable()) {
         this.doFlush(var1);
      }

      var1.fireChannelWritabilityChanged();
   }

   private void discard(Throwable var1) {
      while(true) {
         ChunkedWriteHandler.PendingWrite var2 = this.currentWrite;
         if (this.currentWrite == null) {
            var2 = (ChunkedWriteHandler.PendingWrite)this.queue.poll();
         } else {
            this.currentWrite = null;
         }

         if (var2 == null) {
            return;
         }

         Object var3 = var2.msg;
         if (var3 instanceof ChunkedInput) {
            ChunkedInput var4 = (ChunkedInput)var3;

            try {
               if (!var4.isEndOfInput()) {
                  if (var1 == null) {
                     var1 = new ClosedChannelException();
                  }

                  var2.fail((Throwable)var1);
               } else {
                  var2.success(var4.length());
               }

               closeInput(var4);
            } catch (Exception var6) {
               var2.fail(var6);
               logger.warn(ChunkedInput.class.getSimpleName() + ".isEndOfInput() failed", (Throwable)var6);
               closeInput(var4);
            }
         } else {
            if (var1 == null) {
               var1 = new ClosedChannelException();
            }

            var2.fail((Throwable)var1);
         }
      }
   }

   private void doFlush(ChannelHandlerContext var1) {
      final Channel var2 = var1.channel();
      if (!var2.isActive()) {
         this.discard((Throwable)null);
      } else {
         boolean var3 = true;
         ByteBufAllocator var4 = var1.alloc();

         while(var2.isWritable()) {
            if (this.currentWrite == null) {
               this.currentWrite = (ChunkedWriteHandler.PendingWrite)this.queue.poll();
            }

            if (this.currentWrite == null) {
               break;
            }

            final ChunkedWriteHandler.PendingWrite var5 = this.currentWrite;
            final Object var6 = var5.msg;
            if (var6 instanceof ChunkedInput) {
               final ChunkedInput var7 = (ChunkedInput)var6;
               Object var10 = null;

               boolean var8;
               boolean var9;
               try {
                  var10 = var7.readChunk(var4);
                  var8 = var7.isEndOfInput();
                  if (var10 == null) {
                     var9 = !var8;
                  } else {
                     var9 = false;
                  }
               } catch (Throwable var12) {
                  this.currentWrite = null;
                  if (var10 != null) {
                     ReferenceCountUtil.release(var10);
                  }

                  var5.fail(var12);
                  closeInput(var7);
                  break;
               }

               if (var9) {
                  break;
               }

               if (var10 == null) {
                  var10 = Unpooled.EMPTY_BUFFER;
               }

               ChannelFuture var11 = var1.write(var10);
               if (var8) {
                  this.currentWrite = null;
                  var11.addListener(new ChannelFutureListener() {
                     public void operationComplete(ChannelFuture var1) throws Exception {
                        var5.progress(var7.progress(), var7.length());
                        var5.success(var7.length());
                        ChunkedWriteHandler.closeInput(var7);
                     }
                  });
               } else if (var2.isWritable()) {
                  var11.addListener(new ChannelFutureListener() {
                     public void operationComplete(ChannelFuture var1) throws Exception {
                        if (!var1.isSuccess()) {
                           ChunkedWriteHandler.closeInput((ChunkedInput)var6);
                           var5.fail(var1.cause());
                        } else {
                           var5.progress(var7.progress(), var7.length());
                        }

                     }
                  });
               } else {
                  var11.addListener(new ChannelFutureListener() {
                     public void operationComplete(ChannelFuture var1) throws Exception {
                        if (!var1.isSuccess()) {
                           ChunkedWriteHandler.closeInput((ChunkedInput)var6);
                           var5.fail(var1.cause());
                        } else {
                           var5.progress(var7.progress(), var7.length());
                           if (var2.isWritable()) {
                              ChunkedWriteHandler.this.resumeTransfer();
                           }
                        }

                     }
                  });
               }

               var1.flush();
               var3 = false;
            } else {
               this.currentWrite = null;
               var1.write(var6, var5.promise);
               var3 = true;
            }

            if (!var2.isActive()) {
               this.discard(new ClosedChannelException());
               break;
            }
         }

         if (var3) {
            var1.flush();
         }

      }
   }

   private static void closeInput(ChunkedInput<?> var0) {
      try {
         var0.close();
      } catch (Throwable var2) {
         if (logger.isWarnEnabled()) {
            logger.warn("Failed to close a chunked input.", var2);
         }
      }

   }

   private static final class PendingWrite {
      final Object msg;
      final ChannelPromise promise;

      PendingWrite(Object var1, ChannelPromise var2) {
         super();
         this.msg = var1;
         this.promise = var2;
      }

      void fail(Throwable var1) {
         ReferenceCountUtil.release(this.msg);
         this.promise.tryFailure(var1);
      }

      void success(long var1) {
         if (!this.promise.isDone()) {
            this.progress(var1, var1);
            this.promise.trySuccess();
         }
      }

      void progress(long var1, long var3) {
         if (this.promise instanceof ChannelProgressivePromise) {
            ((ChannelProgressivePromise)this.promise).tryProgress(var1, var3);
         }

      }
   }
}
