package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AttributeKey;
import java.util.List;

public class WebSocketServerProtocolHandler extends WebSocketProtocolHandler {
   private static final AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY = AttributeKey.valueOf(WebSocketServerHandshaker.class, "HANDSHAKER");
   private final String websocketPath;
   private final String subprotocols;
   private final boolean allowExtensions;
   private final int maxFramePayloadLength;
   private final boolean allowMaskMismatch;
   private final boolean checkStartsWith;

   public WebSocketServerProtocolHandler(String var1) {
      this(var1, (String)null, false);
   }

   public WebSocketServerProtocolHandler(String var1, boolean var2) {
      this(var1, (String)null, false, 65536, false, var2);
   }

   public WebSocketServerProtocolHandler(String var1, String var2) {
      this(var1, var2, false);
   }

   public WebSocketServerProtocolHandler(String var1, String var2, boolean var3) {
      this(var1, var2, var3, 65536);
   }

   public WebSocketServerProtocolHandler(String var1, String var2, boolean var3, int var4) {
      this(var1, var2, var3, var4, false);
   }

   public WebSocketServerProtocolHandler(String var1, String var2, boolean var3, int var4, boolean var5) {
      this(var1, var2, var3, var4, var5, false);
   }

   public WebSocketServerProtocolHandler(String var1, String var2, boolean var3, int var4, boolean var5, boolean var6) {
      super();
      this.websocketPath = var1;
      this.subprotocols = var2;
      this.allowExtensions = var3;
      this.maxFramePayloadLength = var4;
      this.allowMaskMismatch = var5;
      this.checkStartsWith = var6;
   }

   public void handlerAdded(ChannelHandlerContext var1) {
      ChannelPipeline var2 = var1.pipeline();
      if (var2.get(WebSocketServerProtocolHandshakeHandler.class) == null) {
         var1.pipeline().addBefore(var1.name(), WebSocketServerProtocolHandshakeHandler.class.getName(), new WebSocketServerProtocolHandshakeHandler(this.websocketPath, this.subprotocols, this.allowExtensions, this.maxFramePayloadLength, this.allowMaskMismatch, this.checkStartsWith));
      }

      if (var2.get(Utf8FrameValidator.class) == null) {
         var1.pipeline().addBefore(var1.name(), Utf8FrameValidator.class.getName(), new Utf8FrameValidator());
      }

   }

   protected void decode(ChannelHandlerContext var1, WebSocketFrame var2, List<Object> var3) throws Exception {
      if (var2 instanceof CloseWebSocketFrame) {
         WebSocketServerHandshaker var4 = getHandshaker(var1.channel());
         if (var4 != null) {
            var2.retain();
            var4.close(var1.channel(), (CloseWebSocketFrame)var2);
         } else {
            var1.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
         }

      } else {
         super.decode(var1, var2, var3);
      }
   }

   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      if (var2 instanceof WebSocketHandshakeException) {
         DefaultFullHttpResponse var3 = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.wrappedBuffer(var2.getMessage().getBytes()));
         var1.channel().writeAndFlush(var3).addListener(ChannelFutureListener.CLOSE);
      } else {
         var1.fireExceptionCaught(var2);
         var1.close();
      }

   }

   static WebSocketServerHandshaker getHandshaker(Channel var0) {
      return (WebSocketServerHandshaker)var0.attr(HANDSHAKER_ATTR_KEY).get();
   }

   static void setHandshaker(Channel var0, WebSocketServerHandshaker var1) {
      var0.attr(HANDSHAKER_ATTR_KEY).set(var1);
   }

   static ChannelHandler forbiddenHttpRequestResponder() {
      return new ChannelInboundHandlerAdapter() {
         public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
            if (var2 instanceof FullHttpRequest) {
               ((FullHttpRequest)var2).release();
               DefaultFullHttpResponse var3 = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN);
               var1.channel().writeAndFlush(var3);
            } else {
               var1.fireChannelRead(var2);
            }

         }
      };
   }

   public static final class HandshakeComplete {
      private final String requestUri;
      private final HttpHeaders requestHeaders;
      private final String selectedSubprotocol;

      HandshakeComplete(String var1, HttpHeaders var2, String var3) {
         super();
         this.requestUri = var1;
         this.requestHeaders = var2;
         this.selectedSubprotocol = var3;
      }

      public String requestUri() {
         return this.requestUri;
      }

      public HttpHeaders requestHeaders() {
         return this.requestHeaders;
      }

      public String selectedSubprotocol() {
         return this.selectedSubprotocol;
      }
   }

   public static enum ServerHandshakeStateEvent {
      /** @deprecated */
      @Deprecated
      HANDSHAKE_COMPLETE;

      private ServerHandshakeStateEvent() {
      }
   }
}
