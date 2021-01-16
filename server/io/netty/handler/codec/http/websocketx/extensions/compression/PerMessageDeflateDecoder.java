package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import java.util.List;

class PerMessageDeflateDecoder extends DeflateDecoder {
   private boolean compressing;

   public PerMessageDeflateDecoder(boolean var1) {
      super(var1);
   }

   public boolean acceptInboundMessage(Object var1) throws Exception {
      return (var1 instanceof TextWebSocketFrame || var1 instanceof BinaryWebSocketFrame) && (((WebSocketFrame)var1).rsv() & 4) > 0 || var1 instanceof ContinuationWebSocketFrame && this.compressing;
   }

   protected int newRsv(WebSocketFrame var1) {
      return (var1.rsv() & 4) > 0 ? var1.rsv() ^ 4 : var1.rsv();
   }

   protected boolean appendFrameTail(WebSocketFrame var1) {
      return var1.isFinalFragment();
   }

   protected void decode(ChannelHandlerContext var1, WebSocketFrame var2, List<Object> var3) throws Exception {
      super.decode(var1, var2, var3);
      if (var2.isFinalFragment()) {
         this.compressing = false;
      } else if (var2 instanceof TextWebSocketFrame || var2 instanceof BinaryWebSocketFrame) {
         this.compressing = true;
      }

   }
}
