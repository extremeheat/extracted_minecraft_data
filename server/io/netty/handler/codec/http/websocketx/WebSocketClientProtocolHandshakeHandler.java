package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;

class WebSocketClientProtocolHandshakeHandler extends ChannelInboundHandlerAdapter {
   private final WebSocketClientHandshaker handshaker;

   WebSocketClientProtocolHandshakeHandler(WebSocketClientHandshaker var1) {
      super();
      this.handshaker = var1;
   }

   public void channelActive(final ChannelHandlerContext var1) throws Exception {
      super.channelActive(var1);
      this.handshaker.handshake(var1.channel()).addListener(new ChannelFutureListener() {
         public void operationComplete(ChannelFuture var1x) throws Exception {
            if (!var1x.isSuccess()) {
               var1.fireExceptionCaught(var1x.cause());
            } else {
               var1.fireUserEventTriggered(WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_ISSUED);
            }

         }
      });
   }

   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      if (!(var2 instanceof FullHttpResponse)) {
         var1.fireChannelRead(var2);
      } else {
         FullHttpResponse var3 = (FullHttpResponse)var2;

         try {
            if (this.handshaker.isHandshakeComplete()) {
               throw new IllegalStateException("WebSocketClientHandshaker should have been non finished yet");
            }

            this.handshaker.finishHandshake(var1.channel(), var3);
            var1.fireUserEventTriggered(WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE);
            var1.pipeline().remove((ChannelHandler)this);
         } finally {
            var3.release();
         }

      }
   }
}
