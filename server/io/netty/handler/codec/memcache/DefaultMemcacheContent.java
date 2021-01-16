package io.netty.handler.codec.memcache;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;

public class DefaultMemcacheContent extends AbstractMemcacheObject implements MemcacheContent {
   private final ByteBuf content;

   public DefaultMemcacheContent(ByteBuf var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("Content cannot be null.");
      } else {
         this.content = var1;
      }
   }

   public ByteBuf content() {
      return this.content;
   }

   public MemcacheContent copy() {
      return this.replace(this.content.copy());
   }

   public MemcacheContent duplicate() {
      return this.replace(this.content.duplicate());
   }

   public MemcacheContent retainedDuplicate() {
      return this.replace(this.content.retainedDuplicate());
   }

   public MemcacheContent replace(ByteBuf var1) {
      return new DefaultMemcacheContent(var1);
   }

   public MemcacheContent retain() {
      super.retain();
      return this;
   }

   public MemcacheContent retain(int var1) {
      super.retain(var1);
      return this;
   }

   public MemcacheContent touch() {
      super.touch();
      return this;
   }

   public MemcacheContent touch(Object var1) {
      this.content.touch(var1);
      return this;
   }

   protected void deallocate() {
      this.content.release();
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + "(data: " + this.content() + ", decoderResult: " + this.decoderResult() + ')';
   }
}
