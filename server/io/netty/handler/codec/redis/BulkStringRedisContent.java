package io.netty.handler.codec.redis;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

public interface BulkStringRedisContent extends RedisMessage, ByteBufHolder {
   BulkStringRedisContent copy();

   BulkStringRedisContent duplicate();

   BulkStringRedisContent retainedDuplicate();

   BulkStringRedisContent replace(ByteBuf var1);

   BulkStringRedisContent retain();

   BulkStringRedisContent retain(int var1);

   BulkStringRedisContent touch();

   BulkStringRedisContent touch(Object var1);
}
