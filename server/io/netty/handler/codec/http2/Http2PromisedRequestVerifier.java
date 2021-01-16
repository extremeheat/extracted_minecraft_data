package io.netty.handler.codec.http2;

import io.netty.channel.ChannelHandlerContext;

public interface Http2PromisedRequestVerifier {
   Http2PromisedRequestVerifier ALWAYS_VERIFY = new Http2PromisedRequestVerifier() {
      public boolean isAuthoritative(ChannelHandlerContext var1, Http2Headers var2) {
         return true;
      }

      public boolean isCacheable(Http2Headers var1) {
         return true;
      }

      public boolean isSafe(Http2Headers var1) {
         return true;
      }
   };

   boolean isAuthoritative(ChannelHandlerContext var1, Http2Headers var2);

   boolean isCacheable(Http2Headers var1);

   boolean isSafe(Http2Headers var1);
}
