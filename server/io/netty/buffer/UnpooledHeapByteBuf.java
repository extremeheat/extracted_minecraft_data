package io.netty.buffer;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public class UnpooledHeapByteBuf extends AbstractReferenceCountedByteBuf {
   private final ByteBufAllocator alloc;
   byte[] array;
   private ByteBuffer tmpNioBuf;

   public UnpooledHeapByteBuf(ByteBufAllocator var1, int var2, int var3) {
      super(var3);
      ObjectUtil.checkNotNull(var1, "alloc");
      if (var2 > var3) {
         throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", var2, var3));
      } else {
         this.alloc = var1;
         this.setArray(this.allocateArray(var2));
         this.setIndex(0, 0);
      }
   }

   protected UnpooledHeapByteBuf(ByteBufAllocator var1, byte[] var2, int var3) {
      super(var3);
      ObjectUtil.checkNotNull(var1, "alloc");
      ObjectUtil.checkNotNull(var2, "initialArray");
      if (var2.length > var3) {
         throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", var2.length, var3));
      } else {
         this.alloc = var1;
         this.setArray(var2);
         this.setIndex(0, var2.length);
      }
   }

   byte[] allocateArray(int var1) {
      return new byte[var1];
   }

   void freeArray(byte[] var1) {
   }

   private void setArray(byte[] var1) {
      this.array = var1;
      this.tmpNioBuf = null;
   }

   public ByteBufAllocator alloc() {
      return this.alloc;
   }

   public ByteOrder order() {
      return ByteOrder.BIG_ENDIAN;
   }

   public boolean isDirect() {
      return false;
   }

   public int capacity() {
      return this.array.length;
   }

   public ByteBuf capacity(int var1) {
      this.checkNewCapacity(var1);
      int var2 = this.array.length;
      byte[] var3 = this.array;
      byte[] var4;
      if (var1 > var2) {
         var4 = this.allocateArray(var1);
         System.arraycopy(var3, 0, var4, 0, var3.length);
         this.setArray(var4);
         this.freeArray(var3);
      } else if (var1 < var2) {
         var4 = this.allocateArray(var1);
         int var5 = this.readerIndex();
         if (var5 < var1) {
            int var6 = this.writerIndex();
            if (var6 > var1) {
               var6 = var1;
               this.writerIndex(var1);
            }

            System.arraycopy(var3, var5, var4, var5, var6 - var5);
         } else {
            this.setIndex(var1, var1);
         }

         this.setArray(var4);
         this.freeArray(var3);
      }

      return this;
   }

   public boolean hasArray() {
      return true;
   }

   public byte[] array() {
      this.ensureAccessible();
      return this.array;
   }

   public int arrayOffset() {
      return 0;
   }

   public boolean hasMemoryAddress() {
      return false;
   }

   public long memoryAddress() {
      throw new UnsupportedOperationException();
   }

   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkDstIndex(var1, var4, var3, var2.capacity());
      if (var2.hasMemoryAddress()) {
         PlatformDependent.copyMemory(this.array, var1, var2.memoryAddress() + (long)var3, (long)var4);
      } else if (var2.hasArray()) {
         this.getBytes(var1, var2.array(), var2.arrayOffset() + var3, var4);
      } else {
         var2.setBytes(var3, this.array, var1, var4);
      }

      return this;
   }

   public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkDstIndex(var1, var4, var3, var2.length);
      System.arraycopy(this.array, var1, var2, var3, var4);
      return this;
   }

   public ByteBuf getBytes(int var1, ByteBuffer var2) {
      this.checkIndex(var1, var2.remaining());
      var2.put(this.array, var1, var2.remaining());
      return this;
   }

   public ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      this.ensureAccessible();
      var2.write(this.array, var1, var3);
      return this;
   }

   public int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      this.ensureAccessible();
      return this.getBytes(var1, var2, var3, false);
   }

   public int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      this.ensureAccessible();
      return this.getBytes(var1, var2, var3, var5, false);
   }

   private int getBytes(int var1, GatheringByteChannel var2, int var3, boolean var4) throws IOException {
      this.ensureAccessible();
      ByteBuffer var5;
      if (var4) {
         var5 = this.internalNioBuffer();
      } else {
         var5 = ByteBuffer.wrap(this.array);
      }

      return var2.write((ByteBuffer)var5.clear().position(var1).limit(var1 + var3));
   }

   private int getBytes(int var1, FileChannel var2, long var3, int var5, boolean var6) throws IOException {
      this.ensureAccessible();
      ByteBuffer var7 = var6 ? this.internalNioBuffer() : ByteBuffer.wrap(this.array);
      return var2.write((ByteBuffer)var7.clear().position(var1).limit(var1 + var5), var3);
   }

   public int readBytes(GatheringByteChannel var1, int var2) throws IOException {
      this.checkReadableBytes(var2);
      int var3 = this.getBytes(this.readerIndex, var1, var2, true);
      this.readerIndex += var3;
      return var3;
   }

   public int readBytes(FileChannel var1, long var2, int var4) throws IOException {
      this.checkReadableBytes(var4);
      int var5 = this.getBytes(this.readerIndex, var1, var2, var4, true);
      this.readerIndex += var5;
      return var5;
   }

   public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkSrcIndex(var1, var4, var3, var2.capacity());
      if (var2.hasMemoryAddress()) {
         PlatformDependent.copyMemory(var2.memoryAddress() + (long)var3, this.array, var1, (long)var4);
      } else if (var2.hasArray()) {
         this.setBytes(var1, var2.array(), var2.arrayOffset() + var3, var4);
      } else {
         var2.getBytes(var3, this.array, var1, var4);
      }

      return this;
   }

   public ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkSrcIndex(var1, var4, var3, var2.length);
      System.arraycopy(var2, var3, this.array, var1, var4);
      return this;
   }

   public ByteBuf setBytes(int var1, ByteBuffer var2) {
      this.ensureAccessible();
      var2.get(this.array, var1, var2.remaining());
      return this;
   }

   public int setBytes(int var1, InputStream var2, int var3) throws IOException {
      this.ensureAccessible();
      return var2.read(this.array, var1, var3);
   }

   public int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException {
      this.ensureAccessible();

      try {
         return var2.read((ByteBuffer)this.internalNioBuffer().clear().position(var1).limit(var1 + var3));
      } catch (ClosedChannelException var5) {
         return -1;
      }
   }

   public int setBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      this.ensureAccessible();

      try {
         return var2.read((ByteBuffer)this.internalNioBuffer().clear().position(var1).limit(var1 + var5), var3);
      } catch (ClosedChannelException var7) {
         return -1;
      }
   }

   public int nioBufferCount() {
      return 1;
   }

   public ByteBuffer nioBuffer(int var1, int var2) {
      this.ensureAccessible();
      return ByteBuffer.wrap(this.array, var1, var2).slice();
   }

   public ByteBuffer[] nioBuffers(int var1, int var2) {
      return new ByteBuffer[]{this.nioBuffer(var1, var2)};
   }

   public ByteBuffer internalNioBuffer(int var1, int var2) {
      this.checkIndex(var1, var2);
      return (ByteBuffer)this.internalNioBuffer().clear().position(var1).limit(var1 + var2);
   }

   public byte getByte(int var1) {
      this.ensureAccessible();
      return this._getByte(var1);
   }

   protected byte _getByte(int var1) {
      return HeapByteBufUtil.getByte(this.array, var1);
   }

   public short getShort(int var1) {
      this.ensureAccessible();
      return this._getShort(var1);
   }

   protected short _getShort(int var1) {
      return HeapByteBufUtil.getShort(this.array, var1);
   }

   public short getShortLE(int var1) {
      this.ensureAccessible();
      return this._getShortLE(var1);
   }

   protected short _getShortLE(int var1) {
      return HeapByteBufUtil.getShortLE(this.array, var1);
   }

   public int getUnsignedMedium(int var1) {
      this.ensureAccessible();
      return this._getUnsignedMedium(var1);
   }

   protected int _getUnsignedMedium(int var1) {
      return HeapByteBufUtil.getUnsignedMedium(this.array, var1);
   }

   public int getUnsignedMediumLE(int var1) {
      this.ensureAccessible();
      return this._getUnsignedMediumLE(var1);
   }

   protected int _getUnsignedMediumLE(int var1) {
      return HeapByteBufUtil.getUnsignedMediumLE(this.array, var1);
   }

   public int getInt(int var1) {
      this.ensureAccessible();
      return this._getInt(var1);
   }

   protected int _getInt(int var1) {
      return HeapByteBufUtil.getInt(this.array, var1);
   }

   public int getIntLE(int var1) {
      this.ensureAccessible();
      return this._getIntLE(var1);
   }

   protected int _getIntLE(int var1) {
      return HeapByteBufUtil.getIntLE(this.array, var1);
   }

   public long getLong(int var1) {
      this.ensureAccessible();
      return this._getLong(var1);
   }

   protected long _getLong(int var1) {
      return HeapByteBufUtil.getLong(this.array, var1);
   }

   public long getLongLE(int var1) {
      this.ensureAccessible();
      return this._getLongLE(var1);
   }

   protected long _getLongLE(int var1) {
      return HeapByteBufUtil.getLongLE(this.array, var1);
   }

   public ByteBuf setByte(int var1, int var2) {
      this.ensureAccessible();
      this._setByte(var1, var2);
      return this;
   }

   protected void _setByte(int var1, int var2) {
      HeapByteBufUtil.setByte(this.array, var1, var2);
   }

   public ByteBuf setShort(int var1, int var2) {
      this.ensureAccessible();
      this._setShort(var1, var2);
      return this;
   }

   protected void _setShort(int var1, int var2) {
      HeapByteBufUtil.setShort(this.array, var1, var2);
   }

   public ByteBuf setShortLE(int var1, int var2) {
      this.ensureAccessible();
      this._setShortLE(var1, var2);
      return this;
   }

   protected void _setShortLE(int var1, int var2) {
      HeapByteBufUtil.setShortLE(this.array, var1, var2);
   }

   public ByteBuf setMedium(int var1, int var2) {
      this.ensureAccessible();
      this._setMedium(var1, var2);
      return this;
   }

   protected void _setMedium(int var1, int var2) {
      HeapByteBufUtil.setMedium(this.array, var1, var2);
   }

   public ByteBuf setMediumLE(int var1, int var2) {
      this.ensureAccessible();
      this._setMediumLE(var1, var2);
      return this;
   }

   protected void _setMediumLE(int var1, int var2) {
      HeapByteBufUtil.setMediumLE(this.array, var1, var2);
   }

   public ByteBuf setInt(int var1, int var2) {
      this.ensureAccessible();
      this._setInt(var1, var2);
      return this;
   }

   protected void _setInt(int var1, int var2) {
      HeapByteBufUtil.setInt(this.array, var1, var2);
   }

   public ByteBuf setIntLE(int var1, int var2) {
      this.ensureAccessible();
      this._setIntLE(var1, var2);
      return this;
   }

   protected void _setIntLE(int var1, int var2) {
      HeapByteBufUtil.setIntLE(this.array, var1, var2);
   }

   public ByteBuf setLong(int var1, long var2) {
      this.ensureAccessible();
      this._setLong(var1, var2);
      return this;
   }

   protected void _setLong(int var1, long var2) {
      HeapByteBufUtil.setLong(this.array, var1, var2);
   }

   public ByteBuf setLongLE(int var1, long var2) {
      this.ensureAccessible();
      this._setLongLE(var1, var2);
      return this;
   }

   protected void _setLongLE(int var1, long var2) {
      HeapByteBufUtil.setLongLE(this.array, var1, var2);
   }

   public ByteBuf copy(int var1, int var2) {
      this.checkIndex(var1, var2);
      byte[] var3 = new byte[var2];
      System.arraycopy(this.array, var1, var3, 0, var2);
      return new UnpooledHeapByteBuf(this.alloc(), var3, this.maxCapacity());
   }

   private ByteBuffer internalNioBuffer() {
      ByteBuffer var1 = this.tmpNioBuf;
      if (var1 == null) {
         this.tmpNioBuf = var1 = ByteBuffer.wrap(this.array);
      }

      return var1;
   }

   protected void deallocate() {
      this.freeArray(this.array);
      this.array = EmptyArrays.EMPTY_BYTES;
   }

   public ByteBuf unwrap() {
      return null;
   }
}
