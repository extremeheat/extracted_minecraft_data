package io.netty.buffer;

import io.netty.util.Recycler;
import io.netty.util.internal.PlatformDependent;

final class PooledUnsafeHeapByteBuf extends PooledHeapByteBuf {
   private static final Recycler<PooledUnsafeHeapByteBuf> RECYCLER = new Recycler<PooledUnsafeHeapByteBuf>() {
      protected PooledUnsafeHeapByteBuf newObject(Recycler.Handle<PooledUnsafeHeapByteBuf> var1) {
         return new PooledUnsafeHeapByteBuf(var1, 0);
      }
   };

   static PooledUnsafeHeapByteBuf newUnsafeInstance(int var0) {
      PooledUnsafeHeapByteBuf var1 = (PooledUnsafeHeapByteBuf)RECYCLER.get();
      var1.reuse(var0);
      return var1;
   }

   private PooledUnsafeHeapByteBuf(Recycler.Handle<PooledUnsafeHeapByteBuf> var1, int var2) {
      super(var1, var2);
   }

   protected byte _getByte(int var1) {
      return UnsafeByteBufUtil.getByte((byte[])this.memory, this.idx(var1));
   }

   protected short _getShort(int var1) {
      return UnsafeByteBufUtil.getShort((byte[])this.memory, this.idx(var1));
   }

   protected short _getShortLE(int var1) {
      return UnsafeByteBufUtil.getShortLE((byte[])this.memory, this.idx(var1));
   }

   protected int _getUnsignedMedium(int var1) {
      return UnsafeByteBufUtil.getUnsignedMedium((byte[])this.memory, this.idx(var1));
   }

   protected int _getUnsignedMediumLE(int var1) {
      return UnsafeByteBufUtil.getUnsignedMediumLE((byte[])this.memory, this.idx(var1));
   }

   protected int _getInt(int var1) {
      return UnsafeByteBufUtil.getInt((byte[])this.memory, this.idx(var1));
   }

   protected int _getIntLE(int var1) {
      return UnsafeByteBufUtil.getIntLE((byte[])this.memory, this.idx(var1));
   }

   protected long _getLong(int var1) {
      return UnsafeByteBufUtil.getLong((byte[])this.memory, this.idx(var1));
   }

   protected long _getLongLE(int var1) {
      return UnsafeByteBufUtil.getLongLE((byte[])this.memory, this.idx(var1));
   }

   protected void _setByte(int var1, int var2) {
      UnsafeByteBufUtil.setByte((byte[])this.memory, this.idx(var1), var2);
   }

   protected void _setShort(int var1, int var2) {
      UnsafeByteBufUtil.setShort((byte[])this.memory, this.idx(var1), var2);
   }

   protected void _setShortLE(int var1, int var2) {
      UnsafeByteBufUtil.setShortLE((byte[])this.memory, this.idx(var1), var2);
   }

   protected void _setMedium(int var1, int var2) {
      UnsafeByteBufUtil.setMedium((byte[])this.memory, this.idx(var1), var2);
   }

   protected void _setMediumLE(int var1, int var2) {
      UnsafeByteBufUtil.setMediumLE((byte[])this.memory, this.idx(var1), var2);
   }

   protected void _setInt(int var1, int var2) {
      UnsafeByteBufUtil.setInt((byte[])this.memory, this.idx(var1), var2);
   }

   protected void _setIntLE(int var1, int var2) {
      UnsafeByteBufUtil.setIntLE((byte[])this.memory, this.idx(var1), var2);
   }

   protected void _setLong(int var1, long var2) {
      UnsafeByteBufUtil.setLong((byte[])this.memory, this.idx(var1), var2);
   }

   protected void _setLongLE(int var1, long var2) {
      UnsafeByteBufUtil.setLongLE((byte[])this.memory, this.idx(var1), var2);
   }

   public ByteBuf setZero(int var1, int var2) {
      if (PlatformDependent.javaVersion() >= 7) {
         this.checkIndex(var1, var2);
         UnsafeByteBufUtil.setZero((byte[])this.memory, this.idx(var1), var2);
         return this;
      } else {
         return super.setZero(var1, var2);
      }
   }

   public ByteBuf writeZero(int var1) {
      if (PlatformDependent.javaVersion() >= 7) {
         this.ensureWritable(var1);
         int var2 = this.writerIndex;
         UnsafeByteBufUtil.setZero((byte[])this.memory, this.idx(var2), var1);
         this.writerIndex = var2 + var1;
         return this;
      } else {
         return super.writeZero(var1);
      }
   }

   /** @deprecated */
   @Deprecated
   protected SwappedByteBuf newSwappedByteBuf() {
      return (SwappedByteBuf)(PlatformDependent.isUnaligned() ? new UnsafeHeapSwappedByteBuf(this) : super.newSwappedByteBuf());
   }

   // $FF: synthetic method
   PooledUnsafeHeapByteBuf(Recycler.Handle var1, int var2, Object var3) {
      this(var1, var2);
   }
}
