package io.netty.buffer;

import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.StringUtil;

public class DefaultByteBufHolder implements ByteBufHolder {
   private final ByteBuf data;

   public DefaultByteBufHolder(ByteBuf var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("data");
      } else {
         this.data = var1;
      }
   }

   public ByteBuf content() {
      if (this.data.refCnt() <= 0) {
         throw new IllegalReferenceCountException(this.data.refCnt());
      } else {
         return this.data;
      }
   }

   public ByteBufHolder copy() {
      return this.replace(this.data.copy());
   }

   public ByteBufHolder duplicate() {
      return this.replace(this.data.duplicate());
   }

   public ByteBufHolder retainedDuplicate() {
      return this.replace(this.data.retainedDuplicate());
   }

   public ByteBufHolder replace(ByteBuf var1) {
      return new DefaultByteBufHolder(var1);
   }

   public int refCnt() {
      return this.data.refCnt();
   }

   public ByteBufHolder retain() {
      this.data.retain();
      return this;
   }

   public ByteBufHolder retain(int var1) {
      this.data.retain(var1);
      return this;
   }

   public ByteBufHolder touch() {
      this.data.touch();
      return this;
   }

   public ByteBufHolder touch(Object var1) {
      this.data.touch(var1);
      return this;
   }

   public boolean release() {
      return this.data.release();
   }

   public boolean release(int var1) {
      return this.data.release(var1);
   }

   protected final String contentToString() {
      return this.data.toString();
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + '(' + this.contentToString() + ')';
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof ByteBufHolder ? this.data.equals(((ByteBufHolder)var1).content()) : false;
      }
   }

   public int hashCode() {
      return this.data.hashCode();
   }
}
