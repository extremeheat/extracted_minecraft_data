package io.netty.buffer;

import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.ObjectUtil;
import java.nio.ByteOrder;

class SimpleLeakAwareByteBuf extends WrappedByteBuf {
   private final ByteBuf trackedByteBuf;
   final ResourceLeakTracker<ByteBuf> leak;

   SimpleLeakAwareByteBuf(ByteBuf var1, ByteBuf var2, ResourceLeakTracker<ByteBuf> var3) {
      super(var1);
      this.trackedByteBuf = (ByteBuf)ObjectUtil.checkNotNull(var2, "trackedByteBuf");
      this.leak = (ResourceLeakTracker)ObjectUtil.checkNotNull(var3, "leak");
   }

   SimpleLeakAwareByteBuf(ByteBuf var1, ResourceLeakTracker<ByteBuf> var2) {
      this(var1, var1, var2);
   }

   public ByteBuf slice() {
      return this.newSharedLeakAwareByteBuf(super.slice());
   }

   public ByteBuf retainedSlice() {
      return this.unwrappedDerived(super.retainedSlice());
   }

   public ByteBuf retainedSlice(int var1, int var2) {
      return this.unwrappedDerived(super.retainedSlice(var1, var2));
   }

   public ByteBuf retainedDuplicate() {
      return this.unwrappedDerived(super.retainedDuplicate());
   }

   public ByteBuf readRetainedSlice(int var1) {
      return this.unwrappedDerived(super.readRetainedSlice(var1));
   }

   public ByteBuf slice(int var1, int var2) {
      return this.newSharedLeakAwareByteBuf(super.slice(var1, var2));
   }

   public ByteBuf duplicate() {
      return this.newSharedLeakAwareByteBuf(super.duplicate());
   }

   public ByteBuf readSlice(int var1) {
      return this.newSharedLeakAwareByteBuf(super.readSlice(var1));
   }

   public ByteBuf asReadOnly() {
      return this.newSharedLeakAwareByteBuf(super.asReadOnly());
   }

   public ByteBuf touch() {
      return this;
   }

   public ByteBuf touch(Object var1) {
      return this;
   }

   public boolean release() {
      if (super.release()) {
         this.closeLeak();
         return true;
      } else {
         return false;
      }
   }

   public boolean release(int var1) {
      if (super.release(var1)) {
         this.closeLeak();
         return true;
      } else {
         return false;
      }
   }

   private void closeLeak() {
      boolean var1 = this.leak.close(this.trackedByteBuf);

      assert var1;

   }

   public ByteBuf order(ByteOrder var1) {
      return this.order() == var1 ? this : this.newSharedLeakAwareByteBuf(super.order(var1));
   }

   private ByteBuf unwrappedDerived(ByteBuf var1) {
      ByteBuf var2 = unwrapSwapped(var1);
      if (var2 instanceof AbstractPooledDerivedByteBuf) {
         ((AbstractPooledDerivedByteBuf)var2).parent(this);
         ResourceLeakTracker var3 = AbstractByteBuf.leakDetector.track(var1);
         return (ByteBuf)(var3 == null ? var1 : this.newLeakAwareByteBuf(var1, var3));
      } else {
         return this.newSharedLeakAwareByteBuf(var1);
      }
   }

   private static ByteBuf unwrapSwapped(ByteBuf var0) {
      if (!(var0 instanceof SwappedByteBuf)) {
         return var0;
      } else {
         do {
            var0 = var0.unwrap();
         } while(var0 instanceof SwappedByteBuf);

         return var0;
      }
   }

   private SimpleLeakAwareByteBuf newSharedLeakAwareByteBuf(ByteBuf var1) {
      return this.newLeakAwareByteBuf(var1, this.trackedByteBuf, this.leak);
   }

   private SimpleLeakAwareByteBuf newLeakAwareByteBuf(ByteBuf var1, ResourceLeakTracker<ByteBuf> var2) {
      return this.newLeakAwareByteBuf(var1, var1, var2);
   }

   protected SimpleLeakAwareByteBuf newLeakAwareByteBuf(ByteBuf var1, ByteBuf var2, ResourceLeakTracker<ByteBuf> var3) {
      return new SimpleLeakAwareByteBuf(var1, var2, var3);
   }
}
