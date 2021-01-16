package io.netty.buffer;

import io.netty.util.Recycler;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

abstract class PooledByteBuf<T> extends AbstractReferenceCountedByteBuf {
   private final Recycler.Handle<PooledByteBuf<T>> recyclerHandle;
   protected PoolChunk<T> chunk;
   protected long handle;
   protected T memory;
   protected int offset;
   protected int length;
   int maxLength;
   PoolThreadCache cache;
   private ByteBuffer tmpNioBuf;
   private ByteBufAllocator allocator;

   protected PooledByteBuf(Recycler.Handle<? extends PooledByteBuf<T>> var1, int var2) {
      super(var2);
      this.recyclerHandle = var1;
   }

   void init(PoolChunk<T> var1, long var2, int var4, int var5, int var6, PoolThreadCache var7) {
      this.init0(var1, var2, var4, var5, var6, var7);
   }

   void initUnpooled(PoolChunk<T> var1, int var2) {
      this.init0(var1, 0L, var1.offset, var2, var2, (PoolThreadCache)null);
   }

   private void init0(PoolChunk<T> var1, long var2, int var4, int var5, int var6, PoolThreadCache var7) {
      assert var2 >= 0L;

      assert var1 != null;

      this.chunk = var1;
      this.memory = var1.memory;
      this.allocator = var1.arena.parent;
      this.cache = var7;
      this.handle = var2;
      this.offset = var4;
      this.length = var5;
      this.maxLength = var6;
      this.tmpNioBuf = null;
   }

   final void reuse(int var1) {
      this.maxCapacity(var1);
      this.setRefCnt(1);
      this.setIndex0(0, 0);
      this.discardMarks();
   }

   public final int capacity() {
      return this.length;
   }

   public final ByteBuf capacity(int var1) {
      this.checkNewCapacity(var1);
      if (this.chunk.unpooled) {
         if (var1 == this.length) {
            return this;
         }
      } else if (var1 > this.length) {
         if (var1 <= this.maxLength) {
            this.length = var1;
            return this;
         }
      } else {
         if (var1 >= this.length) {
            return this;
         }

         if (var1 > this.maxLength >>> 1) {
            if (this.maxLength > 512) {
               this.length = var1;
               this.setIndex(Math.min(this.readerIndex(), var1), Math.min(this.writerIndex(), var1));
               return this;
            }

            if (var1 > this.maxLength - 16) {
               this.length = var1;
               this.setIndex(Math.min(this.readerIndex(), var1), Math.min(this.writerIndex(), var1));
               return this;
            }
         }
      }

      this.chunk.arena.reallocate(this, var1, true);
      return this;
   }

   public final ByteBufAllocator alloc() {
      return this.allocator;
   }

   public final ByteOrder order() {
      return ByteOrder.BIG_ENDIAN;
   }

   public final ByteBuf unwrap() {
      return null;
   }

   public final ByteBuf retainedDuplicate() {
      return PooledDuplicatedByteBuf.newInstance(this, this, this.readerIndex(), this.writerIndex());
   }

   public final ByteBuf retainedSlice() {
      int var1 = this.readerIndex();
      return this.retainedSlice(var1, this.writerIndex() - var1);
   }

   public final ByteBuf retainedSlice(int var1, int var2) {
      return PooledSlicedByteBuf.newInstance(this, this, var1, var2);
   }

   protected final ByteBuffer internalNioBuffer() {
      ByteBuffer var1 = this.tmpNioBuf;
      if (var1 == null) {
         this.tmpNioBuf = var1 = this.newInternalNioBuffer(this.memory);
      }

      return var1;
   }

   protected abstract ByteBuffer newInternalNioBuffer(T var1);

   protected final void deallocate() {
      if (this.handle >= 0L) {
         long var1 = this.handle;
         this.handle = -1L;
         this.memory = null;
         this.tmpNioBuf = null;
         this.chunk.arena.free(this.chunk, var1, this.maxLength, this.cache);
         this.chunk = null;
         this.recycle();
      }

   }

   private void recycle() {
      this.recyclerHandle.recycle(this);
   }

   protected final int idx(int var1) {
      return this.offset + var1;
   }
}
