package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpHeaders;
import java.net.URI;
import java.util.List;

public class WebSocketClientProtocolHandler extends WebSocketProtocolHandler {
   private final WebSocketClientHandshaker handshaker;
   private final boolean handleCloseFrames;

   public WebSocketClientHandshaker handshaker() {
      return this.handshaker;
   }

   public WebSocketClientProtocolHandler(URI var1, WebSocketVersion var2, String var3, boolean var4, HttpHeaders var5, int var6, boolean var7, boolean var8, boolean var9) {
      this(WebSocketClientHandshakerFactory.newHandshaker(var1, var2, var3, var4, var5, var6, var8, var9), var7);
   }

   public WebSocketClientProtocolHandler(URI var1, WebSocketVersion var2, String var3, boolean var4, HttpHeaders var5, int var6, boolean var7) {
      this(var1, var2, var3, var4, var5, var6, var7, true, false);
   }

   public WebSocketClientProtocolHandler(URI var1, WebSocketVersion var2, String var3, boolean var4, HttpHeaders var5, int var6) {
      this(var1, var2, var3, var4, var5, var6, true);
   }

   public WebSocketClientProtocolHandler(WebSocketClientHandshaker var1, boolean var2) {
      super();
      this.handshaker = var1;
      this.handleCloseFrames = var2;
   }

   public WebSocketClientProtocolHandler(WebSocketClientHandshaker var1) {
      this(var1, true);
   }

   protected void decode(ChannelHandlerContext var1, WebSocketFrame var2, List<Object> var3) throws Exception {
      if (this.handleCloseFrames && var2 instanceof CloseWebSocketFrame) {
         var1.close();
      } else {
         super.decode(var1, var2, var3);
      }
   }

   public void handlerAdded(ChannelHandlerContext var1) {
      ChannelPipeline var2 = var1.pipeline();
      if (var2.get(WebSocketClientProtocolHandshakeHandler.class) == null) {
         var1.pipeline().addBefore(var1.name(), WebSocketClientProtocolHandshakeHandler.class.getName(), new WebSocketClientProtocolHandshakeHandler(this.handshaker));
      }

      if (var2.get(Utf8FrameValidator.class) == null) {
         var1.pipeline().addBefore(var1.name(), Utf8FrameValidator.class.getName(), new Utf8FrameValidator());
      }

   }

   public static enum ClientHandshakeStateEvent {
      HANDSHAKE_ISSUED,
      HANDSHAKE_COMPLETE;

      private ClientHandshakeStateEvent() {
      }
   }
}
