package io.netty.buffer;

import java.nio.ByteOrder;

final class UnreleasableByteBuf extends WrappedByteBuf {
   private SwappedByteBuf swappedBuf;

   UnreleasableByteBuf(ByteBuf var1) {
      super(var1 instanceof UnreleasableByteBuf ? var1.unwrap() : var1);
   }

   public ByteBuf order(ByteOrder var1) {
      if (var1 == null) {
         throw new NullPointerException("endianness");
      } else if (var1 == this.order()) {
         return this;
      } else {
         SwappedByteBuf var2 = this.swappedBuf;
         if (var2 == null) {
            this.swappedBuf = var2 = new SwappedByteBuf(this);
         }

         return var2;
      }
   }

   public ByteBuf asReadOnly() {
      return this.buf.isReadOnly() ? this : new UnreleasableByteBuf(this.buf.asReadOnly());
   }

   public ByteBuf readSlice(int var1) {
      return new UnreleasableByteBuf(this.buf.readSlice(var1));
   }

   public ByteBuf readRetainedSlice(int var1) {
      return this.readSlice(var1);
   }

   public ByteBuf slice() {
      return new UnreleasableByteBuf(this.buf.slice());
   }

   public ByteBuf retainedSlice() {
      return this.slice();
   }

   public ByteBuf slice(int var1, int var2) {
      return new UnreleasableByteBuf(this.buf.slice(var1, var2));
   }

   public ByteBuf retainedSlice(int var1, int var2) {
      return this.slice(var1, var2);
   }

   public ByteBuf duplicate() {
      return new UnreleasableByteBuf(this.buf.duplicate());
   }

   public ByteBuf retainedDuplicate() {
      return this.duplicate();
   }

   public ByteBuf retain(int var1) {
      return this;
   }

   public ByteBuf retain() {
      return this;
   }

   public ByteBuf touch() {
      return this;
   }

   public ByteBuf touch(Object var1) {
      return this;
   }

   public boolean release() {
      return false;
   }

   public boolean release(int var1) {
      return false;
   }
}
