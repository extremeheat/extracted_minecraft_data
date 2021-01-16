package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageAggregator;

public class WebSocketFrameAggregator extends MessageAggregator<WebSocketFrame, WebSocketFrame, ContinuationWebSocketFrame, WebSocketFrame> {
   public WebSocketFrameAggregator(int var1) {
      super(var1);
   }

   protected boolean isStartMessage(WebSocketFrame var1) throws Exception {
      return var1 instanceof TextWebSocketFrame || var1 instanceof BinaryWebSocketFrame;
   }

   protected boolean isContentMessage(WebSocketFrame var1) throws Exception {
      return var1 instanceof ContinuationWebSocketFrame;
   }

   protected boolean isLastContentMessage(ContinuationWebSocketFrame var1) throws Exception {
      return this.isContentMessage((WebSocketFrame)var1) && var1.isFinalFragment();
   }

   protected boolean isAggregated(WebSocketFrame var1) throws Exception {
      if (var1.isFinalFragment()) {
         return !this.isContentMessage(var1);
      } else {
         return !this.isStartMessage(var1) && !this.isContentMessage(var1);
      }
   }

   protected boolean isContentLengthInvalid(WebSocketFrame var1, int var2) {
      return false;
   }

   protected Object newContinueResponse(WebSocketFrame var1, int var2, ChannelPipeline var3) {
      return null;
   }

   protected boolean closeAfterContinueResponse(Object var1) throws Exception {
      throw new UnsupportedOperationException();
   }

   protected boolean ignoreContentAfterContinueResponse(Object var1) throws Exception {
      throw new UnsupportedOperationException();
   }

   protected WebSocketFrame beginAggregation(WebSocketFrame var1, ByteBuf var2) throws Exception {
      if (var1 instanceof TextWebSocketFrame) {
         return new TextWebSocketFrame(true, var1.rsv(), var2);
      } else if (var1 instanceof BinaryWebSocketFrame) {
         return new BinaryWebSocketFrame(true, var1.rsv(), var2);
      } else {
         throw new Error();
      }
   }
}
