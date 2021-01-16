package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class WebSocketServerHandshaker08 extends WebSocketServerHandshaker {
   public static final String WEBSOCKET_08_ACCEPT_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
   private final boolean allowExtensions;
   private final boolean allowMaskMismatch;

   public WebSocketServerHandshaker08(String var1, String var2, boolean var3, int var4) {
      this(var1, var2, var3, var4, false);
   }

   public WebSocketServerHandshaker08(String var1, String var2, boolean var3, int var4, boolean var5) {
      super(WebSocketVersion.V08, var1, var2, var4);
      this.allowExtensions = var3;
      this.allowMaskMismatch = var5;
   }

   protected FullHttpResponse newHandshakeResponse(FullHttpRequest var1, HttpHeaders var2) {
      DefaultFullHttpResponse var3 = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.SWITCHING_PROTOCOLS);
      if (var2 != null) {
         var3.headers().add(var2);
      }

      String var4 = var1.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY);
      if (var4 == null) {
         throw new WebSocketHandshakeException("not a WebSocket request: missing key");
      } else {
         String var5 = var4 + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
         byte[] var6 = WebSocketUtil.sha1(var5.getBytes(CharsetUtil.US_ASCII));
         String var7 = WebSocketUtil.base64(var6);
         if (logger.isDebugEnabled()) {
            logger.debug("WebSocket version 08 server handshake key: {}, response: {}", var4, var7);
         }

         var3.headers().add((CharSequence)HttpHeaderNames.UPGRADE, (Object)HttpHeaderValues.WEBSOCKET);
         var3.headers().add((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.UPGRADE);
         var3.headers().add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ACCEPT, (Object)var7);
         String var8 = var1.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
         if (var8 != null) {
            String var9 = this.selectSubprotocol(var8);
            if (var9 == null) {
               if (logger.isDebugEnabled()) {
                  logger.debug("Requested subprotocol(s) not supported: {}", (Object)var8);
               }
            } else {
               var3.headers().add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, (Object)var9);
            }
         }

         return var3;
      }
   }

   protected WebSocketFrameDecoder newWebsocketDecoder() {
      return new WebSocket08FrameDecoder(true, this.allowExtensions, this.maxFramePayloadLength(), this.allowMaskMismatch);
   }

   protected WebSocketFrameEncoder newWebSocketEncoder() {
      return new WebSocket08FrameEncoder(false);
   }
}
