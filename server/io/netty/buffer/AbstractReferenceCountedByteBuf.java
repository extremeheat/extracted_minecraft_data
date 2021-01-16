package io.netty.buffer;

import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class AbstractReferenceCountedByteBuf extends AbstractByteBuf {
   private static final AtomicIntegerFieldUpdater<AbstractReferenceCountedByteBuf> refCntUpdater = AtomicIntegerFieldUpdater.newUpdater(AbstractReferenceCountedByteBuf.class, "refCnt");
   private volatile int refCnt;

   protected AbstractReferenceCountedByteBuf(int var1) {
      super(var1);
      refCntUpdater.set(this, 1);
   }

   public int refCnt() {
      return this.refCnt;
   }

   protected final void setRefCnt(int var1) {
      refCntUpdater.set(this, var1);
   }

   public ByteBuf retain() {
      return this.retain0(1);
   }

   public ByteBuf retain(int var1) {
      return this.retain0(ObjectUtil.checkPositive(var1, "increment"));
   }

   private ByteBuf retain0(int var1) {
      int var2 = refCntUpdater.getAndAdd(this, var1);
      if (var2 > 0 && var2 + var1 >= var2) {
         return this;
      } else {
         refCntUpdater.getAndAdd(this, -var1);
         throw new IllegalReferenceCountException(var2, var1);
      }
   }

   public ByteBuf touch() {
      return this;
   }

   public ByteBuf touch(Object var1) {
      return this;
   }

   public boolean release() {
      return this.release0(1);
   }

   public boolean release(int var1) {
      return this.release0(ObjectUtil.checkPositive(var1, "decrement"));
   }

   private boolean release0(int var1) {
      int var2 = refCntUpdater.getAndAdd(this, -var1);
      if (var2 == var1) {
         this.deallocate();
         return true;
      } else if (var2 >= var1 && var2 - var1 <= var2) {
         return false;
      } else {
         refCntUpdater.getAndAdd(this, var1);
         throw new IllegalReferenceCountException(var2, -var1);
      }
   }

   protected abstract void deallocate();
}
