package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.URI;

public class WebSocketClientHandshaker07 extends WebSocketClientHandshaker {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocketClientHandshaker07.class);
   public static final String MAGIC_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
   private String expectedChallengeResponseString;
   private final boolean allowExtensions;
   private final boolean performMasking;
   private final boolean allowMaskMismatch;

   public WebSocketClientHandshaker07(URI var1, WebSocketVersion var2, String var3, boolean var4, HttpHeaders var5, int var6) {
      this(var1, var2, var3, var4, var5, var6, true, false);
   }

   public WebSocketClientHandshaker07(URI var1, WebSocketVersion var2, String var3, boolean var4, HttpHeaders var5, int var6, boolean var7, boolean var8) {
      super(var1, var2, var3, var5, var6);
      this.allowExtensions = var4;
      this.performMasking = var7;
      this.allowMaskMismatch = var8;
   }

   protected FullHttpRequest newHandshakeRequest() {
      URI var1 = this.uri();
      String var2 = rawPath(var1);
      byte[] var3 = WebSocketUtil.randomBytes(16);
      String var4 = WebSocketUtil.base64(var3);
      String var5 = var4 + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
      byte[] var6 = WebSocketUtil.sha1(var5.getBytes(CharsetUtil.US_ASCII));
      this.expectedChallengeResponseString = WebSocketUtil.base64(var6);
      if (logger.isDebugEnabled()) {
         logger.debug("WebSocket version 07 client handshake key: {}, expected response: {}", var4, this.expectedChallengeResponseString);
      }

      DefaultFullHttpRequest var7 = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, var2);
      HttpHeaders var8 = var7.headers();
      var8.add((CharSequence)HttpHeaderNames.UPGRADE, (Object)HttpHeaderValues.WEBSOCKET).add((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.UPGRADE).add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY, (Object)var4).add((CharSequence)HttpHeaderNames.HOST, (Object)websocketHostValue(var1)).add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ORIGIN, (Object)websocketOriginValue(var1));
      String var9 = this.expectedSubprotocol();
      if (var9 != null && !var9.isEmpty()) {
         var8.add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, (Object)var9);
      }

      var8.add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_VERSION, (Object)"7");
      if (this.customHeaders != null) {
         var8.add(this.customHeaders);
      }

      return var7;
   }

   protected void verify(FullHttpResponse var1) {
      HttpResponseStatus var2 = HttpResponseStatus.SWITCHING_PROTOCOLS;
      HttpHeaders var3 = var1.headers();
      if (!var1.status().equals(var2)) {
         throw new WebSocketHandshakeException("Invalid handshake response getStatus: " + var1.status());
      } else {
         String var4 = var3.get((CharSequence)HttpHeaderNames.UPGRADE);
         if (!HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(var4)) {
            throw new WebSocketHandshakeException("Invalid handshake response upgrade: " + var4);
         } else if (!var3.containsValue(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true)) {
            throw new WebSocketHandshakeException("Invalid handshake response connection: " + var3.get((CharSequence)HttpHeaderNames.CONNECTION));
         } else {
            String var5 = var3.get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ACCEPT);
            if (var5 == null || !var5.equals(this.expectedChallengeResponseString)) {
               throw new WebSocketHandshakeException(String.format("Invalid challenge. Actual: %s. Expected: %s", var5, this.expectedChallengeResponseString));
            }
         }
      }
   }

   protected WebSocketFrameDecoder newWebsocketDecoder() {
      return new WebSocket07FrameDecoder(false, this.allowExtensions, this.maxFramePayloadLength(), this.allowMaskMismatch);
   }

   protected WebSocketFrameEncoder newWebSocketEncoder() {
      return new WebSocket07FrameEncoder(this.performMasking);
   }
}
