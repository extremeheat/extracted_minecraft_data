package io.netty.util;

import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class AbstractReferenceCounted implements ReferenceCounted {
   private static final AtomicIntegerFieldUpdater<AbstractReferenceCounted> refCntUpdater = AtomicIntegerFieldUpdater.newUpdater(AbstractReferenceCounted.class, "refCnt");
   private volatile int refCnt = 1;

   public AbstractReferenceCounted() {
      super();
   }

   public final int refCnt() {
      return this.refCnt;
   }

   protected final void setRefCnt(int var1) {
      refCntUpdater.set(this, var1);
   }

   public ReferenceCounted retain() {
      return this.retain0(1);
   }

   public ReferenceCounted retain(int var1) {
      return this.retain0(ObjectUtil.checkPositive(var1, "increment"));
   }

   private ReferenceCounted retain0(int var1) {
      int var2 = refCntUpdater.getAndAdd(this, var1);
      if (var2 > 0 && var2 + var1 >= var2) {
         return this;
      } else {
         refCntUpdater.getAndAdd(this, -var1);
         throw new IllegalReferenceCountException(var2, var1);
      }
   }

   public ReferenceCounted touch() {
      return this.touch((Object)null);
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
