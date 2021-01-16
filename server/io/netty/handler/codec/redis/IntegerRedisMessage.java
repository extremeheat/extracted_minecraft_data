package io.netty.handler.codec.redis;

import io.netty.util.internal.StringUtil;

public final class IntegerRedisMessage implements RedisMessage {
   private final long value;

   public IntegerRedisMessage(long var1) {
      super();
      this.value = var1;
   }

   public long value() {
      return this.value;
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + '[' + "value=" + this.value + ']';
   }
}
