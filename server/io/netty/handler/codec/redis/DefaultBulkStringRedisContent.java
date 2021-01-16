package io.netty.handler.codec.redis;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.util.internal.StringUtil;

public class DefaultBulkStringRedisContent extends DefaultByteBufHolder implements BulkStringRedisContent {
   public DefaultBulkStringRedisContent(ByteBuf var1) {
      super(var1);
   }

   public BulkStringRedisContent copy() {
      return (BulkStringRedisContent)super.copy();
   }

   public BulkStringRedisContent duplicate() {
      return (BulkStringRedisContent)super.duplicate();
   }

   public BulkStringRedisContent retainedDuplicate() {
      return (BulkStringRedisContent)super.retainedDuplicate();
   }

   public BulkStringRedisContent replace(ByteBuf var1) {
      return new DefaultBulkStringRedisContent(var1);
   }

   public BulkStringRedisContent retain() {
      super.retain();
      return this;
   }

   public BulkStringRedisContent retain(int var1) {
      super.retain(var1);
      return this;
   }

   public BulkStringRedisContent touch() {
      super.touch();
      return this;
   }

   public BulkStringRedisContent touch(Object var1) {
      super.touch(var1);
      return this;
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + '[' + "content=" + this.content() + ']';
   }
}
