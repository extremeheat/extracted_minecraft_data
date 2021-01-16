package io.netty.handler.codec.http2;

import io.netty.channel.ChannelHandlerContext;

public interface Http2FlowController {
   void channelHandlerContext(ChannelHandlerContext var1) throws Http2Exception;

   void initialWindowSize(int var1) throws Http2Exception;

   int initialWindowSize();

   int windowSize(Http2Stream var1);

   void incrementWindowSize(Http2Stream var1, int var2) throws Http2Exception;
}
