package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class Http2FrameAdapter implements Http2FrameListener {
   public Http2FrameAdapter() {
      super();
   }

   public int onDataRead(ChannelHandlerContext var1, int var2, ByteBuf var3, int var4, boolean var5) throws Http2Exception {
      return var3.readableBytes() + var4;
   }

   public void onHeadersRead(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, boolean var5) throws Http2Exception {
   }

   public void onHeadersRead(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, short var5, boolean var6, int var7, boolean var8) throws Http2Exception {
   }

   public void onPriorityRead(ChannelHandlerContext var1, int var2, int var3, short var4, boolean var5) throws Http2Exception {
   }

   public void onRstStreamRead(ChannelHandlerContext var1, int var2, long var3) throws Http2Exception {
   }

   public void onSettingsAckRead(ChannelHandlerContext var1) throws Http2Exception {
   }

   public void onSettingsRead(ChannelHandlerContext var1, Http2Settings var2) throws Http2Exception {
   }

   public void onPingRead(ChannelHandlerContext var1, long var2) throws Http2Exception {
   }

   public void onPingAckRead(ChannelHandlerContext var1, long var2) throws Http2Exception {
   }

   public void onPushPromiseRead(ChannelHandlerContext var1, int var2, int var3, Http2Headers var4, int var5) throws Http2Exception {
   }

   public void onGoAwayRead(ChannelHandlerContext var1, int var2, long var3, ByteBuf var5) throws Http2Exception {
   }

   public void onWindowUpdateRead(ChannelHandlerContext var1, int var2, int var3) throws Http2Exception {
   }

   public void onUnknownFrame(ChannelHandlerContext var1, byte var2, int var3, Http2Flags var4, ByteBuf var5) {
   }
}
