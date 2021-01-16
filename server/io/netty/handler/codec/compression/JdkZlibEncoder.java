package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelPromiseNotifier;
import io.netty.util.concurrent.EventExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

public class JdkZlibEncoder extends ZlibEncoder {
   private final ZlibWrapper wrapper;
   private final Deflater deflater;
   private volatile boolean finished;
   private volatile ChannelHandlerContext ctx;
   private final CRC32 crc;
   private static final byte[] gzipHeader = new byte[]{31, -117, 8, 0, 0, 0, 0, 0, 0, 0};
   private boolean writeHeader;

   public JdkZlibEncoder() {
      this(6);
   }

   public JdkZlibEncoder(int var1) {
      this(ZlibWrapper.ZLIB, var1);
   }

   public JdkZlibEncoder(ZlibWrapper var1) {
      this(var1, 6);
   }

   public JdkZlibEncoder(ZlibWrapper var1, int var2) {
      super();
      this.crc = new CRC32();
      this.writeHeader = true;
      if (var2 >= 0 && var2 <= 9) {
         if (var1 == null) {
            throw new NullPointerException("wrapper");
         } else if (var1 == ZlibWrapper.ZLIB_OR_NONE) {
            throw new IllegalArgumentException("wrapper '" + ZlibWrapper.ZLIB_OR_NONE + "' is not allowed for compression.");
         } else {
            this.wrapper = var1;
            this.deflater = new Deflater(var2, var1 != ZlibWrapper.ZLIB);
         }
      } else {
         throw new IllegalArgumentException("compressionLevel: " + var2 + " (expected: 0-9)");
      }
   }

   public JdkZlibEncoder(byte[] var1) {
      this(6, var1);
   }

   public JdkZlibEncoder(int var1, byte[] var2) {
      super();
      this.crc = new CRC32();
      this.writeHeader = true;
      if (var1 >= 0 && var1 <= 9) {
         if (var2 == null) {
            throw new NullPointerException("dictionary");
         } else {
            this.wrapper = ZlibWrapper.ZLIB;
            this.deflater = new Deflater(var1);
            this.deflater.setDictionary(var2);
         }
      } else {
         throw new IllegalArgumentException("compressionLevel: " + var1 + " (expected: 0-9)");
      }
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
         final ChannelPromise var4 = var2.newPromise();
         var3.execute(new Runnable() {
            public void run() {
               ChannelFuture var1x = JdkZlibEncoder.this.finishEncode(JdkZlibEncoder.this.ctx(), var4);
               var1x.addListener(new ChannelPromiseNotifier(new ChannelPromise[]{var1}));
            }
         });
         return var4;
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

   public boolean isClosed() {
      return this.finished;
   }

   protected void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) throws Exception {
      if (this.finished) {
         var3.writeBytes(var2);
      } else {
         int var4 = var2.readableBytes();
         if (var4 != 0) {
            int var5;
            byte[] var6;
            if (var2.hasArray()) {
               var6 = var2.array();
               var5 = var2.arrayOffset() + var2.readerIndex();
               var2.skipBytes(var4);
            } else {
               var6 = new byte[var4];
               var2.readBytes(var6);
               var5 = 0;
            }

            if (this.writeHeader) {
               this.writeHeader = false;
               if (this.wrapper == ZlibWrapper.GZIP) {
                  var3.writeBytes(gzipHeader);
               }
            }

            if (this.wrapper == ZlibWrapper.GZIP) {
               this.crc.update(var6, var5, var4);
            }

            this.deflater.setInput(var6, var5, var4);

            while(!this.deflater.needsInput()) {
               this.deflate(var3);
            }

         }
      }
   }

   protected final ByteBuf allocateBuffer(ChannelHandlerContext var1, ByteBuf var2, boolean var3) throws Exception {
      int var4 = (int)Math.ceil((double)var2.readableBytes() * 1.001D) + 12;
      if (this.writeHeader) {
         switch(this.wrapper) {
         case GZIP:
            var4 += gzipHeader.length;
            break;
         case ZLIB:
            var4 += 2;
         }
      }

      return var1.alloc().heapBuffer(var4);
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
         ByteBuf var3 = var1.alloc().heapBuffer();
         if (this.writeHeader && this.wrapper == ZlibWrapper.GZIP) {
            this.writeHeader = false;
            var3.writeBytes(gzipHeader);
         }

         this.deflater.finish();

         while(!this.deflater.finished()) {
            this.deflate(var3);
            if (!var3.isWritable()) {
               var1.write(var3);
               var3 = var1.alloc().heapBuffer();
            }
         }

         if (this.wrapper == ZlibWrapper.GZIP) {
            int var4 = (int)this.crc.getValue();
            int var5 = this.deflater.getTotalIn();
            var3.writeByte(var4);
            var3.writeByte(var4 >>> 8);
            var3.writeByte(var4 >>> 16);
            var3.writeByte(var4 >>> 24);
            var3.writeByte(var5);
            var3.writeByte(var5 >>> 8);
            var3.writeByte(var5 >>> 16);
            var3.writeByte(var5 >>> 24);
         }

         this.deflater.end();
         return var1.writeAndFlush(var3, var2);
      }
   }

   private void deflate(ByteBuf var1) {
      int var2;
      do {
         int var3 = var1.writerIndex();
         var2 = this.deflater.deflate(var1.array(), var1.arrayOffset() + var3, var1.writableBytes(), 2);
         var1.writerIndex(var3 + var2);
      } while(var2 > 0);

   }

   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.ctx = var1;
   }
}
