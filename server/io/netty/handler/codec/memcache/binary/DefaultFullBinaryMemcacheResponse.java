package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class DefaultFullBinaryMemcacheResponse extends DefaultBinaryMemcacheResponse implements FullBinaryMemcacheResponse {
   private final ByteBuf content;

   public DefaultFullBinaryMemcacheResponse(ByteBuf var1, ByteBuf var2) {
      this(var1, var2, Unpooled.buffer(0));
   }

   public DefaultFullBinaryMemcacheResponse(ByteBuf var1, ByteBuf var2, ByteBuf var3) {
      super(var1, var2);
      if (var3 == null) {
         throw new NullPointerException("Supplied content is null.");
      } else {
         this.content = var3;
         this.setTotalBodyLength(this.keyLength() + this.extrasLength() + var3.readableBytes());
      }
   }

   public ByteBuf content() {
      return this.content;
   }

   public FullBinaryMemcacheResponse retain() {
      super.retain();
      return this;
   }

   public FullBinaryMemcacheResponse retain(int var1) {
      super.retain(var1);
      return this;
   }

   public FullBinaryMemcacheResponse touch() {
      super.touch();
      return this;
   }

   public FullBinaryMemcacheResponse touch(Object var1) {
      super.touch(var1);
      this.content.touch(var1);
      return this;
   }

   protected void deallocate() {
      super.deallocate();
      this.content.release();
   }

   public FullBinaryMemcacheResponse copy() {
      ByteBuf var1 = this.key();
      if (var1 != null) {
         var1 = var1.copy();
      }

      ByteBuf var2 = this.extras();
      if (var2 != null) {
         var2 = var2.copy();
      }

      return new DefaultFullBinaryMemcacheResponse(var1, var2, this.content().copy());
   }

   public FullBinaryMemcacheResponse duplicate() {
      ByteBuf var1 = this.key();
      if (var1 != null) {
         var1 = var1.duplicate();
      }

      ByteBuf var2 = this.extras();
      if (var2 != null) {
         var2 = var2.duplicate();
      }

      return new DefaultFullBinaryMemcacheResponse(var1, var2, this.content().duplicate());
   }

   public FullBinaryMemcacheResponse retainedDuplicate() {
      return this.replace(this.content().retainedDuplicate());
   }

   public FullBinaryMemcacheResponse replace(ByteBuf var1) {
      ByteBuf var2 = this.key();
      if (var2 != null) {
         var2 = var2.retainedDuplicate();
      }

      ByteBuf var3 = this.extras();
      if (var3 != null) {
         var3 = var3.retainedDuplicate();
      }

      return new DefaultFullBinaryMemcacheResponse(var2, var3, var1);
   }
}
