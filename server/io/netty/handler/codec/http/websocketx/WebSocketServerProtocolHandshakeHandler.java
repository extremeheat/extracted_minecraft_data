package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslHandler;

class WebSocketServerProtocolHandshakeHandler extends ChannelInboundHandlerAdapter {
   private final String websocketPath;
   private final String subprotocols;
   private final boolean allowExtensions;
   private final int maxFramePayloadSize;
   private final boolean allowMaskMismatch;
   private final boolean checkStartsWith;

   WebSocketServerProtocolHandshakeHandler(String var1, String var2, boolean var3, int var4, boolean var5) {
      this(var1, var2, var3, var4, var5, false);
   }

   WebSocketServerProtocolHandshakeHandler(String var1, String var2, boolean var3, int var4, boolean var5, boolean var6) {
      super();
      this.websocketPath = var1;
      this.subprotocols = var2;
      this.allowExtensions = var3;
      this.maxFramePayloadSize = var4;
      this.allowMaskMismatch = var5;
      this.checkStartsWith = var6;
   }

   public void channelRead(final ChannelHandlerContext var1, Object var2) throws Exception {
      final FullHttpRequest var3 = (FullHttpRequest)var2;
      if (this.isNotWebSocketPath(var3)) {
         var1.fireChannelRead(var2);
      } else {
         try {
            if (var3.method() == HttpMethod.GET) {
               WebSocketServerHandshakerFactory var4 = new WebSocketServerHandshakerFactory(getWebSocketLocation(var1.pipeline(), var3, this.websocketPath), this.subprotocols, this.allowExtensions, this.maxFramePayloadSize, this.allowMaskMismatch);
               final WebSocketServerHandshaker var5 = var4.newHandshaker(var3);
               if (var5 == null) {
                  WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(var1.channel());
               } else {
                  ChannelFuture var6 = var5.handshake(var1.channel(), var3);
                  var6.addListener(new ChannelFutureListener() {
                     public void operationComplete(ChannelFuture var1x) throws Exception {
                        if (!var1x.isSuccess()) {
                           var1.fireExceptionCaught(var1x.cause());
                        } else {
                           var1.fireUserEventTriggered(WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);
                           var1.fireUserEventTriggered(new WebSocketServerProtocolHandler.HandshakeComplete(var3.uri(), var3.headers(), var5.selectedSubprotocol()));
                        }

                     }
                  });
                  WebSocketServerProtocolHandler.setHandshaker(var1.channel(), var5);
                  var1.pipeline().replace((ChannelHandler)this, "WS403Responder", WebSocketServerProtocolHandler.forbiddenHttpRequestResponder());
               }

               return;
            }

            sendHttpResponse(var1, var3, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
         } finally {
            var3.release();
         }

      }
   }

   private boolean isNotWebSocketPath(FullHttpRequest var1) {
      return this.checkStartsWith ? !var1.uri().startsWith(this.websocketPath) : !var1.uri().equals(this.websocketPath);
   }

   private static void sendHttpResponse(ChannelHandlerContext var0, HttpRequest var1, HttpResponse var2) {
      ChannelFuture var3 = var0.channel().writeAndFlush(var2);
      if (!HttpUtil.isKeepAlive(var1) || var2.status().code() != 200) {
         var3.addListener(ChannelFutureListener.CLOSE);
      }

   }

   private static String getWebSocketLocation(ChannelPipeline var0, HttpRequest var1, String var2) {
      String var3 = "ws";
      if (var0.get(SslHandler.class) != null) {
         var3 = "wss";
      }

      String var4 = var1.headers().get((CharSequence)HttpHeaderNames.HOST);
      return var3 + "://" + var4 + var2;
   }
}
