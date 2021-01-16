package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.ObjectUtil;

class PemValue extends AbstractReferenceCounted implements PemEncoded {
   private final ByteBuf content;
   private final boolean sensitive;

   public PemValue(ByteBuf var1, boolean var2) {
      super();
      this.content = (ByteBuf)ObjectUtil.checkNotNull(var1, "content");
      this.sensitive = var2;
   }

   public boolean isSensitive() {
      return this.sensitive;
   }

   public ByteBuf content() {
      int var1 = this.refCnt();
      if (var1 <= 0) {
         throw new IllegalReferenceCountException(var1);
      } else {
         return this.content;
      }
   }

   public PemValue copy() {
      return this.replace(this.content.copy());
   }

   public PemValue duplicate() {
      return this.replace(this.content.duplicate());
   }

   public PemValue retainedDuplicate() {
      return this.replace(this.content.retainedDuplicate());
   }

   public PemValue replace(ByteBuf var1) {
      return new PemValue(var1, this.sensitive);
   }

   public PemValue touch() {
      return (PemValue)super.touch();
   }

   public PemValue touch(Object var1) {
      this.content.touch(var1);
      return this;
   }

   public PemValue retain() {
      return (PemValue)super.retain();
   }

   public PemValue retain(int var1) {
      return (PemValue)super.retain(var1);
   }

   protected void deallocate() {
      if (this.sensitive) {
         SslUtils.zeroout(this.content);
      }

      this.content.release();
   }
}
