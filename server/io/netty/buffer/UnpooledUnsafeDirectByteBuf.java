package io.netty.buffer;

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

public class UnpooledUnsafeDirectByteBuf extends AbstractReferenceCountedByteBuf {
   private final ByteBufAllocator alloc;
   private ByteBuffer tmpNioBuf;
   private int capacity;
   private boolean doNotFree;
   ByteBuffer buffer;
   long memoryAddress;

   public UnpooledUnsafeDirectByteBuf(ByteBufAllocator var1, int var2, int var3) {
      super(var3);
      if (var1 == null) {
         throw new NullPointerException("alloc");
      } else if (var2 < 0) {
         throw new IllegalArgumentException("initialCapacity: " + var2);
      } else if (var3 < 0) {
         throw new IllegalArgumentException("maxCapacity: " + var3);
      } else if (var2 > var3) {
         throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", var2, var3));
      } else {
         this.alloc = var1;
         this.setByteBuffer(this.allocateDirect(var2), false);
      }
   }

   protected UnpooledUnsafeDirectByteBuf(ByteBufAllocator var1, ByteBuffer var2, int var3) {
      this(var1, var2.slice(), var3, false);
   }

   UnpooledUnsafeDirectByteBuf(ByteBufAllocator var1, ByteBuffer var2, int var3, boolean var4) {
      super(var3);
      if (var1 == null) {
         throw new NullPointerException("alloc");
      } else if (var2 == null) {
         throw new NullPointerException("initialBuffer");
      } else if (!var2.isDirect()) {
         throw new IllegalArgumentException("initialBuffer is not a direct buffer.");
      } else if (var2.isReadOnly()) {
         throw new IllegalArgumentException("initialBuffer is a read-only buffer.");
      } else {
         int var5 = var2.remaining();
         if (var5 > var3) {
            throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", var5, var3));
         } else {
            this.alloc = var1;
            this.doNotFree = !var4;
            this.setByteBuffer(var2.order(ByteOrder.BIG_ENDIAN), false);
            this.writerIndex(var5);
         }
      }
   }

   protected ByteBuffer allocateDirect(int var1) {
      return ByteBuffer.allocateDirect(var1);
   }

   protected void freeDirect(ByteBuffer var1) {
      PlatformDependent.freeDirectBuffer(var1);
   }

   final void setByteBuffer(ByteBuffer var1, boolean var2) {
      if (var2) {
         ByteBuffer var3 = this.buffer;
         if (var3 != null) {
            if (this.doNotFree) {
               this.doNotFree = false;
            } else {
               this.freeDirect(var3);
            }
         }
      }

      this.buffer = var1;
      this.memoryAddress = PlatformDependent.directBufferAddress(var1);
      this.tmpNioBuf = null;
      this.capacity = var1.remaining();
   }

   public boolean isDirect() {
      return true;
   }

   public int capacity() {
      return this.capacity;
   }

   public ByteBuf capacity(int var1) {
      this.checkNewCapacity(var1);
      int var2 = this.readerIndex();
      int var3 = this.writerIndex();
      int var4 = this.capacity;
      ByteBuffer var5;
      ByteBuffer var6;
      if (var1 > var4) {
         var5 = this.buffer;
         var6 = this.allocateDirect(var1);
         var5.position(0).limit(var5.capacity());
         var6.position(0).limit(var5.capacity());
         var6.put(var5);
         var6.clear();
         this.setByteBuffer(var6, true);
      } else if (var1 < var4) {
         var5 = this.buffer;
         var6 = this.allocateDirect(var1);
         if (var2 < var1) {
            if (var3 > var1) {
               var3 = var1;
               this.writerIndex(var1);
            }

            var5.position(var2).limit(var3);
            var6.position(var2).limit(var3);
            var6.put(var5);
            var6.clear();
         } else {
            this.setIndex(var1, var1);
         }

         this.setByteBuffer(var6, true);
      }

      return this;
   }

   public ByteBufAllocator alloc() {
      return this.alloc;
   }

   public ByteOrder order() {
      return ByteOrder.BIG_ENDIAN;
   }

   public boolean hasArray() {
      return false;
   }

   public byte[] array() {
      throw new UnsupportedOperationException("direct buffer");
   }

   public int arrayOffset() {
      throw new UnsupportedOperationException("direct buffer");
   }

   public boolean hasMemoryAddress() {
      return true;
   }

   public long memoryAddress() {
      this.ensureAccessible();
      return this.memoryAddress;
   }

   protected byte _getByte(int var1) {
      return UnsafeByteBufUtil.getByte(this.addr(var1));
   }

   protected short _getShort(int var1) {
      return UnsafeByteBufUtil.getShort(this.addr(var1));
   }

   protected short _getShortLE(int var1) {
      return UnsafeByteBufUtil.getShortLE(this.addr(var1));
   }

   protected int _getUnsignedMedium(int var1) {
      return UnsafeByteBufUtil.getUnsignedMedium(this.addr(var1));
   }

   protected int _getUnsignedMediumLE(int var1) {
      return UnsafeByteBufUtil.getUnsignedMediumLE(this.addr(var1));
   }

   protected int _getInt(int var1) {
      return UnsafeByteBufUtil.getInt(this.addr(var1));
   }

   protected int _getIntLE(int var1) {
      return UnsafeByteBufUtil.getIntLE(this.addr(var1));
   }

   protected long _getLong(int var1) {
      return UnsafeByteBufUtil.getLong(this.addr(var1));
   }

   protected long _getLongLE(int var1) {
      return UnsafeByteBufUtil.getLongLE(this.addr(var1));
   }

   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      UnsafeByteBufUtil.getBytes(this, this.addr(var1), var1, (ByteBuf)var2, var3, var4);
      return this;
   }

   public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      UnsafeByteBufUtil.getBytes(this, this.addr(var1), var1, (byte[])var2, var3, var4);
      return this;
   }

   public ByteBuf getBytes(int var1, ByteBuffer var2) {
      UnsafeByteBufUtil.getBytes(this, this.addr(var1), var1, var2);
      return this;
   }

   public ByteBuf readBytes(ByteBuffer var1) {
      int var2 = var1.remaining();
      this.checkReadableBytes(var2);
      this.getBytes(this.readerIndex, var1);
      this.readerIndex += var2;
      return this;
   }

   protected void _setByte(int var1, int var2) {
      UnsafeByteBufUtil.setByte(this.addr(var1), var2);
   }

   protected void _setShort(int var1, int var2) {
      UnsafeByteBufUtil.setShort(this.addr(var1), var2);
   }

   protected void _setShortLE(int var1, int var2) {
      UnsafeByteBufUtil.setShortLE(this.addr(var1), var2);
   }

   protected void _setMedium(int var1, int var2) {
      UnsafeByteBufUtil.setMedium(this.addr(var1), var2);
   }

   protected void _setMediumLE(int var1, int var2) {
      UnsafeByteBufUtil.setMediumLE(this.addr(var1), var2);
   }

   protected void _setInt(int var1, int var2) {
      UnsafeByteBufUtil.setInt(this.addr(var1), var2);
   }

   protected void _setIntLE(int var1, int var2) {
      UnsafeByteBufUtil.setIntLE(this.addr(var1), var2);
   }

   protected void _setLong(int var1, long var2) {
      UnsafeByteBufUtil.setLong(this.addr(var1), var2);
   }

   protected void _setLongLE(int var1, long var2) {
      UnsafeByteBufUtil.setLongLE(this.addr(var1), var2);
   }

   public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      UnsafeByteBufUtil.setBytes(this, this.addr(var1), var1, (ByteBuf)var2, var3, var4);
      return this;
   }

   public ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      UnsafeByteBufUtil.setBytes(this, this.addr(var1), var1, (byte[])var2, var3, var4);
      return this;
   }

   public ByteBuf setBytes(int var1, ByteBuffer var2) {
      UnsafeByteBufUtil.setBytes(this, this.addr(var1), var1, var2);
      return this;
   }

   public ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      UnsafeByteBufUtil.getBytes(this, this.addr(var1), var1, var2, var3);
      return this;
   }

   public int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      return this.getBytes(var1, var2, var3, false);
   }

   private int getBytes(int var1, GatheringByteChannel var2, int var3, boolean var4) throws IOException {
      this.ensureAccessible();
      if (var3 == 0) {
         return 0;
      } else {
         ByteBuffer var5;
         if (var4) {
            var5 = this.internalNioBuffer();
         } else {
            var5 = this.buffer.duplicate();
         }

         var5.clear().position(var1).limit(var1 + var3);
         return var2.write(var5);
      }
   }

   public int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      return this.getBytes(var1, var2, var3, var5, false);
   }

   private int getBytes(int var1, FileChannel var2, long var3, int var5, boolean var6) throws IOException {
      this.ensureAccessible();
      if (var5 == 0) {
         return 0;
      } else {
         ByteBuffer var7 = var6 ? this.internalNioBuffer() : this.buffer.duplicate();
         var7.clear().position(var1).limit(var1 + var5);
         return var2.write(var7, var3);
      }
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

   public int setBytes(int var1, InputStream var2, int var3) throws IOException {
      return UnsafeByteBufUtil.setBytes(this, this.addr(var1), var1, var2, var3);
   }

   public int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException {
      this.ensureAccessible();
      ByteBuffer var4 = this.internalNioBuffer();
      var4.clear().position(var1).limit(var1 + var3);

      try {
         return var2.read(var4);
      } catch (ClosedChannelException var6) {
         return -1;
      }
   }

   public int setBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      this.ensureAccessible();
      ByteBuffer var6 = this.internalNioBuffer();
      var6.clear().position(var1).limit(var1 + var5);

      try {
         return var2.read(var6, var3);
      } catch (ClosedChannelException var8) {
         return -1;
      }
   }

   public int nioBufferCount() {
      return 1;
   }

   public ByteBuffer[] nioBuffers(int var1, int var2) {
      return new ByteBuffer[]{this.nioBuffer(var1, var2)};
   }

   public ByteBuf copy(int var1, int var2) {
      return UnsafeByteBufUtil.copy(this, this.addr(var1), var1, var2);
   }

   public ByteBuffer internalNioBuffer(int var1, int var2) {
      this.checkIndex(var1, var2);
      return (ByteBuffer)this.internalNioBuffer().clear().position(var1).limit(var1 + var2);
   }

   private ByteBuffer internalNioBuffer() {
      ByteBuffer var1 = this.tmpNioBuf;
      if (var1 == null) {
         this.tmpNioBuf = var1 = this.buffer.duplicate();
      }

      return var1;
   }

   public ByteBuffer nioBuffer(int var1, int var2) {
      this.checkIndex(var1, var2);
      return ((ByteBuffer)this.buffer.duplicate().position(var1).limit(var1 + var2)).slice();
   }

   protected void deallocate() {
      ByteBuffer var1 = this.buffer;
      if (var1 != null) {
         this.buffer = null;
         if (!this.doNotFree) {
            this.freeDirect(var1);
         }

      }
   }

   public ByteBuf unwrap() {
      return null;
   }

   long addr(int var1) {
      return this.memoryAddress + (long)var1;
   }

   protected SwappedByteBuf newSwappedByteBuf() {
      return (SwappedByteBuf)(PlatformDependent.isUnaligned() ? new UnsafeDirectSwappedByteBuf(this) : super.newSwappedByteBuf());
   }

   public ByteBuf setZero(int var1, int var2) {
      this.checkIndex(var1, var2);
      UnsafeByteBufUtil.setZero(this.addr(var1), var2);
      return this;
   }

   public ByteBuf writeZero(int var1) {
      this.ensureWritable(var1);
      int var2 = this.writerIndex;
      UnsafeByteBufUtil.setZero(this.addr(var2), var1);
      this.writerIndex = var2 + var1;
      return this;
   }
}
