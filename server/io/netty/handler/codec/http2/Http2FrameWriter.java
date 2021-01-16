package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.io.Closeable;

public interface Http2FrameWriter extends Http2DataWriter, Closeable {
   ChannelFuture writeHeaders(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, boolean var5, ChannelPromise var6);

   ChannelFuture writeHeaders(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, short var5, boolean var6, int var7, boolean var8, ChannelPromise var9);

   ChannelFuture writePriority(ChannelHandlerContext var1, int var2, int var3, short var4, boolean var5, ChannelPromise var6);

   ChannelFuture writeRstStream(ChannelHandlerContext var1, int var2, long var3, ChannelPromise var5);

   ChannelFuture writeSettings(ChannelHandlerContext var1, Http2Settings var2, ChannelPromise var3);

   ChannelFuture writeSettingsAck(ChannelHandlerContext var1, ChannelPromise var2);

   ChannelFuture writePing(ChannelHandlerContext var1, boolean var2, long var3, ChannelPromise var5);

   ChannelFuture writePushPromise(ChannelHandlerContext var1, int var2, int var3, Http2Headers var4, int var5, ChannelPromise var6);

   ChannelFuture writeGoAway(ChannelHandlerContext var1, int var2, long var3, ByteBuf var5, ChannelPromise var6);

   ChannelFuture writeWindowUpdate(ChannelHandlerContext var1, int var2, int var3, ChannelPromise var4);

   ChannelFuture writeFrame(ChannelHandlerContext var1, byte var2, int var3, Http2Flags var4, ByteBuf var5, ChannelPromise var6);

   Http2FrameWriter.Configuration configuration();

   void close();

   public interface Configuration {
      Http2HeadersEncoder.Configuration headersConfiguration();

      Http2FrameSizePolicy frameSizePolicy();
   }
}
