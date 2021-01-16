package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public interface Http2LifecycleManager {
   void closeStreamLocal(Http2Stream var1, ChannelFuture var2);

   void closeStreamRemote(Http2Stream var1, ChannelFuture var2);

   void closeStream(Http2Stream var1, ChannelFuture var2);

   ChannelFuture resetStream(ChannelHandlerContext var1, int var2, long var3, ChannelPromise var5);

   ChannelFuture goAway(ChannelHandlerContext var1, int var2, long var3, ByteBuf var5, ChannelPromise var6);

   void onError(ChannelHandlerContext var1, boolean var2, Throwable var3);
}
