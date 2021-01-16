package io.netty.buffer;

import io.netty.util.Recycler;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

class PooledHeapByteBuf extends PooledByteBuf<byte[]> {
   private static final Recycler<PooledHeapByteBuf> RECYCLER = new Recycler<PooledHeapByteBuf>() {
      protected PooledHeapByteBuf newObject(Recycler.Handle<PooledHeapByteBuf> var1) {
         return new PooledHeapByteBuf(var1, 0);
      }
   };

   static PooledHeapByteBuf newInstance(int var0) {
      PooledHeapByteBuf var1 = (PooledHeapByteBuf)RECYCLER.get();
      var1.reuse(var0);
      return var1;
   }

   PooledHeapByteBuf(Recycler.Handle<? extends PooledHeapByteBuf> var1, int var2) {
      super(var1, var2);
   }

   public final boolean isDirect() {
      return false;
   }

   protected byte _getByte(int var1) {
      return HeapByteBufUtil.getByte((byte[])this.memory, this.idx(var1));
   }

   protected short _getShort(int var1) {
      return HeapByteBufUtil.getShort((byte[])this.memory, this.idx(var1));
   }

   protected short _getShortLE(int var1) {
      return HeapByteBufUtil.getShortLE((byte[])this.memory, this.idx(var1));
   }

   protected int _getUnsignedMedium(int var1) {
      return HeapByteBufUtil.getUnsignedMedium((byte[])this.memory, this.idx(var1));
   }

   protected int _getUnsignedMediumLE(int var1) {
      return HeapByteBufUtil.getUnsignedMediumLE((byte[])this.memory, this.idx(var1));
   }

   protected int _getInt(int var1) {
      return HeapByteBufUtil.getInt((byte[])this.memory, this.idx(var1));
   }

   protected int _getIntLE(int var1) {
      return HeapByteBufUtil.getIntLE((byte[])this.memory, this.idx(var1));
   }

   protected long _getLong(int var1) {
      return HeapByteBufUtil.getLong((byte[])this.memory, this.idx(var1));
   }

   protected long _getLongLE(int var1) {
      return HeapByteBufUtil.getLongLE((byte[])this.memory, this.idx(var1));
   }

   public final ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkDstIndex(var1, var4, var3, var2.capacity());
      if (var2.hasMemoryAddress()) {
         PlatformDependent.copyMemory((byte[])this.memory, this.idx(var1), var2.memoryAddress() + (long)var3, (long)var4);
      } else if (var2.hasArray()) {
         this.getBytes(var1, var2.array(), var2.arrayOffset() + var3, var4);
      } else {
         var2.setBytes(var3, (byte[])this.memory, this.idx(var1), var4);
      }

      return this;
   }

   public final ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkDstIndex(var1, var4, var3, var2.length);
      System.arraycopy(this.memory, this.idx(var1), var2, var3, var4);
      return this;
   }

   public final ByteBuf getBytes(int var1, ByteBuffer var2) {
      this.checkIndex(var1, var2.remaining());
      var2.put((byte[])this.memory, this.idx(var1), var2.remaining());
      return this;
   }

   public final ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      this.checkIndex(var1, var3);
      var2.write((byte[])this.memory, this.idx(var1), var3);
      return this;
   }

   public final int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      return this.getBytes(var1, var2, var3, false);
   }

   private int getBytes(int var1, GatheringByteChannel var2, int var3, boolean var4) throws IOException {
      this.checkIndex(var1, var3);
      var1 = this.idx(var1);
      ByteBuffer var5;
      if (var4) {
         var5 = this.internalNioBuffer();
      } else {
         var5 = ByteBuffer.wrap((byte[])this.memory);
      }

      return var2.write((ByteBuffer)var5.clear().position(var1).limit(var1 + var3));
   }

   public final int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      return this.getBytes(var1, var2, var3, var5, false);
   }

   private int getBytes(int var1, FileChannel var2, long var3, int var5, boolean var6) throws IOException {
      this.checkIndex(var1, var5);
      var1 = this.idx(var1);
      ByteBuffer var7 = var6 ? this.internalNioBuffer() : ByteBuffer.wrap((byte[])this.memory);
      return var2.write((ByteBuffer)var7.clear().position(var1).limit(var1 + var5), var3);
   }

   public final int readBytes(GatheringByteChannel var1, int var2) throws IOException {
      this.checkReadableBytes(var2);
      int var3 = this.getBytes(this.readerIndex, var1, var2, true);
      this.readerIndex += var3;
      return var3;
   }

   public final int readBytes(FileChannel var1, long var2, int var4) throws IOException {
      this.checkReadableBytes(var4);
      int var5 = this.getBytes(this.readerIndex, var1, var2, var4, true);
      this.readerIndex += var5;
      return var5;
   }

   protected void _setByte(int var1, int var2) {
      HeapByteBufUtil.setByte((byte[])this.memory, this.idx(var1), var2);
   }

   protected void _setShort(int var1, int var2) {
      HeapByteBufUtil.setShort((byte[])this.memory, this.idx(var1), var2);
   }

   protected void _setShortLE(int var1, int var2) {
      HeapByteBufUtil.setShortLE((byte[])this.memory, this.idx(var1), var2);
   }

   protected void _setMedium(int var1, int var2) {
      HeapByteBufUtil.setMedium((byte[])this.memory, this.idx(var1), var2);
   }

   protected void _setMediumLE(int var1, int var2) {
      HeapByteBufUtil.setMediumLE((byte[])this.memory, this.idx(var1), var2);
   }

   protected void _setInt(int var1, int var2) {
      HeapByteBufUtil.setInt((byte[])this.memory, this.idx(var1), var2);
   }

   protected void _setIntLE(int var1, int var2) {
      HeapByteBufUtil.setIntLE((byte[])this.memory, this.idx(var1), var2);
   }

   protected void _setLong(int var1, long var2) {
      HeapByteBufUtil.setLong((byte[])this.memory, this.idx(var1), var2);
   }

   protected void _setLongLE(int var1, long var2) {
      HeapByteBufUtil.setLongLE((byte[])this.memory, this.idx(var1), var2);
   }

   public final ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkSrcIndex(var1, var4, var3, var2.capacity());
      if (var2.hasMemoryAddress()) {
         PlatformDependent.copyMemory(var2.memoryAddress() + (long)var3, (byte[])this.memory, this.idx(var1), (long)var4);
      } else if (var2.hasArray()) {
         this.setBytes(var1, var2.array(), var2.arrayOffset() + var3, var4);
      } else {
         var2.getBytes(var3, (byte[])this.memory, this.idx(var1), var4);
      }

      return this;
   }

   public final ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkSrcIndex(var1, var4, var3, var2.length);
      System.arraycopy(var2, var3, this.memory, this.idx(var1), var4);
      return this;
   }

   public final ByteBuf setBytes(int var1, ByteBuffer var2) {
      int var3 = var2.remaining();
      this.checkIndex(var1, var3);
      var2.get((byte[])this.memory, this.idx(var1), var3);
      return this;
   }

   public final int setBytes(int var1, InputStream var2, int var3) throws IOException {
      this.checkIndex(var1, var3);
      return var2.read((byte[])this.memory, this.idx(var1), var3);
   }

   public final int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException {
      this.checkIndex(var1, var3);
      var1 = this.idx(var1);

      try {
         return var2.read((ByteBuffer)this.internalNioBuffer().clear().position(var1).limit(var1 + var3));
      } catch (ClosedChannelException var5) {
         return -1;
      }
   }

   public final int setBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      this.checkIndex(var1, var5);
      var1 = this.idx(var1);

      try {
         return var2.read((ByteBuffer)this.internalNioBuffer().clear().position(var1).limit(var1 + var5), var3);
      } catch (ClosedChannelException var7) {
         return -1;
      }
   }

   public final ByteBuf copy(int var1, int var2) {
      this.checkIndex(var1, var2);
      ByteBuf var3 = this.alloc().heapBuffer(var2, this.maxCapacity());
      var3.writeBytes((byte[])this.memory, this.idx(var1), var2);
      return var3;
   }

   public final int nioBufferCount() {
      return 1;
   }

   public final ByteBuffer[] nioBuffers(int var1, int var2) {
      return new ByteBuffer[]{this.nioBuffer(var1, var2)};
   }

   public final ByteBuffer nioBuffer(int var1, int var2) {
      this.checkIndex(var1, var2);
      var1 = this.idx(var1);
      ByteBuffer var3 = ByteBuffer.wrap((byte[])this.memory, var1, var2);
      return var3.slice();
   }

   public final ByteBuffer internalNioBuffer(int var1, int var2) {
      this.checkIndex(var1, var2);
      var1 = this.idx(var1);
      return (ByteBuffer)this.internalNioBuffer().clear().position(var1).limit(var1 + var2);
   }

   public final boolean hasArray() {
      return true;
   }

   public final byte[] array() {
      this.ensureAccessible();
      return (byte[])this.memory;
   }

   public final int arrayOffset() {
      return this.offset;
   }

   public final boolean hasMemoryAddress() {
      return false;
   }

   public final long memoryAddress() {
      throw new UnsupportedOperationException();
   }

   protected final ByteBuffer newInternalNioBuffer(byte[] var1) {
      return ByteBuffer.wrap(var1);
   }
}
