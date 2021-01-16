package io.netty.handler.codec.http.websocketx;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;

public class WebSocketServerHandshakerFactory {
   private final String webSocketURL;
   private final String subprotocols;
   private final boolean allowExtensions;
   private final int maxFramePayloadLength;
   private final boolean allowMaskMismatch;

   public WebSocketServerHandshakerFactory(String var1, String var2, boolean var3) {
      this(var1, var2, var3, 65536);
   }

   public WebSocketServerHandshakerFactory(String var1, String var2, boolean var3, int var4) {
      this(var1, var2, var3, var4, false);
   }

   public WebSocketServerHandshakerFactory(String var1, String var2, boolean var3, int var4, boolean var5) {
      super();
      this.webSocketURL = var1;
      this.subprotocols = var2;
      this.allowExtensions = var3;
      this.maxFramePayloadLength = var4;
      this.allowMaskMismatch = var5;
   }

   public WebSocketServerHandshaker newHandshaker(HttpRequest var1) {
      String var2 = var1.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_VERSION);
      if (var2 != null) {
         if (var2.equals(WebSocketVersion.V13.toHttpHeaderValue())) {
            return new WebSocketServerHandshaker13(this.webSocketURL, this.subprotocols, this.allowExtensions, this.maxFramePayloadLength, this.allowMaskMismatch);
         } else if (var2.equals(WebSocketVersion.V08.toHttpHeaderValue())) {
            return new WebSocketServerHandshaker08(this.webSocketURL, this.subprotocols, this.allowExtensions, this.maxFramePayloadLength, this.allowMaskMismatch);
         } else {
            return var2.equals(WebSocketVersion.V07.toHttpHeaderValue()) ? new WebSocketServerHandshaker07(this.webSocketURL, this.subprotocols, this.allowExtensions, this.maxFramePayloadLength, this.allowMaskMismatch) : null;
         }
      } else {
         return new WebSocketServerHandshaker00(this.webSocketURL, this.subprotocols, this.maxFramePayloadLength);
      }
   }

   /** @deprecated */
   @Deprecated
   public static void sendUnsupportedWebSocketVersionResponse(Channel var0) {
      sendUnsupportedVersionResponse(var0);
   }

   public static ChannelFuture sendUnsupportedVersionResponse(Channel var0) {
      return sendUnsupportedVersionResponse(var0, var0.newPromise());
   }

   public static ChannelFuture sendUnsupportedVersionResponse(Channel var0, ChannelPromise var1) {
      DefaultFullHttpResponse var2 = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UPGRADE_REQUIRED);
      var2.headers().set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_VERSION, (Object)WebSocketVersion.V13.toHttpHeaderValue());
      HttpUtil.setContentLength(var2, 0L);
      return var0.writeAndFlush(var2, var1);
   }
}
