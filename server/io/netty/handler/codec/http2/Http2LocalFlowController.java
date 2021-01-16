package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;

public interface Http2LocalFlowController extends Http2FlowController {
   Http2LocalFlowController frameWriter(Http2FrameWriter var1);

   void receiveFlowControlledFrame(Http2Stream var1, ByteBuf var2, int var3, boolean var4) throws Http2Exception;

   boolean consumeBytes(Http2Stream var1, int var2) throws Http2Exception;

   int unconsumedBytes(Http2Stream var1);

   int initialWindowSize(Http2Stream var1);
}
