package io.netty.handler.codec.memcache;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageAggregator;

public abstract class AbstractMemcacheObjectAggregator<H extends MemcacheMessage> extends MessageAggregator<MemcacheObject, H, MemcacheContent, FullMemcacheMessage> {
   protected AbstractMemcacheObjectAggregator(int var1) {
      super(var1);
   }

   protected boolean isContentMessage(MemcacheObject var1) throws Exception {
      return var1 instanceof MemcacheContent;
   }

   protected boolean isLastContentMessage(MemcacheContent var1) throws Exception {
      return var1 instanceof LastMemcacheContent;
   }

   protected boolean isAggregated(MemcacheObject var1) throws Exception {
      return var1 instanceof FullMemcacheMessage;
   }

   protected boolean isContentLengthInvalid(H var1, int var2) {
      return false;
   }

   protected Object newContinueResponse(H var1, int var2, ChannelPipeline var3) {
      return null;
   }

   protected boolean closeAfterContinueResponse(Object var1) throws Exception {
      throw new UnsupportedOperationException();
   }

   protected boolean ignoreContentAfterContinueResponse(Object var1) throws Exception {
      throw new UnsupportedOperationException();
   }
}
