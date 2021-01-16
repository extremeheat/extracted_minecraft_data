package io.netty.handler.codec.redis;

public class BulkStringHeaderRedisMessage implements RedisMessage {
   private final int bulkStringLength;

   public BulkStringHeaderRedisMessage(int var1) {
      super();
      if (var1 <= 0) {
         throw new RedisCodecException("bulkStringLength: " + var1 + " (expected: > 0)");
      } else {
         this.bulkStringLength = var1;
      }
   }

   public final int bulkStringLength() {
      return this.bulkStringLength;
   }

   public boolean isNull() {
      return this.bulkStringLength == -1;
   }
}
