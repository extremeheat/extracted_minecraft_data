package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelPromiseNotifier;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.concurrent.EventExecutor;
import java.util.concurrent.TimeUnit;

public class Bzip2Encoder extends MessageToByteEncoder<ByteBuf> {
   private Bzip2Encoder.State currentState;
   private final Bzip2BitWriter writer;
   private final int streamBlockSize;
   private int streamCRC;
   private Bzip2BlockCompressor blockCompressor;
   private volatile boolean finished;
   private volatile ChannelHandlerContext ctx;

   public Bzip2Encoder() {
      this(9);
   }

   public Bzip2Encoder(int var1) {
      super();
      this.currentState = Bzip2Encoder.State.INIT;
      this.writer = new Bzip2BitWriter();
      if (var1 >= 1 && var1 <= 9) {
         this.streamBlockSize = var1 * 100000;
      } else {
         throw new IllegalArgumentException("blockSizeMultiplier: " + var1 + " (expected: 1-9)");
      }
   }

   protected void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) throws Exception {
      if (this.finished) {
         var3.writeBytes(var2);
      } else {
         while(true) {
            switch(this.currentState) {
            case INIT:
               var3.ensureWritable(4);
               var3.writeMedium(4348520);
               var3.writeByte(48 + this.streamBlockSize / 100000);
               this.currentState = Bzip2Encoder.State.INIT_BLOCK;
            case INIT_BLOCK:
               this.blockCompressor = new Bzip2BlockCompressor(this.writer, this.streamBlockSize);
               this.currentState = Bzip2Encoder.State.WRITE_DATA;
            case WRITE_DATA:
               if (!var2.isReadable()) {
                  return;
               }

               Bzip2BlockCompressor var4 = this.blockCompressor;
               int var5 = Math.min(var2.readableBytes(), var4.availableSize());
               int var6 = var4.write(var2, var2.readerIndex(), var5);
               var2.skipBytes(var6);
               if (!var4.isFull()) {
                  if (!var2.isReadable()) {
                     return;
                  }
                  break;
               } else {
                  this.currentState = Bzip2Encoder.State.CLOSE_BLOCK;
               }
            case CLOSE_BLOCK:
               this.closeBlock(var3);
               this.currentState = Bzip2Encoder.State.INIT_BLOCK;
               break;
            default:
               throw new IllegalStateException();
            }
         }
      }
   }

   private void closeBlock(ByteBuf var1) {
      Bzip2BlockCompressor var2 = this.blockCompressor;
      if (!var2.isEmpty()) {
         var2.close(var1);
         int var3 = var2.crc();
         this.streamCRC = (this.streamCRC << 1 | this.streamCRC >>> 31) ^ var3;
      }

   }

   public boolean isClosed() {
      return this.finished;
   }

   public ChannelFuture close() {
      return this.close(this.ctx().newPromise());
   }

   public ChannelFuture close(final ChannelPromise var1) {
      ChannelHandlerContext var2 = this.ctx();
      EventExecutor var3 = var2.executor();
      if (var3.inEventLoop()) {
         return this.finishEncode(var2, var1);
      } else {
         var3.execute(new Runnable() {
            public void run() {
               ChannelFuture var1x = Bzip2Encoder.this.finishEncode(Bzip2Encoder.this.ctx(), var1);
               var1x.addListener(new ChannelPromiseNotifier(new ChannelPromise[]{var1}));
            }
         });
         return var1;
      }
   }

   public void close(final ChannelHandlerContext var1, final ChannelPromise var2) throws Exception {
      ChannelFuture var3 = this.finishEncode(var1, var1.newPromise());
      var3.addListener(new ChannelFutureListener() {
         public void operationComplete(ChannelFuture var1x) throws Exception {
            var1.close(var2);
         }
      });
      if (!var3.isDone()) {
         var1.executor().schedule(new Runnable() {
            public void run() {
               var1.close(var2);
            }
         }, 10L, TimeUnit.SECONDS);
      }

   }

   private ChannelFuture finishEncode(ChannelHandlerContext var1, ChannelPromise var2) {
      if (this.finished) {
         var2.setSuccess();
         return var2;
      } else {
         this.finished = true;
         ByteBuf var3 = var1.alloc().buffer();
         this.closeBlock(var3);
         int var4 = this.streamCRC;
         Bzip2BitWriter var5 = this.writer;

         try {
            var5.writeBits(var3, 24, 1536581L);
            var5.writeBits(var3, 24, 3690640L);
            var5.writeInt(var3, var4);
            var5.flush(var3);
         } finally {
            this.blockCompressor = null;
         }

         return var1.writeAndFlush(var3, var2);
      }
   }

   private ChannelHandlerContext ctx() {
      ChannelHandlerContext var1 = this.ctx;
      if (var1 == null) {
         throw new IllegalStateException("not added to a pipeline");
      } else {
         return var1;
      }
   }

   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.ctx = var1;
   }

   private static enum State {
      INIT,
      INIT_BLOCK,
      WRITE_DATA,
      CLOSE_BLOCK;

      private State() {
      }
   }
}
