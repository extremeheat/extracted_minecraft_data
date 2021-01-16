package io.netty.handler.codec.redis;

import io.netty.buffer.ByteBuf;

public interface RedisMessagePool {
   SimpleStringRedisMessage getSimpleString(String var1);

   SimpleStringRedisMessage getSimpleString(ByteBuf var1);

   ErrorRedisMessage getError(String var1);

   ErrorRedisMessage getError(ByteBuf var1);

   IntegerRedisMessage getInteger(long var1);

   IntegerRedisMessage getInteger(ByteBuf var1);

   byte[] getByteBufOfInteger(long var1);
}
