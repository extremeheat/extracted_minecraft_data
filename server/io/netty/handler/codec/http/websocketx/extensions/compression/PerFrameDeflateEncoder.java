package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

class PerFrameDeflateEncoder extends DeflateEncoder {
   public PerFrameDeflateEncoder(int var1, int var2, boolean var3) {
      super(var1, var2, var3);
   }

   public boolean acceptOutboundMessage(Object var1) throws Exception {
      return (var1 instanceof TextWebSocketFrame || var1 instanceof BinaryWebSocketFrame || var1 instanceof ContinuationWebSocketFrame) && ((WebSocketFrame)var1).content().readableBytes() > 0 && (((WebSocketFrame)var1).rsv() & 4) == 0;
   }

   protected int rsv(WebSocketFrame var1) {
      return var1.rsv() | 4;
   }

   protected boolean removeFrameTail(WebSocketFrame var1) {
      return true;
   }
}
