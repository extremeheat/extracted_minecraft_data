package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.ObjectUtil;

public class DecoratingHttp2FrameWriter implements Http2FrameWriter {
   private final Http2FrameWriter delegate;

   public DecoratingHttp2FrameWriter(Http2FrameWriter var1) {
      super();
      this.delegate = (Http2FrameWriter)ObjectUtil.checkNotNull(var1, "delegate");
   }

   public ChannelFuture writeData(ChannelHandlerContext var1, int var2, ByteBuf var3, int var4, boolean var5, ChannelPromise var6) {
      return this.delegate.writeData(var1, var2, var3, var4, var5, var6);
   }

   public ChannelFuture writeHeaders(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, boolean var5, ChannelPromise var6) {
      return this.delegate.writeHeaders(var1, var2, var3, var4, var5, var6);
   }

   public ChannelFuture writeHeaders(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, short var5, boolean var6, int var7, boolean var8, ChannelPromise var9) {
      return this.delegate.writeHeaders(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public ChannelFuture writePriority(ChannelHandlerContext var1, int var2, int var3, short var4, boolean var5, ChannelPromise var6) {
      return this.delegate.writePriority(var1, var2, var3, var4, var5, var6);
   }

   public ChannelFuture writeRstStream(ChannelHandlerContext var1, int var2, long var3, ChannelPromise var5) {
      return this.delegate.writeRstStream(var1, var2, var3, var5);
   }

   public ChannelFuture writeSettings(ChannelHandlerContext var1, Http2Settings var2, ChannelPromise var3) {
      return this.delegate.writeSettings(var1, var2, var3);
   }

   public ChannelFuture writeSettingsAck(ChannelHandlerContext var1, ChannelPromise var2) {
      return this.delegate.writeSettingsAck(var1, var2);
   }

   public ChannelFuture writePing(ChannelHandlerContext var1, boolean var2, long var3, ChannelPromise var5) {
      return this.delegate.writePing(var1, var2, var3, var5);
   }

   public ChannelFuture writePushPromise(ChannelHandlerContext var1, int var2, int var3, Http2Headers var4, int var5, ChannelPromise var6) {
      return this.delegate.writePushPromise(var1, var2, var3, var4, var5, var6);
   }

   public ChannelFuture writeGoAway(ChannelHandlerContext var1, int var2, long var3, ByteBuf var5, ChannelPromise var6) {
      return this.delegate.writeGoAway(var1, var2, var3, var5, var6);
   }

   public ChannelFuture writeWindowUpdate(ChannelHandlerContext var1, int var2, int var3, ChannelPromise var4) {
      return this.delegate.writeWindowUpdate(var1, var2, var3, var4);
   }

   public ChannelFuture writeFrame(ChannelHandlerContext var1, byte var2, int var3, Http2Flags var4, ByteBuf var5, ChannelPromise var6) {
      return this.delegate.writeFrame(var1, var2, var3, var4, var5, var6);
   }

   public Http2FrameWriter.Configuration configuration() {
      return this.delegate.configuration();
   }

   public void close() {
      this.delegate.close();
   }
}
