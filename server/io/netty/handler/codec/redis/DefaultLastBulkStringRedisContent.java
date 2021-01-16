package io.netty.handler.codec.redis;

import io.netty.buffer.ByteBuf;

public final class DefaultLastBulkStringRedisContent extends DefaultBulkStringRedisContent implements LastBulkStringRedisContent {
   public DefaultLastBulkStringRedisContent(ByteBuf var1) {
      super(var1);
   }

   public LastBulkStringRedisContent copy() {
      return (LastBulkStringRedisContent)super.copy();
   }

   public LastBulkStringRedisContent duplicate() {
      return (LastBulkStringRedisContent)super.duplicate();
   }

   public LastBulkStringRedisContent retainedDuplicate() {
      return (LastBulkStringRedisContent)super.retainedDuplicate();
   }

   public LastBulkStringRedisContent replace(ByteBuf var1) {
      return new DefaultLastBulkStringRedisContent(var1);
   }

   public LastBulkStringRedisContent retain() {
      super.retain();
      return this;
   }

   public LastBulkStringRedisContent retain(int var1) {
      super.retain(var1);
      return this;
   }

   public LastBulkStringRedisContent touch() {
      super.touch();
      return this;
   }

   public LastBulkStringRedisContent touch(Object var1) {
      super.touch(var1);
      return this;
   }
}
