package io.netty.handler.codec.http2;

public interface Http2FrameSizePolicy {
   void maxFrameSize(int var1) throws Http2Exception;

   int maxFrameSize();
}
