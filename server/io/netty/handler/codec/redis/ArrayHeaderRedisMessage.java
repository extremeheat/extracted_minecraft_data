package io.netty.handler.codec.redis;

import io.netty.util.internal.StringUtil;

public class ArrayHeaderRedisMessage implements RedisMessage {
   private final long length;

   public ArrayHeaderRedisMessage(long var1) {
      super();
      if (var1 < -1L) {
         throw new RedisCodecException("length: " + var1 + " (expected: >= " + -1 + ")");
      } else {
         this.length = var1;
      }
   }

   public final long length() {
      return this.length;
   }

   public boolean isNull() {
      return this.length == -1L;
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + '[' + "length=" + this.length + ']';
   }
}
