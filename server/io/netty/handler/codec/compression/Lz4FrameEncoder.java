package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelPromiseNotifier;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.ThrowableUtil;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.zip.Checksum;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Exception;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.xxhash.XXHashFactory;

public class Lz4FrameEncoder extends MessageToByteEncoder<ByteBuf> {
   private static final EncoderException ENCODE_FINSHED_EXCEPTION = (EncoderException)ThrowableUtil.unknownStackTrace(new EncoderException(new IllegalStateException("encode finished and not enough space to write remaining data")), Lz4FrameEncoder.class, "encode");
   static final int DEFAULT_MAX_ENCODE_SIZE = 2147483647;
   private final int blockSize;
   private final LZ4Compressor compressor;
   private final ByteBufChecksum checksum;
   private final int compressionLevel;
   private ByteBuf buffer;
   private final int maxEncodeSize;
   private volatile boolean finished;
   private volatile ChannelHandlerContext ctx;

   public Lz4FrameEncoder() {
      this(false);
   }

   public Lz4FrameEncoder(boolean var1) {
      this(LZ4Factory.fastestInstance(), var1, 65536, XXHashFactory.fastestInstance().newStreamingHash32(-1756908916).asChecksum());
   }

   public Lz4FrameEncoder(LZ4Factory var1, boolean var2, int var3, Checksum var4) {
      this(var1, var2, var3, var4, 2147483647);
   }

   public Lz4FrameEncoder(LZ4Factory var1, boolean var2, int var3, Checksum var4, int var5) {
      super();
      if (var1 == null) {
         throw new NullPointerException("factory");
      } else if (var4 == null) {
         throw new NullPointerException("checksum");
      } else {
         this.compressor = var2 ? var1.highCompressor() : var1.fastCompressor();
         this.checksum = ByteBufChecksum.wrapChecksum(var4);
         this.compressionLevel = compressionLevel(var3);
         this.blockSize = var3;
         this.maxEncodeSize = ObjectUtil.checkPositive(var5, "maxEncodeSize");
         this.finished = false;
      }
   }

   private static int compressionLevel(int var0) {
      if (var0 >= 64 && var0 <= 33554432) {
         int var1 = 32 - Integer.numberOfLeadingZeros(var0 - 1);
         var1 = Math.max(0, var1 - 10);
         return var1;
      } else {
         throw new IllegalArgumentException(String.format("blockSize: %d (expected: %d-%d)", var0, 64, 33554432));
      }
   }

   protected ByteBuf allocateBuffer(ChannelHandlerContext var1, ByteBuf var2, boolean var3) {
      return this.allocateBuffer(var1, var2, var3, true);
   }

   private ByteBuf allocateBuffer(ChannelHandlerContext var1, ByteBuf var2, boolean var3, boolean var4) {
      int var5 = 0;
      int var6 = var2.readableBytes() + this.buffer.readableBytes();
      if (var6 < 0) {
         throw new EncoderException("too much data to allocate a buffer for compression");
      } else {
         while(var6 > 0) {
            int var7 = Math.min(this.blockSize, var6);
            var6 -= var7;
            var5 += this.compressor.maxCompressedLength(var7) + 21;
         }

         if (var5 <= this.maxEncodeSize && 0 <= var5) {
            if (var4 && var5 < this.blockSize) {
               return Unpooled.EMPTY_BUFFER;
            } else {
               return var3 ? var1.alloc().ioBuffer(var5, var5) : var1.alloc().heapBuffer(var5, var5);
            }
         } else {
            throw new EncoderException(String.format("requested encode buffer size (%d bytes) exceeds the maximum allowable size (%d bytes)", var5, this.maxEncodeSize));
         }
      }
   }

   protected void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) throws Exception {
      if (this.finished) {
         if (!var3.isWritable(var2.readableBytes())) {
            throw ENCODE_FINSHED_EXCEPTION;
         } else {
            var3.writeBytes(var2);
         }
      } else {
         ByteBuf var4 = this.buffer;

         int var5;
         while((var5 = var2.readableBytes()) > 0) {
            int var6 = Math.min(var5, var4.writableBytes());
            var2.readBytes(var4, var6);
            if (!var4.isWritable()) {
               this.flushBufferedData(var3);
            }
         }

      }
   }

   private void flushBufferedData(ByteBuf var1) {
      int var2 = this.buffer.readableBytes();
      if (var2 != 0) {
         this.checksum.reset();
         this.checksum.update(this.buffer, this.buffer.readerIndex(), var2);
         int var3 = (int)this.checksum.getValue();
         int var4 = this.compressor.maxCompressedLength(var2) + 21;
         var1.ensureWritable(var4);
         int var5 = var1.writerIndex();

         int var6;
         try {
            ByteBuffer var7 = var1.internalNioBuffer(var5 + 21, var1.writableBytes() - 21);
            int var8 = var7.position();
            this.compressor.compress(this.buffer.internalNioBuffer(this.buffer.readerIndex(), var2), var7);
            var6 = var7.position() - var8;
         } catch (LZ4Exception var9) {
            throw new CompressionException(var9);
         }

         byte var10;
         if (var6 >= var2) {
            var10 = 16;
            var6 = var2;
            var1.setBytes(var5 + 21, (ByteBuf)this.buffer, 0, var2);
         } else {
            var10 = 32;
         }

         var1.setLong(var5, 5501767354678207339L);
         var1.setByte(var5 + 8, (byte)(var10 | this.compressionLevel));
         var1.setIntLE(var5 + 9, var6);
         var1.setIntLE(var5 + 13, var2);
         var1.setIntLE(var5 + 17, var3);
         var1.writerIndex(var5 + 21 + var6);
         this.buffer.clear();
      }
   }

   public void flush(ChannelHandlerContext var1) throws Exception {
      if (this.buffer != null && this.buffer.isReadable()) {
         ByteBuf var2 = this.allocateBuffer(var1, Unpooled.EMPTY_BUFFER, this.isPreferDirect(), false);
         this.flushBufferedData(var2);
         var1.write(var2);
      }

      var1.flush();
   }

   private ChannelFuture finishEncode(ChannelHandlerContext var1, ChannelPromise var2) {
      if (this.finished) {
         var2.setSuccess();
         return var2;
      } else {
         this.finished = true;
         ByteBuf var3 = var1.alloc().heapBuffer(this.compressor.maxCompressedLength(this.buffer.readableBytes()) + 21);
         this.flushBufferedData(var3);
         int var4 = var3.writerIndex();
         var3.setLong(var4, 5501767354678207339L);
         var3.setByte(var4 + 8, (byte)(16 | this.compressionLevel));
         var3.setInt(var4 + 9, 0);
         var3.setInt(var4 + 13, 0);
         var3.setInt(var4 + 17, 0);
         var3.writerIndex(var4 + 21);
         return var1.writeAndFlush(var3, var2);
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
               ChannelFuture var1x = Lz4FrameEncoder.this.finishEncode(Lz4FrameEncoder.this.ctx(), var1);
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

   private ChannelHandlerContext ctx() {
      ChannelHandlerContext var1 = this.ctx;
      if (var1 == null) {
         throw new IllegalStateException("not added to a pipeline");
      } else {
         return var1;
      }
   }

   public void handlerAdded(ChannelHandlerContext var1) {
      this.ctx = var1;
      this.buffer = Unpooled.wrappedBuffer(new byte[this.blockSize]);
      this.buffer.clear();
   }

   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      super.handlerRemoved(var1);
      if (this.buffer != null) {
         this.buffer.release();
         this.buffer = null;
      }

   }

   final ByteBuf getBackingBuffer() {
      return this.buffer;
   }
}
