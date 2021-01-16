package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;

class UnpooledUnsafeHeapByteBuf extends UnpooledHeapByteBuf {
   UnpooledUnsafeHeapByteBuf(ByteBufAllocator var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   byte[] allocateArray(int var1) {
      return PlatformDependent.allocateUninitializedArray(var1);
   }

   public byte getByte(int var1) {
      this.checkIndex(var1);
      return this._getByte(var1);
   }

   protected byte _getByte(int var1) {
      return UnsafeByteBufUtil.getByte(this.array, var1);
   }

   public short getShort(int var1) {
      this.checkIndex(var1, 2);
      return this._getShort(var1);
   }

   protected short _getShort(int var1) {
      return UnsafeByteBufUtil.getShort(this.array, var1);
   }

   public short getShortLE(int var1) {
      this.checkIndex(var1, 2);
      return this._getShortLE(var1);
   }

   protected short _getShortLE(int var1) {
      return UnsafeByteBufUtil.getShortLE(this.array, var1);
   }

   public int getUnsignedMedium(int var1) {
      this.checkIndex(var1, 3);
      return this._getUnsignedMedium(var1);
   }

   protected int _getUnsignedMedium(int var1) {
      return UnsafeByteBufUtil.getUnsignedMedium(this.array, var1);
   }

   public int getUnsignedMediumLE(int var1) {
      this.checkIndex(var1, 3);
      return this._getUnsignedMediumLE(var1);
   }

   protected int _getUnsignedMediumLE(int var1) {
      return UnsafeByteBufUtil.getUnsignedMediumLE(this.array, var1);
   }

   public int getInt(int var1) {
      this.checkIndex(var1, 4);
      return this._getInt(var1);
   }

   protected int _getInt(int var1) {
      return UnsafeByteBufUtil.getInt(this.array, var1);
   }

   public int getIntLE(int var1) {
      this.checkIndex(var1, 4);
      return this._getIntLE(var1);
   }

   protected int _getIntLE(int var1) {
      return UnsafeByteBufUtil.getIntLE(this.array, var1);
   }

   public long getLong(int var1) {
      this.checkIndex(var1, 8);
      return this._getLong(var1);
   }

   protected long _getLong(int var1) {
      return UnsafeByteBufUtil.getLong(this.array, var1);
   }

   public long getLongLE(int var1) {
      this.checkIndex(var1, 8);
      return this._getLongLE(var1);
   }

   protected long _getLongLE(int var1) {
      return UnsafeByteBufUtil.getLongLE(this.array, var1);
   }

   public ByteBuf setByte(int var1, int var2) {
      this.checkIndex(var1);
      this._setByte(var1, var2);
      return this;
   }

   protected void _setByte(int var1, int var2) {
      UnsafeByteBufUtil.setByte(this.array, var1, var2);
   }

   public ByteBuf setShort(int var1, int var2) {
      this.checkIndex(var1, 2);
      this._setShort(var1, var2);
      return this;
   }

   protected void _setShort(int var1, int var2) {
      UnsafeByteBufUtil.setShort(this.array, var1, var2);
   }

   public ByteBuf setShortLE(int var1, int var2) {
      this.checkIndex(var1, 2);
      this._setShortLE(var1, var2);
      return this;
   }

   protected void _setShortLE(int var1, int var2) {
      UnsafeByteBufUtil.setShortLE(this.array, var1, var2);
   }

   public ByteBuf setMedium(int var1, int var2) {
      this.checkIndex(var1, 3);
      this._setMedium(var1, var2);
      return this;
   }

   protected void _setMedium(int var1, int var2) {
      UnsafeByteBufUtil.setMedium(this.array, var1, var2);
   }

   public ByteBuf setMediumLE(int var1, int var2) {
      this.checkIndex(var1, 3);
      this._setMediumLE(var1, var2);
      return this;
   }

   protected void _setMediumLE(int var1, int var2) {
      UnsafeByteBufUtil.setMediumLE(this.array, var1, var2);
   }

   public ByteBuf setInt(int var1, int var2) {
      this.checkIndex(var1, 4);
      this._setInt(var1, var2);
      return this;
   }

   protected void _setInt(int var1, int var2) {
      UnsafeByteBufUtil.setInt(this.array, var1, var2);
   }

   public ByteBuf setIntLE(int var1, int var2) {
      this.checkIndex(var1, 4);
      this._setIntLE(var1, var2);
      return this;
   }

   protected void _setIntLE(int var1, int var2) {
      UnsafeByteBufUtil.setIntLE(this.array, var1, var2);
   }

   public ByteBuf setLong(int var1, long var2) {
      this.checkIndex(var1, 8);
      this._setLong(var1, var2);
      return this;
   }

   protected void _setLong(int var1, long var2) {
      UnsafeByteBufUtil.setLong(this.array, var1, var2);
   }

   public ByteBuf setLongLE(int var1, long var2) {
      this.checkIndex(var1, 8);
      this._setLongLE(var1, var2);
      return this;
   }

   protected void _setLongLE(int var1, long var2) {
      UnsafeByteBufUtil.setLongLE(this.array, var1, var2);
   }

   public ByteBuf setZero(int var1, int var2) {
      if (PlatformDependent.javaVersion() >= 7) {
         this.checkIndex(var1, var2);
         UnsafeByteBufUtil.setZero(this.array, var1, var2);
         return this;
      } else {
         return super.setZero(var1, var2);
      }
   }

   public ByteBuf writeZero(int var1) {
      if (PlatformDependent.javaVersion() >= 7) {
         this.ensureWritable(var1);
         int var2 = this.writerIndex;
         UnsafeByteBufUtil.setZero(this.array, var2, var1);
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
}
