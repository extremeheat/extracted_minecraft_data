package io.netty.handler.codec.redis;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public interface LastBulkStringRedisContent extends BulkStringRedisContent {
   LastBulkStringRedisContent EMPTY_LAST_CONTENT = new LastBulkStringRedisContent() {
      public ByteBuf content() {
         return Unpooled.EMPTY_BUFFER;
      }

      public LastBulkStringRedisContent copy() {
         return this;
      }

      public LastBulkStringRedisContent duplicate() {
         return this;
      }

      public LastBulkStringRedisContent retainedDuplicate() {
         return this;
      }

      public LastBulkStringRedisContent replace(ByteBuf var1) {
         return new DefaultLastBulkStringRedisContent(var1);
      }

      public LastBulkStringRedisContent retain(int var1) {
         return this;
      }

      public LastBulkStringRedisContent retain() {
         return this;
      }

      public int refCnt() {
         return 1;
      }

      public LastBulkStringRedisContent touch() {
         return this;
      }

      public LastBulkStringRedisContent touch(Object var1) {
         return this;
      }

      public boolean release() {
         return false;
      }

      public boolean release(int var1) {
         return false;
      }
   };

   LastBulkStringRedisContent copy();

   LastBulkStringRedisContent duplicate();

   LastBulkStringRedisContent retainedDuplicate();

   LastBulkStringRedisContent replace(ByteBuf var1);

   LastBulkStringRedisContent retain();

   LastBulkStringRedisContent retain(int var1);

   LastBulkStringRedisContent touch();

   LastBulkStringRedisContent touch(Object var1);
}
