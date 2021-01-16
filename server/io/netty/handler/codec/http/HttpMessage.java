package io.netty.handler.codec.http;

public interface HttpMessage extends HttpObject {
   /** @deprecated */
   @Deprecated
   HttpVersion getProtocolVersion();

   HttpVersion protocolVersion();

   HttpMessage setProtocolVersion(HttpVersion var1);

   HttpHeaders headers();
}
