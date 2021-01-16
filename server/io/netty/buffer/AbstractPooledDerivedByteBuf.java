package io.netty.buffer;

import io.netty.util.Recycler;
import io.netty.util.ReferenceCounted;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

abstract class AbstractPooledDerivedByteBuf extends AbstractReferenceCountedByteBuf {
   private final Recycler.Handle<AbstractPooledDerivedByteBuf> recyclerHandle;
   private AbstractByteBuf rootParent;
   private ByteBuf parent;

   AbstractPooledDerivedByteBuf(Recycler.Handle<? extends AbstractPooledDerivedByteBuf> var1) {
      super(0);
      this.recyclerHandle = var1;
   }

   final void parent(ByteBuf var1) {
      assert var1 instanceof SimpleLeakAwareByteBuf;

      this.parent = var1;
   }

   public final AbstractByteBuf unwrap() {
      return this.rootParent;
   }

   final <U extends AbstractPooledDerivedByteBuf> U init(AbstractByteBuf var1, ByteBuf var2, int var3, int var4, int var5) {
      var2.retain();
      this.parent = var2;
      this.rootParent = var1;

      AbstractPooledDerivedByteBuf var7;
      try {
         this.maxCapacity(var5);
         this.setIndex0(var3, var4);
         this.setRefCnt(1);
         var2 = null;
         var7 = this;
      } finally {
         if (var2 != null) {
            this.parent = this.rootParent = null;
            var2.release();
         }

      }

      return var7;
   }

   protected final void deallocate() {
      ByteBuf var1 = this.parent;
      this.recyclerHandle.recycle(this);
      var1.release();
   }

   public final ByteBufAllocator alloc() {
      return this.unwrap().alloc();
   }

   /** @deprecated */
   @Deprecated
   public final ByteOrder order() {
      return this.unwrap().order();
   }

   public boolean isReadOnly() {
      return this.unwrap().isReadOnly();
   }

   public final boolean isDirect() {
      return this.unwrap().isDirect();
   }

   public boolean hasArray() {
      return this.unwrap().hasArray();
   }

   public byte[] array() {
      return this.unwrap().array();
   }

   public boolean hasMemoryAddress() {
      return this.unwrap().hasMemoryAddress();
   }

   public final int nioBufferCount() {
      return this.unwrap().nioBufferCount();
   }

   public final ByteBuffer internalNioBuffer(int var1, int var2) {
      return this.nioBuffer(var1, var2);
   }

   public final ByteBuf retainedSlice() {
      int var1 = this.readerIndex();
      return this.retainedSlice(var1, this.writerIndex() - var1);
   }

   public ByteBuf slice(int var1, int var2) {
      this.ensureAccessible();
      return new AbstractPooledDerivedByteBuf.PooledNonRetainedSlicedByteBuf(this, this.unwrap(), var1, var2);
   }

   final ByteBuf duplicate0() {
      this.ensureAccessible();
      return new AbstractPooledDerivedByteBuf.PooledNonRetainedDuplicateByteBuf(this, this.unwrap());
   }

   private static final class PooledNonRetainedSlicedByteBuf extends UnpooledSlicedByteBuf {
      private final ReferenceCounted referenceCountDelegate;

      PooledNonRetainedSlicedByteBuf(ReferenceCounted var1, AbstractByteBuf var2, int var3, int var4) {
         super(var2, var3, var4);
         this.referenceCountDelegate = var1;
      }

      int refCnt0() {
         return this.referenceCountDelegate.refCnt();
      }

      ByteBuf retain0() {
         this.referenceCountDelegate.retain();
         return this;
      }

      ByteBuf retain0(int var1) {
         this.referenceCountDelegate.retain(var1);
         return this;
      }

      ByteBuf touch0() {
         this.referenceCountDelegate.touch();
         return this;
      }

      ByteBuf touch0(Object var1) {
         this.referenceCountDelegate.touch(var1);
         return this;
      }

      boolean release0() {
         return this.referenceCountDelegate.release();
      }

      boolean release0(int var1) {
         return this.referenceCountDelegate.release(var1);
      }

      public ByteBuf duplicate() {
         this.ensureAccessible();
         return (new AbstractPooledDerivedByteBuf.PooledNonRetainedDuplicateByteBuf(this.referenceCountDelegate, this.unwrap())).setIndex(this.idx(this.readerIndex()), this.idx(this.writerIndex()));
      }

      public ByteBuf retainedDuplicate() {
         return PooledDuplicatedByteBuf.newInstance(this.unwrap(), this, this.idx(this.readerIndex()), this.idx(this.writerIndex()));
      }

      public ByteBuf slice(int var1, int var2) {
         this.checkIndex(var1, var2);
         return new AbstractPooledDerivedByteBuf.PooledNonRetainedSlicedByteBuf(this.referenceCountDelegate, this.unwrap(), this.idx(var1), var2);
      }

      public ByteBuf retainedSlice() {
         return this.retainedSlice(0, this.capacity());
      }

      public ByteBuf retainedSlice(int var1, int var2) {
         return PooledSlicedByteBuf.newInstance(this.unwrap(), this, this.idx(var1), var2);
      }
   }

   private static final class PooledNonRetainedDuplicateByteBuf extends UnpooledDuplicatedByteBuf {
      private final ReferenceCounted referenceCountDelegate;

      PooledNonRetainedDuplicateByteBuf(ReferenceCounted var1, AbstractByteBuf var2) {
         super(var2);
         this.referenceCountDelegate = var1;
      }

      int refCnt0() {
         return this.referenceCountDelegate.refCnt();
      }

      ByteBuf retain0() {
         this.referenceCountDelegate.retain();
         return this;
      }

      ByteBuf retain0(int var1) {
         this.referenceCountDelegate.retain(var1);
         return this;
      }

      ByteBuf touch0() {
         this.referenceCountDelegate.touch();
         return this;
      }

      ByteBuf touch0(Object var1) {
         this.referenceCountDelegate.touch(var1);
         return this;
      }

      boolean release0() {
         return this.referenceCountDelegate.release();
      }

      boolean release0(int var1) {
         return this.referenceCountDelegate.release(var1);
      }

      public ByteBuf duplicate() {
         this.ensureAccessible();
         return new AbstractPooledDerivedByteBuf.PooledNonRetainedDuplicateByteBuf(this.referenceCountDelegate, this);
      }

      public ByteBuf retainedDuplicate() {
         return PooledDuplicatedByteBuf.newInstance(this.unwrap(), this, this.readerIndex(), this.writerIndex());
      }

      public ByteBuf slice(int var1, int var2) {
         this.checkIndex(var1, var2);
         return new AbstractPooledDerivedByteBuf.PooledNonRetainedSlicedByteBuf(this.referenceCountDelegate, this.unwrap(), var1, var2);
      }

      public ByteBuf retainedSlice() {
         return this.retainedSlice(this.readerIndex(), this.capacity());
      }

      public ByteBuf retainedSlice(int var1, int var2) {
         return PooledSlicedByteBuf.newInstance(this.unwrap(), this, var1, var2);
      }
   }
}
