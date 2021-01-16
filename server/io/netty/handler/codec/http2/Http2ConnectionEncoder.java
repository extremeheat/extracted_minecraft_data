package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public interface Http2ConnectionEncoder extends Http2FrameWriter {
   void lifecycleManager(Http2LifecycleManager var1);

   Http2Connection connection();

   Http2RemoteFlowController flowController();

   Http2FrameWriter frameWriter();

   Http2Settings pollSentSettings();

   void remoteSettings(Http2Settings var1) throws Http2Exception;

   ChannelFuture writeFrame(ChannelHandlerContext var1, byte var2, int var3, Http2Flags var4, ByteBuf var5, ChannelPromise var6);
}
