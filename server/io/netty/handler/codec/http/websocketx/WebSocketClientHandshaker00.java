package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;
import java.net.URI;
import java.nio.ByteBuffer;

public class WebSocketClientHandshaker00 extends WebSocketClientHandshaker {
   private static final AsciiString WEBSOCKET = AsciiString.cached("WebSocket");
   private ByteBuf expectedChallengeResponseBytes;

   public WebSocketClientHandshaker00(URI var1, WebSocketVersion var2, String var3, HttpHeaders var4, int var5) {
      super(var1, var2, var3, var4, var5);
   }

   protected FullHttpRequest newHandshakeRequest() {
      int var1 = WebSocketUtil.randomNumber(1, 12);
      int var2 = WebSocketUtil.randomNumber(1, 12);
      int var3 = 2147483647 / var1;
      int var4 = 2147483647 / var2;
      int var5 = WebSocketUtil.randomNumber(0, var3);
      int var6 = WebSocketUtil.randomNumber(0, var4);
      int var7 = var5 * var1;
      int var8 = var6 * var2;
      String var9 = Integer.toString(var7);
      String var10 = Integer.toString(var8);
      var9 = insertRandomCharacters(var9);
      var10 = insertRandomCharacters(var10);
      var9 = insertSpaces(var9, var1);
      var10 = insertSpaces(var10, var2);
      byte[] var11 = WebSocketUtil.randomBytes(8);
      ByteBuffer var12 = ByteBuffer.allocate(4);
      var12.putInt(var5);
      byte[] var13 = var12.array();
      var12 = ByteBuffer.allocate(4);
      var12.putInt(var6);
      byte[] var14 = var12.array();
      byte[] var15 = new byte[16];
      System.arraycopy(var13, 0, var15, 0, 4);
      System.arraycopy(var14, 0, var15, 4, 4);
      System.arraycopy(var11, 0, var15, 8, 8);
      this.expectedChallengeResponseBytes = Unpooled.wrappedBuffer(WebSocketUtil.md5(var15));
      URI var16 = this.uri();
      String var17 = rawPath(var16);
      DefaultFullHttpRequest var18 = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, var17);
      HttpHeaders var19 = var18.headers();
      var19.add((CharSequence)HttpHeaderNames.UPGRADE, (Object)WEBSOCKET).add((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.UPGRADE).add((CharSequence)HttpHeaderNames.HOST, (Object)websocketHostValue(var16)).add((CharSequence)HttpHeaderNames.ORIGIN, (Object)websocketOriginValue(var16)).add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY1, (Object)var9).add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY2, (Object)var10);
      String var20 = this.expectedSubprotocol();
      if (var20 != null && !var20.isEmpty()) {
         var19.add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, (Object)var20);
      }

      if (this.customHeaders != null) {
         var19.add(this.customHeaders);
      }

      var19.set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)var11.length);
      var18.content().writeBytes(var11);
      return var18;
   }

   protected void verify(FullHttpResponse var1) {
      if (!var1.status().equals(HttpResponseStatus.SWITCHING_PROTOCOLS)) {
         throw new WebSocketHandshakeException("Invalid handshake response getStatus: " + var1.status());
      } else {
         HttpHeaders var2 = var1.headers();
         String var3 = var2.get((CharSequence)HttpHeaderNames.UPGRADE);
         if (!WEBSOCKET.contentEqualsIgnoreCase(var3)) {
            throw new WebSocketHandshakeException("Invalid handshake response upgrade: " + var3);
         } else if (!var2.containsValue(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true)) {
            throw new WebSocketHandshakeException("Invalid handshake response connection: " + var2.get((CharSequence)HttpHeaderNames.CONNECTION));
         } else {
            ByteBuf var4 = var1.content();
            if (!var4.equals(this.expectedChallengeResponseBytes)) {
               throw new WebSocketHandshakeException("Invalid challenge");
            }
         }
      }
   }

   private static String insertRandomCharacters(String var0) {
      int var1 = WebSocketUtil.randomNumber(1, 12);
      char[] var2 = new char[var1];
      int var3 = 0;

      while(true) {
         int var4;
         do {
            if (var3 >= var1) {
               for(var4 = 0; var4 < var1; ++var4) {
                  int var5 = WebSocketUtil.randomNumber(0, var0.length());
                  String var6 = var0.substring(0, var5);
                  String var7 = var0.substring(var5);
                  var0 = var6 + var2[var4] + var7;
               }

               return var0;
            }

            var4 = (int)(Math.random() * 126.0D + 33.0D);
         } while((33 >= var4 || var4 >= 47) && (58 >= var4 || var4 >= 126));

         var2[var3] = (char)var4;
         ++var3;
      }
   }

   private static String insertSpaces(String var0, int var1) {
      for(int var2 = 0; var2 < var1; ++var2) {
         int var3 = WebSocketUtil.randomNumber(1, var0.length() - 1);
         String var4 = var0.substring(0, var3);
         String var5 = var0.substring(var3);
         var0 = var4 + ' ' + var5;
      }

      return var0;
   }

   protected WebSocketFrameDecoder newWebsocketDecoder() {
      return new WebSocket00FrameDecoder(this.maxFramePayloadLength());
   }

   protected WebSocketFrameEncoder newWebSocketEncoder() {
      return new WebSocket00FrameEncoder();
   }
}
