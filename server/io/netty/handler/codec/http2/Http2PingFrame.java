package io.netty.handler.codec.http2;

public interface Http2PingFrame extends Http2Frame {
   boolean ack();

   long content();
}
