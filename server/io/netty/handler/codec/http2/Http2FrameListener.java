package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface Http2FrameListener {
   int onDataRead(ChannelHandlerContext var1, int var2, ByteBuf var3, int var4, boolean var5) throws Http2Exception;

   void onHeadersRead(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, boolean var5) throws Http2Exception;

   void onHeadersRead(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, short var5, boolean var6, int var7, boolean var8) throws Http2Exception;

   void onPriorityRead(ChannelHandlerContext var1, int var2, int var3, short var4, boolean var5) throws Http2Exception;

   void onRstStreamRead(ChannelHandlerContext var1, int var2, long var3) throws Http2Exception;

   void onSettingsAckRead(ChannelHandlerContext var1) throws Http2Exception;

   void onSettingsRead(ChannelHandlerContext var1, Http2Settings var2) throws Http2Exception;

   void onPingRead(ChannelHandlerContext var1, long var2) throws Http2Exception;

   void onPingAckRead(ChannelHandlerContext var1, long var2) throws Http2Exception;

   void onPushPromiseRead(ChannelHandlerContext var1, int var2, int var3, Http2Headers var4, int var5) throws Http2Exception;

   void onGoAwayRead(ChannelHandlerContext var1, int var2, long var3, ByteBuf var5) throws Http2Exception;

   void onWindowUpdateRead(ChannelHandlerContext var1, int var2, int var3) throws Http2Exception;

   void onUnknownFrame(ChannelHandlerContext var1, byte var2, int var3, Http2Flags var4, ByteBuf var5) throws Http2Exception;
}
