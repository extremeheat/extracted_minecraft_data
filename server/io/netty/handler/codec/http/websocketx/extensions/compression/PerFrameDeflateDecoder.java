package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

class PerFrameDeflateDecoder extends DeflateDecoder {
   public PerFrameDeflateDecoder(boolean var1) {
      super(var1);
   }

   public boolean acceptInboundMessage(Object var1) throws Exception {
      return (var1 instanceof TextWebSocketFrame || var1 instanceof BinaryWebSocketFrame || var1 instanceof ContinuationWebSocketFrame) && (((WebSocketFrame)var1).rsv() & 4) > 0;
   }

   protected int newRsv(WebSocketFrame var1) {
      return var1.rsv() ^ 4;
   }

   protected boolean appendFrameTail(WebSocketFrame var1) {
      return true;
   }
}
