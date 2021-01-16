package io.netty.handler.codec.memcache;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class DefaultLastMemcacheContent extends DefaultMemcacheContent implements LastMemcacheContent {
   public DefaultLastMemcacheContent() {
      super(Unpooled.buffer());
   }

   public DefaultLastMemcacheContent(ByteBuf var1) {
      super(var1);
   }

   public LastMemcacheContent retain() {
      super.retain();
      return this;
   }

   public LastMemcacheContent retain(int var1) {
      super.retain(var1);
      return this;
   }

   public LastMemcacheContent touch() {
      super.touch();
      return this;
   }

   public LastMemcacheContent touch(Object var1) {
      super.touch(var1);
      return this;
   }

   public LastMemcacheContent copy() {
      return this.replace(this.content().copy());
   }

   public LastMemcacheContent duplicate() {
      return this.replace(this.content().duplicate());
   }

   public LastMemcacheContent retainedDuplicate() {
      return this.replace(this.content().retainedDuplicate());
   }

   public LastMemcacheContent replace(ByteBuf var1) {
      return new DefaultLastMemcacheContent(var1);
   }
}
