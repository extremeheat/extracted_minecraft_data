package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import java.util.List;

class PerMessageDeflateEncoder extends DeflateEncoder {
   private boolean compressing;

   public PerMessageDeflateEncoder(int var1, int var2, boolean var3) {
      super(var1, var2, var3);
   }

   public boolean acceptOutboundMessage(Object var1) throws Exception {
      return (var1 instanceof TextWebSocketFrame || var1 instanceof BinaryWebSocketFrame) && (((WebSocketFrame)var1).rsv() & 4) == 0 || var1 instanceof ContinuationWebSocketFrame && this.compressing;
   }

   protected int rsv(WebSocketFrame var1) {
      return !(var1 instanceof TextWebSocketFrame) && !(var1 instanceof BinaryWebSocketFrame) ? var1.rsv() : var1.rsv() | 4;
   }

   protected boolean removeFrameTail(WebSocketFrame var1) {
      return var1.isFinalFragment();
   }

   protected void encode(ChannelHandlerContext var1, WebSocketFrame var2, List<Object> var3) throws Exception {
      super.encode(var1, var2, var3);
      if (var2.isFinalFragment()) {
         this.compressing = false;
      } else if (var2 instanceof TextWebSocketFrame || var2 instanceof BinaryWebSocketFrame) {
         this.compressing = true;
      }

   }
}
