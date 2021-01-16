package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.util.internal.StringUtil;
import java.util.List;

public abstract class ByteToMessageDecoder extends ChannelInboundHandlerAdapter {
   public static final ByteToMessageDecoder.Cumulator MERGE_CUMULATOR = new ByteToMessageDecoder.Cumulator() {
      public ByteBuf cumulate(ByteBufAllocator var1, ByteBuf var2, ByteBuf var3) {
         ByteBuf var4;
         if (var2.writerIndex() <= var2.maxCapacity() - var3.readableBytes() && var2.refCnt() <= 1 && !var2.isReadOnly()) {
            var4 = var2;
         } else {
            var4 = ByteToMessageDecoder.expandCumulation(var1, var2, var3.readableBytes());
         }

         var4.writeBytes(var3);
         var3.release();
         return var4;
      }
   };
   public static final ByteToMessageDecoder.Cumulator COMPOSITE_CUMULATOR = new ByteToMessageDecoder.Cumulator() {
      public ByteBuf cumulate(ByteBufAllocator var1, ByteBuf var2, ByteBuf var3) {
         Object var4;
         if (var2.refCnt() > 1) {
            var4 = ByteToMessageDecoder.expandCumulation(var1, var2, var3.readableBytes());
            ((ByteBuf)var4).writeBytes(var3);
            var3.release();
         } else {
            CompositeByteBuf var5;
            if (var2 instanceof CompositeByteBuf) {
               var5 = (CompositeByteBuf)var2;
            } else {
               var5 = var1.compositeBuffer(2147483647);
               var5.addComponent(true, var2);
            }

            var5.addComponent(true, var3);
            var4 = var5;
         }

         return (ByteBuf)var4;
      }
   };
   private static final byte STATE_INIT = 0;
   private static final byte STATE_CALLING_CHILD_DECODE = 1;
   private static final byte STATE_HANDLER_REMOVED_PENDING = 2;
   ByteBuf cumulation;
   private ByteToMessageDecoder.Cumulator cumulator;
   private boolean singleDecode;
   private boolean decodeWasNull;
   private boolean first;
   private byte decodeState;
   private int discardAfterReads;
   private int numReads;

   protected ByteToMessageDecoder() {
      super();
      this.cumulator = MERGE_CUMULATOR;
      this.decodeState = 0;
      this.discardAfterReads = 16;
      this.ensureNotSharable();
   }

   public void setSingleDecode(boolean var1) {
      this.singleDecode = var1;
   }

   public boolean isSingleDecode() {
      return this.singleDecode;
   }

   public void setCumulator(ByteToMessageDecoder.Cumulator var1) {
      if (var1 == null) {
         throw new NullPointerException("cumulator");
      } else {
         this.cumulator = var1;
      }
   }

   public void setDiscardAfterReads(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("discardAfterReads must be > 0");
      } else {
         this.discardAfterReads = var1;
      }
   }

   protected int actualReadableBytes() {
      return this.internalBuffer().readableBytes();
   }

   protected ByteBuf internalBuffer() {
      return this.cumulation != null ? this.cumulation : Unpooled.EMPTY_BUFFER;
   }

   public final void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      if (this.decodeState == 1) {
         this.decodeState = 2;
      } else {
         ByteBuf var2 = this.cumulation;
         if (var2 != null) {
            this.cumulation = null;
            int var3 = var2.readableBytes();
            if (var3 > 0) {
               ByteBuf var4 = var2.readBytes(var3);
               var2.release();
               var1.fireChannelRead(var4);
            } else {
               var2.release();
            }

            this.numReads = 0;
            var1.fireChannelReadComplete();
         }

         this.handlerRemoved0(var1);
      }
   }

   protected void handlerRemoved0(ChannelHandlerContext var1) throws Exception {
   }

   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 instanceof ByteBuf) {
         CodecOutputList var3 = CodecOutputList.newInstance();
         boolean var10 = false;

         try {
            var10 = true;
            ByteBuf var4 = (ByteBuf)var2;
            this.first = this.cumulation == null;
            if (this.first) {
               this.cumulation = var4;
            } else {
               this.cumulation = this.cumulator.cumulate(var1.alloc(), this.cumulation, var4);
            }

            this.callDecode(var1, this.cumulation, var3);
            var10 = false;
         } catch (DecoderException var11) {
            throw var11;
         } catch (Exception var12) {
            throw new DecoderException(var12);
         } finally {
            if (var10) {
               if (this.cumulation != null && !this.cumulation.isReadable()) {
                  this.numReads = 0;
                  this.cumulation.release();
                  this.cumulation = null;
               } else if (++this.numReads >= this.discardAfterReads) {
                  this.numReads = 0;
                  this.discardSomeReadBytes();
               }

               int var6 = var3.size();
               this.decodeWasNull = !var3.insertSinceRecycled();
               fireChannelRead(var1, var3, var6);
               var3.recycle();
            }
         }

         if (this.cumulation != null && !this.cumulation.isReadable()) {
            this.numReads = 0;
            this.cumulation.release();
            this.cumulation = null;
         } else if (++this.numReads >= this.discardAfterReads) {
            this.numReads = 0;
            this.discardSomeReadBytes();
         }

         int var14 = var3.size();
         this.decodeWasNull = !var3.insertSinceRecycled();
         fireChannelRead(var1, var3, var14);
         var3.recycle();
      } else {
         var1.fireChannelRead(var2);
      }

   }

   static void fireChannelRead(ChannelHandlerContext var0, List<Object> var1, int var2) {
      if (var1 instanceof CodecOutputList) {
         fireChannelRead(var0, (CodecOutputList)var1, var2);
      } else {
         for(int var3 = 0; var3 < var2; ++var3) {
            var0.fireChannelRead(var1.get(var3));
         }
      }

   }

   static void fireChannelRead(ChannelHandlerContext var0, CodecOutputList var1, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         var0.fireChannelRead(var1.getUnsafe(var3));
      }

   }

   public void channelReadComplete(ChannelHandlerContext var1) throws Exception {
      this.numReads = 0;
      this.discardSomeReadBytes();
      if (this.decodeWasNull) {
         this.decodeWasNull = false;
         if (!var1.channel().config().isAutoRead()) {
            var1.read();
         }
      }

      var1.fireChannelReadComplete();
   }

   protected final void discardSomeReadBytes() {
      if (this.cumulation != null && !this.first && this.cumulation.refCnt() == 1) {
         this.cumulation.discardSomeReadBytes();
      }

   }

   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      this.channelInputClosed(var1, true);
   }

   public void userEventTriggered(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 instanceof ChannelInputShutdownEvent) {
         this.channelInputClosed(var1, false);
      }

      super.userEventTriggered(var1, var2);
   }

   private void channelInputClosed(ChannelHandlerContext var1, boolean var2) throws Exception {
      CodecOutputList var3 = CodecOutputList.newInstance();
      boolean var24 = false;

      try {
         var24 = true;
         this.channelInputClosed(var1, var3);
         var24 = false;
      } catch (DecoderException var25) {
         throw var25;
      } catch (Exception var26) {
         throw new DecoderException(var26);
      } finally {
         if (var24) {
            try {
               if (this.cumulation != null) {
                  this.cumulation.release();
                  this.cumulation = null;
               }

               int var7 = var3.size();
               fireChannelRead(var1, var3, var7);
               if (var7 > 0) {
                  var1.fireChannelReadComplete();
               }

               if (var2) {
                  var1.fireChannelInactive();
               }
            } finally {
               var3.recycle();
            }

         }
      }

      try {
         if (this.cumulation != null) {
            this.cumulation.release();
            this.cumulation = null;
         }

         int var4 = var3.size();
         fireChannelRead(var1, var3, var4);
         if (var4 > 0) {
            var1.fireChannelReadComplete();
         }

         if (var2) {
            var1.fireChannelInactive();
         }
      } finally {
         var3.recycle();
      }

   }

   void channelInputClosed(ChannelHandlerContext var1, List<Object> var2) throws Exception {
      if (this.cumulation != null) {
         this.callDecode(var1, this.cumulation, var2);
         this.decodeLast(var1, this.cumulation, var2);
      } else {
         this.decodeLast(var1, Unpooled.EMPTY_BUFFER, var2);
      }

   }

   protected void callDecode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) {
      try {
         while(true) {
            if (var2.isReadable()) {
               int var4 = var3.size();
               if (var4 > 0) {
                  fireChannelRead(var1, var3, var4);
                  var3.clear();
                  if (var1.isRemoved()) {
                     return;
                  }

                  var4 = 0;
               }

               int var5 = var2.readableBytes();
               this.decodeRemovalReentryProtection(var1, var2, var3);
               if (!var1.isRemoved()) {
                  if (var4 == var3.size()) {
                     if (var5 != var2.readableBytes()) {
                        continue;
                     }
                  } else {
                     if (var5 == var2.readableBytes()) {
                        throw new DecoderException(StringUtil.simpleClassName(this.getClass()) + ".decode() did not read anything but decoded a message.");
                     }

                     if (!this.isSingleDecode()) {
                        continue;
                     }
                  }
               }
            }

            return;
         }
      } catch (DecoderException var6) {
         throw var6;
      } catch (Exception var7) {
         throw new DecoderException(var7);
      }
   }

   protected abstract void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception;

   final void decodeRemovalReentryProtection(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      this.decodeState = 1;
      boolean var8 = false;

      try {
         var8 = true;
         this.decode(var1, var2, var3);
         var8 = false;
      } finally {
         if (var8) {
            boolean var6 = this.decodeState == 2;
            this.decodeState = 0;
            if (var6) {
               this.handlerRemoved(var1);
            }

         }
      }

      boolean var4 = this.decodeState == 2;
      this.decodeState = 0;
      if (var4) {
         this.handlerRemoved(var1);
      }

   }

   protected void decodeLast(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (var2.isReadable()) {
         this.decodeRemovalReentryProtection(var1, var2, var3);
      }

   }

   static ByteBuf expandCumulation(ByteBufAllocator var0, ByteBuf var1, int var2) {
      ByteBuf var3 = var1;
      var1 = var0.buffer(var1.readableBytes() + var2);
      var1.writeBytes(var3);
      var3.release();
      return var1;
   }

   public interface Cumulator {
      ByteBuf cumulate(ByteBufAllocator var1, ByteBuf var2, ByteBuf var3);
   }
}
