package io.netty.buffer;

import io.netty.util.ByteProcessor;
import io.netty.util.Recycler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

final class PooledSlicedByteBuf extends AbstractPooledDerivedByteBuf {
   private static final Recycler<PooledSlicedByteBuf> RECYCLER = new Recycler<PooledSlicedByteBuf>() {
      protected PooledSlicedByteBuf newObject(Recycler.Handle<PooledSlicedByteBuf> var1) {
         return new PooledSlicedByteBuf(var1);
      }
   };
   int adjustment;

   static PooledSlicedByteBuf newInstance(AbstractByteBuf var0, ByteBuf var1, int var2, int var3) {
      AbstractUnpooledSlicedByteBuf.checkSliceOutOfBounds(var2, var3, var0);
      return newInstance0(var0, var1, var2, var3);
   }

   private static PooledSlicedByteBuf newInstance0(AbstractByteBuf var0, ByteBuf var1, int var2, int var3) {
      PooledSlicedByteBuf var4 = (PooledSlicedByteBuf)RECYCLER.get();
      var4.init(var0, var1, 0, var3, var3);
      var4.discardMarks();
      var4.adjustment = var2;
      return var4;
   }

   private PooledSlicedByteBuf(Recycler.Handle<PooledSlicedByteBuf> var1) {
      super(var1);
   }

   public int capacity() {
      return this.maxCapacity();
   }

   public ByteBuf capacity(int var1) {
      throw new UnsupportedOperationException("sliced buffer");
   }

   public int arrayOffset() {
      return this.idx(this.unwrap().arrayOffset());
   }

   public long memoryAddress() {
      return this.unwrap().memoryAddress() + (long)this.adjustment;
   }

   public ByteBuffer nioBuffer(int var1, int var2) {
      this.checkIndex0(var1, var2);
      return this.unwrap().nioBuffer(this.idx(var1), var2);
   }

   public ByteBuffer[] nioBuffers(int var1, int var2) {
      this.checkIndex0(var1, var2);
      return this.unwrap().nioBuffers(this.idx(var1), var2);
   }

   public ByteBuf copy(int var1, int var2) {
      this.checkIndex0(var1, var2);
      return this.unwrap().copy(this.idx(var1), var2);
   }

   public ByteBuf slice(int var1, int var2) {
      this.checkIndex0(var1, var2);
      return super.slice(this.idx(var1), var2);
   }

   public ByteBuf retainedSlice(int var1, int var2) {
      this.checkIndex0(var1, var2);
      return newInstance0(this.unwrap(), this, this.idx(var1), var2);
   }

   public ByteBuf duplicate() {
      return this.duplicate0().setIndex(this.idx(this.readerIndex()), this.idx(this.writerIndex()));
   }

   public ByteBuf retainedDuplicate() {
      return PooledDuplicatedByteBuf.newInstance(this.unwrap(), this, this.idx(this.readerIndex()), this.idx(this.writerIndex()));
   }

   public byte getByte(int var1) {
      this.checkIndex0(var1, 1);
      return this.unwrap().getByte(this.idx(var1));
   }

   protected byte _getByte(int var1) {
      return this.unwrap()._getByte(this.idx(var1));
   }

   public short getShort(int var1) {
      this.checkIndex0(var1, 2);
      return this.unwrap().getShort(this.idx(var1));
   }

   protected short _getShort(int var1) {
      return this.unwrap()._getShort(this.idx(var1));
   }

   public short getShortLE(int var1) {
      this.checkIndex0(var1, 2);
      return this.unwrap().getShortLE(this.idx(var1));
   }

   protected short _getShortLE(int var1) {
      return this.unwrap()._getShortLE(this.idx(var1));
   }

   public int getUnsignedMedium(int var1) {
      this.checkIndex0(var1, 3);
      return this.unwrap().getUnsignedMedium(this.idx(var1));
   }

   protected int _getUnsignedMedium(int var1) {
      return this.unwrap()._getUnsignedMedium(this.idx(var1));
   }

   public int getUnsignedMediumLE(int var1) {
      this.checkIndex0(var1, 3);
      return this.unwrap().getUnsignedMediumLE(this.idx(var1));
   }

   protected int _getUnsignedMediumLE(int var1) {
      return this.unwrap()._getUnsignedMediumLE(this.idx(var1));
   }

   public int getInt(int var1) {
      this.checkIndex0(var1, 4);
      return this.unwrap().getInt(this.idx(var1));
   }

   protected int _getInt(int var1) {
      return this.unwrap()._getInt(this.idx(var1));
   }

   public int getIntLE(int var1) {
      this.checkIndex0(var1, 4);
      return this.unwrap().getIntLE(this.idx(var1));
   }

   protected int _getIntLE(int var1) {
      return this.unwrap()._getIntLE(this.idx(var1));
   }

   public long getLong(int var1) {
      this.checkIndex0(var1, 8);
      return this.unwrap().getLong(this.idx(var1));
   }

   protected long _getLong(int var1) {
      return this.unwrap()._getLong(this.idx(var1));
   }

   public long getLongLE(int var1) {
      this.checkIndex0(var1, 8);
      return this.unwrap().getLongLE(this.idx(var1));
   }

   protected long _getLongLE(int var1) {
      return this.unwrap()._getLongLE(this.idx(var1));
   }

   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkIndex0(var1, var4);
      this.unwrap().getBytes(this.idx(var1), var2, var3, var4);
      return this;
   }

   public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkIndex0(var1, var4);
      this.unwrap().getBytes(this.idx(var1), var2, var3, var4);
      return this;
   }

   public ByteBuf getBytes(int var1, ByteBuffer var2) {
      this.checkIndex0(var1, var2.remaining());
      this.unwrap().getBytes(this.idx(var1), (ByteBuffer)var2);
      return this;
   }

   public ByteBuf setByte(int var1, int var2) {
      this.checkIndex0(var1, 1);
      this.unwrap().setByte(this.idx(var1), var2);
      return this;
   }

   protected void _setByte(int var1, int var2) {
      this.unwrap()._setByte(this.idx(var1), var2);
   }

   public ByteBuf setShort(int var1, int var2) {
      this.checkIndex0(var1, 2);
      this.unwrap().setShort(this.idx(var1), var2);
      return this;
   }

   protected void _setShort(int var1, int var2) {
      this.unwrap()._setShort(this.idx(var1), var2);
   }

   public ByteBuf setShortLE(int var1, int var2) {
      this.checkIndex0(var1, 2);
      this.unwrap().setShortLE(this.idx(var1), var2);
      return this;
   }

   protected void _setShortLE(int var1, int var2) {
      this.unwrap()._setShortLE(this.idx(var1), var2);
   }

   public ByteBuf setMedium(int var1, int var2) {
      this.checkIndex0(var1, 3);
      this.unwrap().setMedium(this.idx(var1), var2);
      return this;
   }

   protected void _setMedium(int var1, int var2) {
      this.unwrap()._setMedium(this.idx(var1), var2);
   }

   public ByteBuf setMediumLE(int var1, int var2) {
      this.checkIndex0(var1, 3);
      this.unwrap().setMediumLE(this.idx(var1), var2);
      return this;
   }

   protected void _setMediumLE(int var1, int var2) {
      this.unwrap()._setMediumLE(this.idx(var1), var2);
   }

   public ByteBuf setInt(int var1, int var2) {
      this.checkIndex0(var1, 4);
      this.unwrap().setInt(this.idx(var1), var2);
      return this;
   }

   protected void _setInt(int var1, int var2) {
      this.unwrap()._setInt(this.idx(var1), var2);
   }

   public ByteBuf setIntLE(int var1, int var2) {
      this.checkIndex0(var1, 4);
      this.unwrap().setIntLE(this.idx(var1), var2);
      return this;
   }

   protected void _setIntLE(int var1, int var2) {
      this.unwrap()._setIntLE(this.idx(var1), var2);
   }

   public ByteBuf setLong(int var1, long var2) {
      this.checkIndex0(var1, 8);
      this.unwrap().setLong(this.idx(var1), var2);
      return this;
   }

   protected void _setLong(int var1, long var2) {
      this.unwrap()._setLong(this.idx(var1), var2);
   }

   public ByteBuf setLongLE(int var1, long var2) {
      this.checkIndex0(var1, 8);
      this.unwrap().setLongLE(this.idx(var1), var2);
      return this;
   }

   protected void _setLongLE(int var1, long var2) {
      this.unwrap().setLongLE(this.idx(var1), var2);
   }

   public ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkIndex0(var1, var4);
      this.unwrap().setBytes(this.idx(var1), var2, var3, var4);
      return this;
   }

   public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkIndex0(var1, var4);
      this.unwrap().setBytes(this.idx(var1), var2, var3, var4);
      return this;
   }

   public ByteBuf setBytes(int var1, ByteBuffer var2) {
      this.checkIndex0(var1, var2.remaining());
      this.unwrap().setBytes(this.idx(var1), (ByteBuffer)var2);
      return this;
   }

   public ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      this.checkIndex0(var1, var3);
      this.unwrap().getBytes(this.idx(var1), var2, var3);
      return this;
   }

   public int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      this.checkIndex0(var1, var3);
      return this.unwrap().getBytes(this.idx(var1), var2, var3);
   }

   public int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      this.checkIndex0(var1, var5);
      return this.unwrap().getBytes(this.idx(var1), var2, var3, var5);
   }

   public int setBytes(int var1, InputStream var2, int var3) throws IOException {
      this.checkIndex0(var1, var3);
      return this.unwrap().setBytes(this.idx(var1), var2, var3);
   }

   public int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException {
      this.checkIndex0(var1, var3);
      return this.unwrap().setBytes(this.idx(var1), var2, var3);
   }

   public int setBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      this.checkIndex0(var1, var5);
      return this.unwrap().setBytes(this.idx(var1), var2, var3, var5);
   }

   public int forEachByte(int var1, int var2, ByteProcessor var3) {
      this.checkIndex0(var1, var2);
      int var4 = this.unwrap().forEachByte(this.idx(var1), var2, var3);
      return var4 < this.adjustment ? -1 : var4 - this.adjustment;
   }

   public int forEachByteDesc(int var1, int var2, ByteProcessor var3) {
      this.checkIndex0(var1, var2);
      int var4 = this.unwrap().forEachByteDesc(this.idx(var1), var2, var3);
      return var4 < this.adjustment ? -1 : var4 - this.adjustment;
   }

   private int idx(int var1) {
      return var1 + this.adjustment;
   }

   // $FF: synthetic method
   PooledSlicedByteBuf(Recycler.Handle var1, Object var2) {
      this(var1);
   }
}
