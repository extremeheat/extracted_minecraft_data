package io.netty.handler.codec.http2;

public interface Http2StreamVisitor {
   boolean visit(Http2Stream var1) throws Http2Exception;
}
