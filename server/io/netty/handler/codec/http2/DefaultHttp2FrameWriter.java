package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.collection.CharObjectMap;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.Iterator;

public class DefaultHttp2FrameWriter implements Http2FrameWriter, Http2FrameSizePolicy, Http2FrameWriter.Configuration {
   private static final String STREAM_ID = "Stream ID";
   private static final String STREAM_DEPENDENCY = "Stream Dependency";
   private static final ByteBuf ZERO_BUFFER = Unpooled.unreleasableBuffer(Unpooled.directBuffer(255).writeZero(255)).asReadOnly();
   private final Http2HeadersEncoder headersEncoder;
   private int maxFrameSize;

   public DefaultHttp2FrameWriter() {
      this((Http2HeadersEncoder)(new DefaultHttp2HeadersEncoder()));
   }

   public DefaultHttp2FrameWriter(Http2HeadersEncoder.SensitivityDetector var1) {
      this((Http2HeadersEncoder)(new DefaultHttp2HeadersEncoder(var1)));
   }

   public DefaultHttp2FrameWriter(Http2HeadersEncoder.SensitivityDetector var1, boolean var2) {
      this((Http2HeadersEncoder)(new DefaultHttp2HeadersEncoder(var1, var2)));
   }

   public DefaultHttp2FrameWriter(Http2HeadersEncoder var1) {
      super();
      this.headersEncoder = var1;
      this.maxFrameSize = 16384;
   }

   public Http2FrameWriter.Configuration configuration() {
      return this;
   }

   public Http2HeadersEncoder.Configuration headersConfiguration() {
      return this.headersEncoder.configuration();
   }

   public Http2FrameSizePolicy frameSizePolicy() {
      return this;
   }

   public void maxFrameSize(int var1) throws Http2Exception {
      if (!Http2CodecUtil.isMaxFrameSizeValid(var1)) {
         throw Http2Exception.connectionError(Http2Error.FRAME_SIZE_ERROR, "Invalid MAX_FRAME_SIZE specified in sent settings: %d", var1);
      } else {
         this.maxFrameSize = var1;
      }
   }

   public int maxFrameSize() {
      return this.maxFrameSize;
   }

   public void close() {
   }

   public ChannelFuture writeData(ChannelHandlerContext var1, int var2, ByteBuf var3, int var4, boolean var5, ChannelPromise var6) {
      Http2CodecUtil.SimpleChannelPromiseAggregator var7 = new Http2CodecUtil.SimpleChannelPromiseAggregator(var6, var1.channel(), var1.executor());
      ByteBuf var8 = null;

      try {
         verifyStreamId(var2, "Stream ID");
         Http2CodecUtil.verifyPadding(var4);
         int var9 = var3.readableBytes();
         Http2Flags var10 = new Http2Flags();
         var10.endOfStream(false);
         var10.paddingPresent(false);
         if (var9 > this.maxFrameSize) {
            var8 = var1.alloc().buffer(9);
            Http2CodecUtil.writeFrameHeaderInternal(var8, this.maxFrameSize, (byte)0, var10, var2);

            do {
               var1.write(var8.retainedSlice(), var7.newPromise());
               var1.write(var3.readRetainedSlice(this.maxFrameSize), var7.newPromise());
               var9 -= this.maxFrameSize;
            } while(var9 > this.maxFrameSize);
         }

         ByteBuf var11;
         if (var4 == 0) {
            if (var8 != null) {
               var8.release();
               var8 = null;
            }

            var11 = var1.alloc().buffer(9);
            var10.endOfStream(var5);
            Http2CodecUtil.writeFrameHeaderInternal(var11, var9, (byte)0, var10, var2);
            var1.write(var11, var7.newPromise());
            ByteBuf var12 = var3.readSlice(var9);
            var3 = null;
            var1.write(var12, var7.newPromise());
         } else {
            if (var9 != this.maxFrameSize) {
               if (var8 != null) {
                  var8.release();
                  var8 = null;
               }
            } else {
               var9 -= this.maxFrameSize;
               if (var8 == null) {
                  var11 = var1.alloc().buffer(9);
                  Http2CodecUtil.writeFrameHeaderInternal(var11, this.maxFrameSize, (byte)0, var10, var2);
               } else {
                  var11 = var8.slice();
                  var8 = null;
               }

               var1.write(var11, var7.newPromise());
               var11 = var3.readSlice(this.maxFrameSize);
               var3 = null;
               var1.write(var11, var7.newPromise());
            }

            do {
               int var21 = Math.min(var9, this.maxFrameSize);
               int var20 = Math.min(var4, Math.max(0, this.maxFrameSize - 1 - var21));
               var4 -= var20;
               var9 -= var21;
               ByteBuf var13 = var1.alloc().buffer(10);
               var10.endOfStream(var5 && var9 == 0 && var4 == 0);
               var10.paddingPresent(var20 > 0);
               Http2CodecUtil.writeFrameHeaderInternal(var13, var20 + var21, (byte)0, var10, var2);
               writePaddingLength(var13, var20);
               var1.write(var13, var7.newPromise());
               if (var21 != 0) {
                  if (var9 == 0) {
                     ByteBuf var14 = var3.readSlice(var21);
                     var3 = null;
                     var1.write(var14, var7.newPromise());
                  } else {
                     var1.write(var3.readRetainedSlice(var21), var7.newPromise());
                  }
               }

               if (paddingBytes(var20) > 0) {
                  var1.write(ZERO_BUFFER.slice(0, paddingBytes(var20)), var7.newPromise());
               }
            } while(var9 != 0 || var4 != 0);
         }
      } catch (Throwable var19) {
         if (var8 != null) {
            var8.release();
         }

         try {
            if (var3 != null) {
               var3.release();
            }
         } finally {
            var7.setFailure(var19);
            var7.doneAllocatingPromises();
         }

         return var7;
      }

      return var7.doneAllocatingPromises();
   }

   public ChannelFuture writeHeaders(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, boolean var5, ChannelPromise var6) {
      return this.writeHeadersInternal(var1, var2, var3, var4, var5, false, 0, (short)0, false, var6);
   }

   public ChannelFuture writeHeaders(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, short var5, boolean var6, int var7, boolean var8, ChannelPromise var9) {
      return this.writeHeadersInternal(var1, var2, var3, var7, var8, true, var4, var5, var6, var9);
   }

   public ChannelFuture writePriority(ChannelHandlerContext var1, int var2, int var3, short var4, boolean var5, ChannelPromise var6) {
      try {
         verifyStreamId(var2, "Stream ID");
         verifyStreamId(var3, "Stream Dependency");
         verifyWeight(var4);
         ByteBuf var7 = var1.alloc().buffer(14);
         Http2CodecUtil.writeFrameHeaderInternal(var7, 5, (byte)2, new Http2Flags(), var2);
         var7.writeInt(var5 ? (int)(2147483648L | (long)var3) : var3);
         var7.writeByte(var4 - 1);
         return var1.write(var7, var6);
      } catch (Throwable var8) {
         return var6.setFailure(var8);
      }
   }

   public ChannelFuture writeRstStream(ChannelHandlerContext var1, int var2, long var3, ChannelPromise var5) {
      try {
         verifyStreamId(var2, "Stream ID");
         verifyErrorCode(var3);
         ByteBuf var6 = var1.alloc().buffer(13);
         Http2CodecUtil.writeFrameHeaderInternal(var6, 4, (byte)3, new Http2Flags(), var2);
         var6.writeInt((int)var3);
         return var1.write(var6, var5);
      } catch (Throwable var7) {
         return var5.setFailure(var7);
      }
   }

   public ChannelFuture writeSettings(ChannelHandlerContext var1, Http2Settings var2, ChannelPromise var3) {
      try {
         ObjectUtil.checkNotNull(var2, "settings");
         int var4 = 6 * var2.size();
         ByteBuf var5 = var1.alloc().buffer(9 + var2.size() * 6);
         Http2CodecUtil.writeFrameHeaderInternal(var5, var4, (byte)4, new Http2Flags(), 0);
         Iterator var6 = var2.entries().iterator();

         while(var6.hasNext()) {
            CharObjectMap.PrimitiveEntry var7 = (CharObjectMap.PrimitiveEntry)var6.next();
            var5.writeChar(var7.key());
            var5.writeInt(((Long)var7.value()).intValue());
         }

         return var1.write(var5, var3);
      } catch (Throwable var8) {
         return var3.setFailure(var8);
      }
   }

   public ChannelFuture writeSettingsAck(ChannelHandlerContext var1, ChannelPromise var2) {
      try {
         ByteBuf var3 = var1.alloc().buffer(9);
         Http2CodecUtil.writeFrameHeaderInternal(var3, 0, (byte)4, (new Http2Flags()).ack(true), 0);
         return var1.write(var3, var2);
      } catch (Throwable var4) {
         return var2.setFailure(var4);
      }
   }

   public ChannelFuture writePing(ChannelHandlerContext var1, boolean var2, long var3, ChannelPromise var5) {
      Http2Flags var6 = var2 ? (new Http2Flags()).ack(true) : new Http2Flags();
      ByteBuf var7 = var1.alloc().buffer(17);
      Http2CodecUtil.writeFrameHeaderInternal(var7, 8, (byte)6, var6, 0);
      var7.writeLong(var3);
      return var1.write(var7, var5);
   }

   public ChannelFuture writePushPromise(ChannelHandlerContext var1, int var2, int var3, Http2Headers var4, int var5, ChannelPromise var6) {
      ByteBuf var7 = null;
      Http2CodecUtil.SimpleChannelPromiseAggregator var8 = new Http2CodecUtil.SimpleChannelPromiseAggregator(var6, var1.channel(), var1.executor());

      try {
         verifyStreamId(var2, "Stream ID");
         verifyStreamId(var3, "Promised Stream ID");
         Http2CodecUtil.verifyPadding(var5);
         var7 = var1.alloc().buffer();
         this.headersEncoder.encodeHeaders(var2, var4, var7);
         Http2Flags var9 = (new Http2Flags()).paddingPresent(var5 > 0);
         int var10 = 4 + var5;
         int var11 = this.maxFrameSize - var10;
         ByteBuf var12 = var7.readRetainedSlice(Math.min(var7.readableBytes(), var11));
         var9.endOfHeaders(!var7.isReadable());
         int var13 = var12.readableBytes() + var10;
         ByteBuf var14 = var1.alloc().buffer(14);
         Http2CodecUtil.writeFrameHeaderInternal(var14, var13, (byte)5, var9, var2);
         writePaddingLength(var14, var5);
         var14.writeInt(var3);
         var1.write(var14, var8.newPromise());
         var1.write(var12, var8.newPromise());
         if (paddingBytes(var5) > 0) {
            var1.write(ZERO_BUFFER.slice(0, paddingBytes(var5)), var8.newPromise());
         }

         if (!var9.endOfHeaders()) {
            this.writeContinuationFrames(var1, var2, var7, var5, var8);
         }
      } catch (Http2Exception var19) {
         var8.setFailure(var19);
      } catch (Throwable var20) {
         var8.setFailure(var20);
         var8.doneAllocatingPromises();
         PlatformDependent.throwException(var20);
      } finally {
         if (var7 != null) {
            var7.release();
         }

      }

      return var8.doneAllocatingPromises();
   }

   public ChannelFuture writeGoAway(ChannelHandlerContext var1, int var2, long var3, ByteBuf var5, ChannelPromise var6) {
      Http2CodecUtil.SimpleChannelPromiseAggregator var7 = new Http2CodecUtil.SimpleChannelPromiseAggregator(var6, var1.channel(), var1.executor());

      try {
         verifyStreamOrConnectionId(var2, "Last Stream ID");
         verifyErrorCode(var3);
         int var8 = 8 + var5.readableBytes();
         ByteBuf var9 = var1.alloc().buffer(17);
         Http2CodecUtil.writeFrameHeaderInternal(var9, var8, (byte)7, new Http2Flags(), 0);
         var9.writeInt(var2);
         var9.writeInt((int)var3);
         var1.write(var9, var7.newPromise());
      } catch (Throwable var16) {
         try {
            var5.release();
         } finally {
            var7.setFailure(var16);
            var7.doneAllocatingPromises();
         }

         return var7;
      }

      try {
         var1.write(var5, var7.newPromise());
      } catch (Throwable var15) {
         var7.setFailure(var15);
      }

      return var7.doneAllocatingPromises();
   }

   public ChannelFuture writeWindowUpdate(ChannelHandlerContext var1, int var2, int var3, ChannelPromise var4) {
      try {
         verifyStreamOrConnectionId(var2, "Stream ID");
         verifyWindowSizeIncrement(var3);
         ByteBuf var5 = var1.alloc().buffer(13);
         Http2CodecUtil.writeFrameHeaderInternal(var5, 4, (byte)8, new Http2Flags(), var2);
         var5.writeInt(var3);
         return var1.write(var5, var4);
      } catch (Throwable var6) {
         return var4.setFailure(var6);
      }
   }

   public ChannelFuture writeFrame(ChannelHandlerContext var1, byte var2, int var3, Http2Flags var4, ByteBuf var5, ChannelPromise var6) {
      Http2CodecUtil.SimpleChannelPromiseAggregator var7 = new Http2CodecUtil.SimpleChannelPromiseAggregator(var6, var1.channel(), var1.executor());

      try {
         verifyStreamOrConnectionId(var3, "Stream ID");
         ByteBuf var8 = var1.alloc().buffer(9);
         Http2CodecUtil.writeFrameHeaderInternal(var8, var5.readableBytes(), var2, var4, var3);
         var1.write(var8, var7.newPromise());
      } catch (Throwable var15) {
         try {
            var5.release();
         } finally {
            var7.setFailure(var15);
            var7.doneAllocatingPromises();
         }

         return var7;
      }

      try {
         var1.write(var5, var7.newPromise());
      } catch (Throwable var14) {
         var7.setFailure(var14);
      }

      return var7.doneAllocatingPromises();
   }

   private ChannelFuture writeHeadersInternal(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, boolean var5, boolean var6, int var7, short var8, boolean var9, ChannelPromise var10) {
      ByteBuf var11 = null;
      Http2CodecUtil.SimpleChannelPromiseAggregator var12 = new Http2CodecUtil.SimpleChannelPromiseAggregator(var10, var1.channel(), var1.executor());

      try {
         verifyStreamId(var2, "Stream ID");
         if (var6) {
            verifyStreamOrConnectionId(var7, "Stream Dependency");
            Http2CodecUtil.verifyPadding(var4);
            verifyWeight(var8);
         }

         var11 = var1.alloc().buffer();
         this.headersEncoder.encodeHeaders(var2, var3, var11);
         Http2Flags var13 = (new Http2Flags()).endOfStream(var5).priorityPresent(var6).paddingPresent(var4 > 0);
         int var14 = var4 + var13.getNumPriorityBytes();
         int var15 = this.maxFrameSize - var14;
         ByteBuf var16 = var11.readRetainedSlice(Math.min(var11.readableBytes(), var15));
         var13.endOfHeaders(!var11.isReadable());
         int var17 = var16.readableBytes() + var14;
         ByteBuf var18 = var1.alloc().buffer(15);
         Http2CodecUtil.writeFrameHeaderInternal(var18, var17, (byte)1, var13, var2);
         writePaddingLength(var18, var4);
         if (var6) {
            var18.writeInt(var9 ? (int)(2147483648L | (long)var7) : var7);
            var18.writeByte(var8 - 1);
         }

         var1.write(var18, var12.newPromise());
         var1.write(var16, var12.newPromise());
         if (paddingBytes(var4) > 0) {
            var1.write(ZERO_BUFFER.slice(0, paddingBytes(var4)), var12.newPromise());
         }

         if (!var13.endOfHeaders()) {
            this.writeContinuationFrames(var1, var2, var11, var4, var12);
         }
      } catch (Http2Exception var23) {
         var12.setFailure(var23);
      } catch (Throwable var24) {
         var12.setFailure(var24);
         var12.doneAllocatingPromises();
         PlatformDependent.throwException(var24);
      } finally {
         if (var11 != null) {
            var11.release();
         }

      }

      return var12.doneAllocatingPromises();
   }

   private ChannelFuture writeContinuationFrames(ChannelHandlerContext var1, int var2, ByteBuf var3, int var4, Http2CodecUtil.SimpleChannelPromiseAggregator var5) {
      Http2Flags var6 = (new Http2Flags()).paddingPresent(var4 > 0);
      int var7 = this.maxFrameSize - var4;
      if (var7 <= 0) {
         return var5.setFailure(new IllegalArgumentException("Padding [" + var4 + "] is too large for max frame size [" + this.maxFrameSize + "]"));
      } else {
         if (var3.isReadable()) {
            int var8 = Math.min(var3.readableBytes(), var7);
            int var9 = var8 + var4;
            ByteBuf var10 = var1.alloc().buffer(10);
            Http2CodecUtil.writeFrameHeaderInternal(var10, var9, (byte)9, var6, var2);
            writePaddingLength(var10, var4);

            do {
               var8 = Math.min(var3.readableBytes(), var7);
               ByteBuf var11 = var3.readRetainedSlice(var8);
               var9 = var8 + var4;
               if (var3.isReadable()) {
                  var1.write(var10.retain(), var5.newPromise());
               } else {
                  var6 = var6.endOfHeaders(true);
                  var10.release();
                  var10 = var1.alloc().buffer(10);
                  Http2CodecUtil.writeFrameHeaderInternal(var10, var9, (byte)9, var6, var2);
                  writePaddingLength(var10, var4);
                  var1.write(var10, var5.newPromise());
               }

               var1.write(var11, var5.newPromise());
               if (paddingBytes(var4) > 0) {
                  var1.write(ZERO_BUFFER.slice(0, paddingBytes(var4)), var5.newPromise());
               }
            } while(var3.isReadable());
         }

         return var5;
      }
   }

   private static int paddingBytes(int var0) {
      return var0 - 1;
   }

   private static void writePaddingLength(ByteBuf var0, int var1) {
      if (var1 > 0) {
         var0.writeByte(var1 - 1);
      }

   }

   private static void verifyStreamId(int var0, String var1) {
      if (var0 <= 0) {
         throw new IllegalArgumentException(var1 + " must be > 0");
      }
   }

   private static void verifyStreamOrConnectionId(int var0, String var1) {
      if (var0 < 0) {
         throw new IllegalArgumentException(var1 + " must be >= 0");
      }
   }

   private static void verifyWeight(short var0) {
      if (var0 < 1 || var0 > 256) {
         throw new IllegalArgumentException("Invalid weight: " + var0);
      }
   }

   private static void verifyErrorCode(long var0) {
      if (var0 < 0L || var0 > 4294967295L) {
         throw new IllegalArgumentException("Invalid errorCode: " + var0);
      }
   }

   private static void verifyWindowSizeIncrement(int var0) {
      if (var0 < 0) {
         throw new IllegalArgumentException("WindowSizeIncrement must be >= 0");
      }
   }

   private static void verifyPingPayload(ByteBuf var0) {
      if (var0 == null || var0.readableBytes() != 8) {
         throw new IllegalArgumentException("Opaque data must be 8 bytes");
      }
   }
}
