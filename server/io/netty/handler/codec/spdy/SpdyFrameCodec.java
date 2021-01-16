package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import java.net.SocketAddress;
import java.util.List;

public class SpdyFrameCodec extends ByteToMessageDecoder implements SpdyFrameDecoderDelegate, ChannelOutboundHandler {
   private static final SpdyProtocolException INVALID_FRAME = new SpdyProtocolException("Received invalid frame");
   private final SpdyFrameDecoder spdyFrameDecoder;
   private final SpdyFrameEncoder spdyFrameEncoder;
   private final SpdyHeaderBlockDecoder spdyHeaderBlockDecoder;
   private final SpdyHeaderBlockEncoder spdyHeaderBlockEncoder;
   private SpdyHeadersFrame spdyHeadersFrame;
   private SpdySettingsFrame spdySettingsFrame;
   private ChannelHandlerContext ctx;
   private boolean read;
   private final boolean validateHeaders;

   public SpdyFrameCodec(SpdyVersion var1) {
      this(var1, true);
   }

   public SpdyFrameCodec(SpdyVersion var1, boolean var2) {
      this(var1, 8192, 16384, 6, 15, 8, var2);
   }

   public SpdyFrameCodec(SpdyVersion var1, int var2, int var3, int var4, int var5, int var6) {
      this(var1, var2, var3, var4, var5, var6, true);
   }

   public SpdyFrameCodec(SpdyVersion var1, int var2, int var3, int var4, int var5, int var6, boolean var7) {
      this(var1, var2, SpdyHeaderBlockDecoder.newInstance(var1, var3), SpdyHeaderBlockEncoder.newInstance(var1, var4, var5, var6), var7);
   }

   protected SpdyFrameCodec(SpdyVersion var1, int var2, SpdyHeaderBlockDecoder var3, SpdyHeaderBlockEncoder var4, boolean var5) {
      super();
      this.spdyFrameDecoder = new SpdyFrameDecoder(var1, this, var2);
      this.spdyFrameEncoder = new SpdyFrameEncoder(var1);
      this.spdyHeaderBlockDecoder = var3;
      this.spdyHeaderBlockEncoder = var4;
      this.validateHeaders = var5;
   }

   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      super.handlerAdded(var1);
      this.ctx = var1;
      var1.channel().closeFuture().addListener(new ChannelFutureListener() {
         public void operationComplete(ChannelFuture var1) throws Exception {
            SpdyFrameCodec.this.spdyHeaderBlockDecoder.end();
            SpdyFrameCodec.this.spdyHeaderBlockEncoder.end();
         }
      });
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      this.spdyFrameDecoder.decode(var2);
   }

   public void channelReadComplete(ChannelHandlerContext var1) throws Exception {
      if (!this.read && !var1.channel().config().isAutoRead()) {
         var1.read();
      }

      this.read = false;
      super.channelReadComplete(var1);
   }

   public void bind(ChannelHandlerContext var1, SocketAddress var2, ChannelPromise var3) throws Exception {
      var1.bind(var2, var3);
   }

   public void connect(ChannelHandlerContext var1, SocketAddress var2, SocketAddress var3, ChannelPromise var4) throws Exception {
      var1.connect(var2, var3, var4);
   }

   public void disconnect(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      var1.disconnect(var2);
   }

   public void close(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      var1.close(var2);
   }

   public void deregister(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      var1.deregister(var2);
   }

   public void read(ChannelHandlerContext var1) throws Exception {
      var1.read();
   }

   public void flush(ChannelHandlerContext var1) throws Exception {
      var1.flush();
   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      ByteBuf var4;
      if (var2 instanceof SpdyDataFrame) {
         SpdyDataFrame var5 = (SpdyDataFrame)var2;
         var4 = this.spdyFrameEncoder.encodeDataFrame(var1.alloc(), var5.streamId(), var5.isLast(), var5.content());
         var5.release();
         var1.write(var4, var3);
      } else {
         ByteBuf var6;
         if (var2 instanceof SpdySynStreamFrame) {
            SpdySynStreamFrame var22 = (SpdySynStreamFrame)var2;
            var6 = this.spdyHeaderBlockEncoder.encode(var1.alloc(), var22);

            try {
               var4 = this.spdyFrameEncoder.encodeSynStreamFrame(var1.alloc(), var22.streamId(), var22.associatedStreamId(), var22.priority(), var22.isLast(), var22.isUnidirectional(), var6);
            } finally {
               var6.release();
            }

            var1.write(var4, var3);
         } else if (var2 instanceof SpdySynReplyFrame) {
            SpdySynReplyFrame var23 = (SpdySynReplyFrame)var2;
            var6 = this.spdyHeaderBlockEncoder.encode(var1.alloc(), var23);

            try {
               var4 = this.spdyFrameEncoder.encodeSynReplyFrame(var1.alloc(), var23.streamId(), var23.isLast(), var6);
            } finally {
               var6.release();
            }

            var1.write(var4, var3);
         } else if (var2 instanceof SpdyRstStreamFrame) {
            SpdyRstStreamFrame var24 = (SpdyRstStreamFrame)var2;
            var4 = this.spdyFrameEncoder.encodeRstStreamFrame(var1.alloc(), var24.streamId(), var24.status().code());
            var1.write(var4, var3);
         } else if (var2 instanceof SpdySettingsFrame) {
            SpdySettingsFrame var25 = (SpdySettingsFrame)var2;
            var4 = this.spdyFrameEncoder.encodeSettingsFrame(var1.alloc(), var25);
            var1.write(var4, var3);
         } else if (var2 instanceof SpdyPingFrame) {
            SpdyPingFrame var26 = (SpdyPingFrame)var2;
            var4 = this.spdyFrameEncoder.encodePingFrame(var1.alloc(), var26.id());
            var1.write(var4, var3);
         } else if (var2 instanceof SpdyGoAwayFrame) {
            SpdyGoAwayFrame var27 = (SpdyGoAwayFrame)var2;
            var4 = this.spdyFrameEncoder.encodeGoAwayFrame(var1.alloc(), var27.lastGoodStreamId(), var27.status().code());
            var1.write(var4, var3);
         } else if (var2 instanceof SpdyHeadersFrame) {
            SpdyHeadersFrame var28 = (SpdyHeadersFrame)var2;
            var6 = this.spdyHeaderBlockEncoder.encode(var1.alloc(), var28);

            try {
               var4 = this.spdyFrameEncoder.encodeHeadersFrame(var1.alloc(), var28.streamId(), var28.isLast(), var6);
            } finally {
               var6.release();
            }

            var1.write(var4, var3);
         } else {
            if (!(var2 instanceof SpdyWindowUpdateFrame)) {
               throw new UnsupportedMessageTypeException(var2, new Class[0]);
            }

            SpdyWindowUpdateFrame var29 = (SpdyWindowUpdateFrame)var2;
            var4 = this.spdyFrameEncoder.encodeWindowUpdateFrame(var1.alloc(), var29.streamId(), var29.deltaWindowSize());
            var1.write(var4, var3);
         }
      }

   }

   public void readDataFrame(int var1, boolean var2, ByteBuf var3) {
      this.read = true;
      DefaultSpdyDataFrame var4 = new DefaultSpdyDataFrame(var1, var3);
      var4.setLast(var2);
      this.ctx.fireChannelRead(var4);
   }

   public void readSynStreamFrame(int var1, int var2, byte var3, boolean var4, boolean var5) {
      DefaultSpdySynStreamFrame var6 = new DefaultSpdySynStreamFrame(var1, var2, var3, this.validateHeaders);
      var6.setLast(var4);
      var6.setUnidirectional(var5);
      this.spdyHeadersFrame = var6;
   }

   public void readSynReplyFrame(int var1, boolean var2) {
      DefaultSpdySynReplyFrame var3 = new DefaultSpdySynReplyFrame(var1, this.validateHeaders);
      var3.setLast(var2);
      this.spdyHeadersFrame = var3;
   }

   public void readRstStreamFrame(int var1, int var2) {
      this.read = true;
      DefaultSpdyRstStreamFrame var3 = new DefaultSpdyRstStreamFrame(var1, var2);
      this.ctx.fireChannelRead(var3);
   }

   public void readSettingsFrame(boolean var1) {
      this.read = true;
      this.spdySettingsFrame = new DefaultSpdySettingsFrame();
      this.spdySettingsFrame.setClearPreviouslyPersistedSettings(var1);
   }

   public void readSetting(int var1, int var2, boolean var3, boolean var4) {
      this.spdySettingsFrame.setValue(var1, var2, var3, var4);
   }

   public void readSettingsEnd() {
      this.read = true;
      SpdySettingsFrame var1 = this.spdySettingsFrame;
      this.spdySettingsFrame = null;
      this.ctx.fireChannelRead(var1);
   }

   public void readPingFrame(int var1) {
      this.read = true;
      DefaultSpdyPingFrame var2 = new DefaultSpdyPingFrame(var1);
      this.ctx.fireChannelRead(var2);
   }

   public void readGoAwayFrame(int var1, int var2) {
      this.read = true;
      DefaultSpdyGoAwayFrame var3 = new DefaultSpdyGoAwayFrame(var1, var2);
      this.ctx.fireChannelRead(var3);
   }

   public void readHeadersFrame(int var1, boolean var2) {
      this.spdyHeadersFrame = new DefaultSpdyHeadersFrame(var1, this.validateHeaders);
      this.spdyHeadersFrame.setLast(var2);
   }

   public void readWindowUpdateFrame(int var1, int var2) {
      this.read = true;
      DefaultSpdyWindowUpdateFrame var3 = new DefaultSpdyWindowUpdateFrame(var1, var2);
      this.ctx.fireChannelRead(var3);
   }

   public void readHeaderBlock(ByteBuf var1) {
      try {
         this.spdyHeaderBlockDecoder.decode(this.ctx.alloc(), var1, this.spdyHeadersFrame);
      } catch (Exception var6) {
         this.ctx.fireExceptionCaught(var6);
      } finally {
         var1.release();
      }

   }

   public void readHeaderBlockEnd() {
      SpdyHeadersFrame var1 = null;

      try {
         this.spdyHeaderBlockDecoder.endHeaderBlock(this.spdyHeadersFrame);
         var1 = this.spdyHeadersFrame;
         this.spdyHeadersFrame = null;
      } catch (Exception var3) {
         this.ctx.fireExceptionCaught(var3);
      }

      if (var1 != null) {
         this.read = true;
         this.ctx.fireChannelRead(var1);
      }

   }

   public void readFrameError(String var1) {
      this.ctx.fireExceptionCaught(INVALID_FRAME);
   }
}
