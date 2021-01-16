package io.netty.handler.codec.compression;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.JZlib;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelPromiseNotifier;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.EmptyArrays;
import java.util.concurrent.TimeUnit;

public class JZlibEncoder extends ZlibEncoder {
   private final int wrapperOverhead;
   private final Deflater z;
   private volatile boolean finished;
   private volatile ChannelHandlerContext ctx;

   public JZlibEncoder() {
      this(6);
   }

   public JZlibEncoder(int var1) {
      this(ZlibWrapper.ZLIB, var1);
   }

   public JZlibEncoder(ZlibWrapper var1) {
      this(var1, 6);
   }

   public JZlibEncoder(ZlibWrapper var1, int var2) {
      this(var1, var2, 15, 8);
   }

   public JZlibEncoder(ZlibWrapper var1, int var2, int var3, int var4) {
      super();
      this.z = new Deflater();
      if (var2 >= 0 && var2 <= 9) {
         if (var3 >= 9 && var3 <= 15) {
            if (var4 >= 1 && var4 <= 9) {
               if (var1 == null) {
                  throw new NullPointerException("wrapper");
               } else if (var1 == ZlibWrapper.ZLIB_OR_NONE) {
                  throw new IllegalArgumentException("wrapper '" + ZlibWrapper.ZLIB_OR_NONE + "' is not allowed for compression.");
               } else {
                  int var5 = this.z.init(var2, var3, var4, ZlibUtil.convertWrapperType(var1));
                  if (var5 != 0) {
                     ZlibUtil.fail(this.z, "initialization failure", var5);
                  }

                  this.wrapperOverhead = ZlibUtil.wrapperOverhead(var1);
               }
            } else {
               throw new IllegalArgumentException("memLevel: " + var4 + " (expected: 1-9)");
            }
         } else {
            throw new IllegalArgumentException("windowBits: " + var3 + " (expected: 9-15)");
         }
      } else {
         throw new IllegalArgumentException("compressionLevel: " + var2 + " (expected: 0-9)");
      }
   }

   public JZlibEncoder(byte[] var1) {
      this(6, var1);
   }

   public JZlibEncoder(int var1, byte[] var2) {
      this(var1, 15, 8, var2);
   }

   public JZlibEncoder(int var1, int var2, int var3, byte[] var4) {
      super();
      this.z = new Deflater();
      if (var1 >= 0 && var1 <= 9) {
         if (var2 >= 9 && var2 <= 15) {
            if (var3 >= 1 && var3 <= 9) {
               if (var4 == null) {
                  throw new NullPointerException("dictionary");
               } else {
                  int var5 = this.z.deflateInit(var1, var2, var3, JZlib.W_ZLIB);
                  if (var5 != 0) {
                     ZlibUtil.fail(this.z, "initialization failure", var5);
                  } else {
                     var5 = this.z.deflateSetDictionary(var4, var4.length);
                     if (var5 != 0) {
                        ZlibUtil.fail(this.z, "failed to set the dictionary", var5);
                     }
                  }

                  this.wrapperOverhead = ZlibUtil.wrapperOverhead(ZlibWrapper.ZLIB);
               }
            } else {
               throw new IllegalArgumentException("memLevel: " + var3 + " (expected: 1-9)");
            }
         } else {
            throw new IllegalArgumentException("windowBits: " + var2 + " (expected: 9-15)");
         }
      } else {
         throw new IllegalArgumentException("compressionLevel: " + var1 + " (expected: 0-9)");
      }
   }

   public ChannelFuture close() {
      return this.close(this.ctx().channel().newPromise());
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
               ChannelFuture var1x = JZlibEncoder.this.finishEncode(JZlibEncoder.this.ctx(), var4);
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
            try {
               boolean var5 = var2.hasArray();
               this.z.avail_in = var4;
               if (var5) {
                  this.z.next_in = var2.array();
                  this.z.next_in_index = var2.arrayOffset() + var2.readerIndex();
               } else {
                  byte[] var6 = new byte[var4];
                  var2.getBytes(var2.readerIndex(), var6);
                  this.z.next_in = var6;
                  this.z.next_in_index = 0;
               }

               int var18 = this.z.next_in_index;
               int var7 = (int)Math.ceil((double)var4 * 1.001D) + 12 + this.wrapperOverhead;
               var3.ensureWritable(var7);
               this.z.avail_out = var7;
               this.z.next_out = var3.array();
               this.z.next_out_index = var3.arrayOffset() + var3.writerIndex();
               int var8 = this.z.next_out_index;

               int var9;
               try {
                  var9 = this.z.deflate(2);
               } finally {
                  var2.skipBytes(this.z.next_in_index - var18);
               }

               if (var9 != 0) {
                  ZlibUtil.fail(this.z, "compression failure", var9);
               }

               int var10 = this.z.next_out_index - var8;
               if (var10 > 0) {
                  var3.writerIndex(var3.writerIndex() + var10);
               }
            } finally {
               this.z.next_in = null;
               this.z.next_out = null;
            }

         }
      }
   }

   public void close(final ChannelHandlerContext var1, final ChannelPromise var2) {
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

         ChannelPromise var6;
         try {
            this.z.next_in = EmptyArrays.EMPTY_BYTES;
            this.z.next_in_index = 0;
            this.z.avail_in = 0;
            byte[] var4 = new byte[32];
            this.z.next_out = var4;
            this.z.next_out_index = 0;
            this.z.avail_out = var4.length;
            int var5 = this.z.deflate(4);
            if (var5 == 0 || var5 == 1) {
               ByteBuf var3;
               if (this.z.next_out_index != 0) {
                  var3 = Unpooled.wrappedBuffer(var4, 0, this.z.next_out_index);
               } else {
                  var3 = Unpooled.EMPTY_BUFFER;
               }

               return var1.writeAndFlush(var3, var2);
            }

            var2.setFailure(ZlibUtil.deflaterException(this.z, "compression failure", var5));
            var6 = var2;
         } finally {
            this.z.deflateEnd();
            this.z.next_in = null;
            this.z.next_out = null;
         }

         return var6;
      }
   }

   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.ctx = var1;
   }
}
