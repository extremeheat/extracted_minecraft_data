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

final class PooledDuplicatedByteBuf extends AbstractPooledDerivedByteBuf {
   private static final Recycler<PooledDuplicatedByteBuf> RECYCLER = new Recycler<PooledDuplicatedByteBuf>() {
      protected PooledDuplicatedByteBuf newObject(Recycler.Handle<PooledDuplicatedByteBuf> var1) {
         return new PooledDuplicatedByteBuf(var1);
      }
   };

   static PooledDuplicatedByteBuf newInstance(AbstractByteBuf var0, ByteBuf var1, int var2, int var3) {
      PooledDuplicatedByteBuf var4 = (PooledDuplicatedByteBuf)RECYCLER.get();
      var4.init(var0, var1, var2, var3, var0.maxCapacity());
      var4.markReaderIndex();
      var4.markWriterIndex();
      return var4;
   }

   private PooledDuplicatedByteBuf(Recycler.Handle<PooledDuplicatedByteBuf> var1) {
      super(var1);
   }

   public int capacity() {
      return this.unwrap().capacity();
   }

   public ByteBuf capacity(int var1) {
      this.unwrap().capacity(var1);
      return this;
   }

   public int arrayOffset() {
      return this.unwrap().arrayOffset();
   }

   public long memoryAddress() {
      return this.unwrap().memoryAddress();
   }

   public ByteBuffer nioBuffer(int var1, int var2) {
      return this.unwrap().nioBuffer(var1, var2);
   }

   public ByteBuffer[] nioBuffers(int var1, int var2) {
      return this.unwrap().nioBuffers(var1, var2);
   }

   public ByteBuf copy(int var1, int var2) {
      return this.unwrap().copy(var1, var2);
   }

   public ByteBuf retainedSlice(int var1, int var2) {
      return PooledSlicedByteBuf.newInstance(this.unwrap(), this, var1, var2);
   }

   public ByteBuf duplicate() {
      return this.duplicate0().setIndex(this.readerIndex(), this.writerIndex());
   }

   public ByteBuf retainedDuplicate() {
      return newInstance(this.unwrap(), this, this.readerIndex(), this.writerIndex());
   }

   public byte getByte(int var1) {
      return this.unwrap().getByte(var1);
   }

   protected byte _getByte(int var1) {
      return this.unwrap()._getByte(var1);
   }

   public short getShort(int var1) {
      return this.unwrap().getShort(var1);
   }

   protected short _getShort(int var1) {
      return this.unwrap()._getShort(var1);
   }

   public short getShortLE(int var1) {
      return this.unwrap().getShortLE(var1);
   }

   protected short _getShortLE(int var1) {
      return this.unwrap()._getShortLE(var1);
   }

   public int getUnsignedMedium(int var1) {
      return this.unwrap().getUnsignedMedium(var1);
   }

   protected int _getUnsignedMedium(int var1) {
      return this.unwrap()._getUnsignedMedium(var1);
   }

   public int getUnsignedMediumLE(int var1) {
      return this.unwrap().getUnsignedMediumLE(var1);
   }

   protected int _getUnsignedMediumLE(int var1) {
      return this.unwrap()._getUnsignedMediumLE(var1);
   }

   public int getInt(int var1) {
      return this.unwrap().getInt(var1);
   }

   protected int _getInt(int var1) {
      return this.unwrap()._getInt(var1);
   }

   public int getIntLE(int var1) {
      return this.unwrap().getIntLE(var1);
   }

   protected int _getIntLE(int var1) {
      return this.unwrap()._getIntLE(var1);
   }

   public long getLong(int var1) {
      return this.unwrap().getLong(var1);
   }

   protected long _getLong(int var1) {
      return this.unwrap()._getLong(var1);
   }

   public long getLongLE(int var1) {
      return this.unwrap().getLongLE(var1);
   }

   protected long _getLongLE(int var1) {
      return this.unwrap()._getLongLE(var1);
   }

   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.unwrap().getBytes(var1, var2, var3, var4);
      return this;
   }

   public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      this.unwrap().getBytes(var1, var2, var3, var4);
      return this;
   }

   public ByteBuf getBytes(int var1, ByteBuffer var2) {
      this.unwrap().getBytes(var1, (ByteBuffer)var2);
      return this;
   }

   public ByteBuf setByte(int var1, int var2) {
      this.unwrap().setByte(var1, var2);
      return this;
   }

   protected void _setByte(int var1, int var2) {
      this.unwrap()._setByte(var1, var2);
   }

   public ByteBuf setShort(int var1, int var2) {
      this.unwrap().setShort(var1, var2);
      return this;
   }

   protected void _setShort(int var1, int var2) {
      this.unwrap()._setShort(var1, var2);
   }

   public ByteBuf setShortLE(int var1, int var2) {
      this.unwrap().setShortLE(var1, var2);
      return this;
   }

   protected void _setShortLE(int var1, int var2) {
      this.unwrap()._setShortLE(var1, var2);
   }

   public ByteBuf setMedium(int var1, int var2) {
      this.unwrap().setMedium(var1, var2);
      return this;
   }

   protected void _setMedium(int var1, int var2) {
      this.unwrap()._setMedium(var1, var2);
   }

   public ByteBuf setMediumLE(int var1, int var2) {
      this.unwrap().setMediumLE(var1, var2);
      return this;
   }

   protected void _setMediumLE(int var1, int var2) {
      this.unwrap()._setMediumLE(var1, var2);
   }

   public ByteBuf setInt(int var1, int var2) {
      this.unwrap().setInt(var1, var2);
      return this;
   }

   protected void _setInt(int var1, int var2) {
      this.unwrap()._setInt(var1, var2);
   }

   public ByteBuf setIntLE(int var1, int var2) {
      this.unwrap().setIntLE(var1, var2);
      return this;
   }

   protected void _setIntLE(int var1, int var2) {
      this.unwrap()._setIntLE(var1, var2);
   }

   public ByteBuf setLong(int var1, long var2) {
      this.unwrap().setLong(var1, var2);
      return this;
   }

   protected void _setLong(int var1, long var2) {
      this.unwrap()._setLong(var1, var2);
   }

   public ByteBuf setLongLE(int var1, long var2) {
      this.unwrap().setLongLE(var1, var2);
      return this;
   }

   protected void _setLongLE(int var1, long var2) {
      this.unwrap().setLongLE(var1, var2);
   }

   public ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      this.unwrap().setBytes(var1, var2, var3, var4);
      return this;
   }

   public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.unwrap().setBytes(var1, var2, var3, var4);
      return this;
   }

   public ByteBuf setBytes(int var1, ByteBuffer var2) {
      this.unwrap().setBytes(var1, (ByteBuffer)var2);
      return this;
   }

   public ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      this.unwrap().getBytes(var1, var2, var3);
      return this;
   }

   public int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      return this.unwrap().getBytes(var1, var2, var3);
   }

   public int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      return this.unwrap().getBytes(var1, var2, var3, var5);
   }

   public int setBytes(int var1, InputStream var2, int var3) throws IOException {
      return this.unwrap().setBytes(var1, var2, var3);
   }

   public int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException {
      return this.unwrap().setBytes(var1, var2, var3);
   }

   public int setBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      return this.unwrap().setBytes(var1, var2, var3, var5);
   }

   public int forEachByte(int var1, int var2, ByteProcessor var3) {
      return this.unwrap().forEachByte(var1, var2, var3);
   }

   public int forEachByteDesc(int var1, int var2, ByteProcessor var3) {
      return this.unwrap().forEachByteDesc(var1, var2, var3);
   }

   // $FF: synthetic method
   PooledDuplicatedByteBuf(Recycler.Handle var1, Object var2) {
      this(var1);
   }
}
