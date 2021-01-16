package io.netty.handler.codec.stomp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageAggregator;

public class StompSubframeAggregator extends MessageAggregator<StompSubframe, StompHeadersSubframe, StompContentSubframe, StompFrame> {
   public StompSubframeAggregator(int var1) {
      super(var1);
   }

   protected boolean isStartMessage(StompSubframe var1) throws Exception {
      return var1 instanceof StompHeadersSubframe;
   }

   protected boolean isContentMessage(StompSubframe var1) throws Exception {
      return var1 instanceof StompContentSubframe;
   }

   protected boolean isLastContentMessage(StompContentSubframe var1) throws Exception {
      return var1 instanceof LastStompContentSubframe;
   }

   protected boolean isAggregated(StompSubframe var1) throws Exception {
      return var1 instanceof StompFrame;
   }

   protected boolean isContentLengthInvalid(StompHeadersSubframe var1, int var2) {
      return (int)Math.min(2147483647L, var1.headers().getLong(StompHeaders.CONTENT_LENGTH, -1L)) > var2;
   }

   protected Object newContinueResponse(StompHeadersSubframe var1, int var2, ChannelPipeline var3) {
      return null;
   }

   protected boolean closeAfterContinueResponse(Object var1) throws Exception {
      throw new UnsupportedOperationException();
   }

   protected boolean ignoreContentAfterContinueResponse(Object var1) throws Exception {
      throw new UnsupportedOperationException();
   }

   protected StompFrame beginAggregation(StompHeadersSubframe var1, ByteBuf var2) throws Exception {
      DefaultStompFrame var3 = new DefaultStompFrame(var1.command(), var2);
      var3.headers().set(var1.headers());
      return var3;
   }
}
