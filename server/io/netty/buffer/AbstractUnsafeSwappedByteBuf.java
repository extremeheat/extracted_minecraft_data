package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;
import java.nio.ByteOrder;

abstract class AbstractUnsafeSwappedByteBuf extends SwappedByteBuf {
   private final boolean nativeByteOrder;
   private final AbstractByteBuf wrapped;

   AbstractUnsafeSwappedByteBuf(AbstractByteBuf var1) {
      super(var1);

      assert PlatformDependent.isUnaligned();

      this.wrapped = var1;
      this.nativeByteOrder = PlatformDependent.BIG_ENDIAN_NATIVE_ORDER == (this.order() == ByteOrder.BIG_ENDIAN);
   }

   public final long getLong(int var1) {
      this.wrapped.checkIndex(var1, 8);
      long var2 = this._getLong(this.wrapped, var1);
      return this.nativeByteOrder ? var2 : Long.reverseBytes(var2);
   }

   public final float getFloat(int var1) {
      return Float.intBitsToFloat(this.getInt(var1));
   }

   public final double getDouble(int var1) {
      return Double.longBitsToDouble(this.getLong(var1));
   }

   public final char getChar(int var1) {
      return (char)this.getShort(var1);
   }

   public final long getUnsignedInt(int var1) {
      return (long)this.getInt(var1) & 4294967295L;
   }

   public final int getInt(int var1) {
      this.wrapped.checkIndex(var1, 4);
      int var2 = this._getInt(this.wrapped, var1);
      return this.nativeByteOrder ? var2 : Integer.reverseBytes(var2);
   }

   public final int getUnsignedShort(int var1) {
      return this.getShort(var1) & '\uffff';
   }

   public final short getShort(int var1) {
      this.wrapped.checkIndex(var1, 2);
      short var2 = this._getShort(this.wrapped, var1);
      return this.nativeByteOrder ? var2 : Short.reverseBytes(var2);
   }

   public final ByteBuf setShort(int var1, int var2) {
      this.wrapped.checkIndex(var1, 2);
      this._setShort(this.wrapped, var1, this.nativeByteOrder ? (short)var2 : Short.reverseBytes((short)var2));
      return this;
   }

   public final ByteBuf setInt(int var1, int var2) {
      this.wrapped.checkIndex(var1, 4);
      this._setInt(this.wrapped, var1, this.nativeByteOrder ? var2 : Integer.reverseBytes(var2));
      return this;
   }

   public final ByteBuf setLong(int var1, long var2) {
      this.wrapped.checkIndex(var1, 8);
      this._setLong(this.wrapped, var1, this.nativeByteOrder ? var2 : Long.reverseBytes(var2));
      return this;
   }

   public final ByteBuf setChar(int var1, int var2) {
      this.setShort(var1, var2);
      return this;
   }

   public final ByteBuf setFloat(int var1, float var2) {
      this.setInt(var1, Float.floatToRawIntBits(var2));
      return this;
   }

   public final ByteBuf setDouble(int var1, double var2) {
      this.setLong(var1, Double.doubleToRawLongBits(var2));
      return this;
   }

   public final ByteBuf writeShort(int var1) {
      this.wrapped.ensureWritable0(2);
      this._setShort(this.wrapped, this.wrapped.writerIndex, this.nativeByteOrder ? (short)var1 : Short.reverseBytes((short)var1));
      AbstractByteBuf var10000 = this.wrapped;
      var10000.writerIndex += 2;
      return this;
   }

   public final ByteBuf writeInt(int var1) {
      this.wrapped.ensureWritable0(4);
      this._setInt(this.wrapped, this.wrapped.writerIndex, this.nativeByteOrder ? var1 : Integer.reverseBytes(var1));
      AbstractByteBuf var10000 = this.wrapped;
      var10000.writerIndex += 4;
      return this;
   }

   public final ByteBuf writeLong(long var1) {
      this.wrapped.ensureWritable0(8);
      this._setLong(this.wrapped, this.wrapped.writerIndex, this.nativeByteOrder ? var1 : Long.reverseBytes(var1));
      AbstractByteBuf var10000 = this.wrapped;
      var10000.writerIndex += 8;
      return this;
   }

   public final ByteBuf writeChar(int var1) {
      this.writeShort(var1);
      return this;
   }

   public final ByteBuf writeFloat(float var1) {
      this.writeInt(Float.floatToRawIntBits(var1));
      return this;
   }

   public final ByteBuf writeDouble(double var1) {
      this.writeLong(Double.doubleToRawLongBits(var1));
      return this;
   }

   protected abstract short _getShort(AbstractByteBuf var1, int var2);

   protected abstract int _getInt(AbstractByteBuf var1, int var2);

   protected abstract long _getLong(AbstractByteBuf var1, int var2);

   protected abstract void _setShort(AbstractByteBuf var1, int var2, short var3);

   protected abstract void _setInt(AbstractByteBuf var1, int var2, int var3);

   protected abstract void _setLong(AbstractByteBuf var1, int var2, long var3);
}
