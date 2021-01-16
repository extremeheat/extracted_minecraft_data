package io.netty.handler.codec.redis;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageAggregator;

public final class RedisBulkStringAggregator extends MessageAggregator<RedisMessage, BulkStringHeaderRedisMessage, BulkStringRedisContent, FullBulkStringRedisMessage> {
   public RedisBulkStringAggregator() {
      super(536870912);
   }

   protected boolean isStartMessage(RedisMessage var1) throws Exception {
      return var1 instanceof BulkStringHeaderRedisMessage && !this.isAggregated(var1);
   }

   protected boolean isContentMessage(RedisMessage var1) throws Exception {
      return var1 instanceof BulkStringRedisContent;
   }

   protected boolean isLastContentMessage(BulkStringRedisContent var1) throws Exception {
      return var1 instanceof LastBulkStringRedisContent;
   }

   protected boolean isAggregated(RedisMessage var1) throws Exception {
      return var1 instanceof FullBulkStringRedisMessage;
   }

   protected boolean isContentLengthInvalid(BulkStringHeaderRedisMessage var1, int var2) throws Exception {
      return var1.bulkStringLength() > var2;
   }

   protected Object newContinueResponse(BulkStringHeaderRedisMessage var1, int var2, ChannelPipeline var3) throws Exception {
      return null;
   }

   protected boolean closeAfterContinueResponse(Object var1) throws Exception {
      throw new UnsupportedOperationException();
   }

   protected boolean ignoreContentAfterContinueResponse(Object var1) throws Exception {
      throw new UnsupportedOperationException();
   }

   protected FullBulkStringRedisMessage beginAggregation(BulkStringHeaderRedisMessage var1, ByteBuf var2) throws Exception {
      return new FullBulkStringRedisMessage(var2);
   }
}
