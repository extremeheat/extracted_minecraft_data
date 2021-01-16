package io.netty.handler.codec.socksx.v4;

public interface Socks4CommandRequest extends Socks4Message {
   Socks4CommandType type();

   String userId();

   String dstAddr();

   int dstPort();
}
