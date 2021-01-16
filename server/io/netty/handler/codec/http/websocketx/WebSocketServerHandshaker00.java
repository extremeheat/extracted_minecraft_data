package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.regex.Pattern;

public class WebSocketServerHandshaker00 extends WebSocketServerHandshaker {
   private static final Pattern BEGINNING_DIGIT = Pattern.compile("[^0-9]");
   private static final Pattern BEGINNING_SPACE = Pattern.compile("[^ ]");

   public WebSocketServerHandshaker00(String var1, String var2, int var3) {
      super(WebSocketVersion.V00, var1, var2, var3);
   }

   protected FullHttpResponse newHandshakeResponse(FullHttpRequest var1, HttpHeaders var2) {
      if (var1.headers().containsValue(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true) && HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(var1.headers().get((CharSequence)HttpHeaderNames.UPGRADE))) {
         boolean var3 = var1.headers().contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY1) && var1.headers().contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY2);
         DefaultFullHttpResponse var4 = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, new HttpResponseStatus(101, var3 ? "WebSocket Protocol Handshake" : "Web Socket Protocol Handshake"));
         if (var2 != null) {
            var4.headers().add(var2);
         }

         var4.headers().add((CharSequence)HttpHeaderNames.UPGRADE, (Object)HttpHeaderValues.WEBSOCKET);
         var4.headers().add((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.UPGRADE);
         String var5;
         if (var3) {
            var4.headers().add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ORIGIN, (Object)var1.headers().get((CharSequence)HttpHeaderNames.ORIGIN));
            var4.headers().add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_LOCATION, (Object)this.uri());
            var5 = var1.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
            String var6;
            if (var5 != null) {
               var6 = this.selectSubprotocol(var5);
               if (var6 == null) {
                  if (logger.isDebugEnabled()) {
                     logger.debug("Requested subprotocol(s) not supported: {}", (Object)var5);
                  }
               } else {
                  var4.headers().add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, (Object)var6);
               }
            }

            var6 = var1.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY1);
            String var7 = var1.headers().get((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY2);
            int var8 = (int)(Long.parseLong(BEGINNING_DIGIT.matcher(var6).replaceAll("")) / (long)BEGINNING_SPACE.matcher(var6).replaceAll("").length());
            int var9 = (int)(Long.parseLong(BEGINNING_DIGIT.matcher(var7).replaceAll("")) / (long)BEGINNING_SPACE.matcher(var7).replaceAll("").length());
            long var10 = var1.content().readLong();
            ByteBuf var12 = Unpooled.buffer(16);
            var12.writeInt(var8);
            var12.writeInt(var9);
            var12.writeLong(var10);
            var4.content().writeBytes(WebSocketUtil.md5(var12.array()));
         } else {
            var4.headers().add((CharSequence)HttpHeaderNames.WEBSOCKET_ORIGIN, (Object)var1.headers().get((CharSequence)HttpHeaderNames.ORIGIN));
            var4.headers().add((CharSequence)HttpHeaderNames.WEBSOCKET_LOCATION, (Object)this.uri());
            var5 = var1.headers().get((CharSequence)HttpHeaderNames.WEBSOCKET_PROTOCOL);
            if (var5 != null) {
               var4.headers().add((CharSequence)HttpHeaderNames.WEBSOCKET_PROTOCOL, (Object)this.selectSubprotocol(var5));
            }
         }

         return var4;
      } else {
         throw new WebSocketHandshakeException("not a WebSocket handshake request: missing upgrade");
      }
   }

   public ChannelFuture close(Channel var1, CloseWebSocketFrame var2, ChannelPromise var3) {
      return var1.writeAndFlush(var2, var3);
   }

   protected WebSocketFrameDecoder newWebsocketDecoder() {
      return new WebSocket00FrameDecoder(this.maxFramePayloadLength());
   }

   protected WebSocketFrameEncoder newWebSocketEncoder() {
      return new WebSocket00FrameEncoder();
   }
}
