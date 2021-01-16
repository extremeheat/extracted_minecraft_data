package io.netty.buffer;

import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.ObjectUtil;
import java.nio.ByteOrder;

class SimpleLeakAwareCompositeByteBuf extends WrappedCompositeByteBuf {
   final ResourceLeakTracker<ByteBuf> leak;

   SimpleLeakAwareCompositeByteBuf(CompositeByteBuf var1, ResourceLeakTracker<ByteBuf> var2) {
      super(var1);
      this.leak = (ResourceLeakTracker)ObjectUtil.checkNotNull(var2, "leak");
   }

   public boolean release() {
      ByteBuf var1 = this.unwrap();
      if (super.release()) {
         this.closeLeak(var1);
         return true;
      } else {
         return false;
      }
   }

   public boolean release(int var1) {
      ByteBuf var2 = this.unwrap();
      if (super.release(var1)) {
         this.closeLeak(var2);
         return true;
      } else {
         return false;
      }
   }

   private void closeLeak(ByteBuf var1) {
      boolean var2 = this.leak.close(var1);

      assert var2;

   }

   public ByteBuf order(ByteOrder var1) {
      return (ByteBuf)(this.order() == var1 ? this : this.newLeakAwareByteBuf(super.order(var1)));
   }

   public ByteBuf slice() {
      return this.newLeakAwareByteBuf(super.slice());
   }

   public ByteBuf retainedSlice() {
      return this.newLeakAwareByteBuf(super.retainedSlice());
   }

   public ByteBuf slice(int var1, int var2) {
      return this.newLeakAwareByteBuf(super.slice(var1, var2));
   }

   public ByteBuf retainedSlice(int var1, int var2) {
      return this.newLeakAwareByteBuf(super.retainedSlice(var1, var2));
   }

   public ByteBuf duplicate() {
      return this.newLeakAwareByteBuf(super.duplicate());
   }

   public ByteBuf retainedDuplicate() {
      return this.newLeakAwareByteBuf(super.retainedDuplicate());
   }

   public ByteBuf readSlice(int var1) {
      return this.newLeakAwareByteBuf(super.readSlice(var1));
   }

   public ByteBuf readRetainedSlice(int var1) {
      return this.newLeakAwareByteBuf(super.readRetainedSlice(var1));
   }

   public ByteBuf asReadOnly() {
      return this.newLeakAwareByteBuf(super.asReadOnly());
   }

   private SimpleLeakAwareByteBuf newLeakAwareByteBuf(ByteBuf var1) {
      return this.newLeakAwareByteBuf(var1, this.unwrap(), this.leak);
   }

   protected SimpleLeakAwareByteBuf newLeakAwareByteBuf(ByteBuf var1, ByteBuf var2, ResourceLeakTracker<ByteBuf> var3) {
      return new SimpleLeakAwareByteBuf(var1, var2, var3);
   }
}
