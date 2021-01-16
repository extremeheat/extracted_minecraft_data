package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;

public class Http2FrameListenerDecorator implements Http2FrameListener {
   protected final Http2FrameListener listener;

   public Http2FrameListenerDecorator(Http2FrameListener var1) {
      super();
      this.listener = (Http2FrameListener)ObjectUtil.checkNotNull(var1, "listener");
   }

   public int onDataRead(ChannelHandlerContext var1, int var2, ByteBuf var3, int var4, boolean var5) throws Http2Exception {
      return this.listener.onDataRead(var1, var2, var3, var4, var5);
   }

   public void onHeadersRead(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, boolean var5) throws Http2Exception {
      this.listener.onHeadersRead(var1, var2, var3, var4, var5);
   }

   public void onHeadersRead(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, short var5, boolean var6, int var7, boolean var8) throws Http2Exception {
      this.listener.onHeadersRead(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void onPriorityRead(ChannelHandlerContext var1, int var2, int var3, short var4, boolean var5) throws Http2Exception {
      this.listener.onPriorityRead(var1, var2, var3, var4, var5);
   }

   public void onRstStreamRead(ChannelHandlerContext var1, int var2, long var3) throws Http2Exception {
      this.listener.onRstStreamRead(var1, var2, var3);
   }

   public void onSettingsAckRead(ChannelHandlerContext var1) throws Http2Exception {
      this.listener.onSettingsAckRead(var1);
   }

   public void onSettingsRead(ChannelHandlerContext var1, Http2Settings var2) throws Http2Exception {
      this.listener.onSettingsRead(var1, var2);
   }

   public void onPingRead(ChannelHandlerContext var1, long var2) throws Http2Exception {
      this.listener.onPingRead(var1, var2);
   }

   public void onPingAckRead(ChannelHandlerContext var1, long var2) throws Http2Exception {
      this.listener.onPingAckRead(var1, var2);
   }

   public void onPushPromiseRead(ChannelHandlerContext var1, int var2, int var3, Http2Headers var4, int var5) throws Http2Exception {
      this.listener.onPushPromiseRead(var1, var2, var3, var4, var5);
   }

   public void onGoAwayRead(ChannelHandlerContext var1, int var2, long var3, ByteBuf var5) throws Http2Exception {
      this.listener.onGoAwayRead(var1, var2, var3, var5);
   }

   public void onWindowUpdateRead(ChannelHandlerContext var1, int var2, int var3) throws Http2Exception {
      this.listener.onWindowUpdateRead(var1, var2, var3);
   }

   public void onUnknownFrame(ChannelHandlerContext var1, byte var2, int var3, Http2Flags var4, ByteBuf var5) throws Http2Exception {
      this.listener.onUnknownFrame(var1, var2, var3, var4, var5);
   }
}
