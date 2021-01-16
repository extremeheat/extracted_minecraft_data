package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

final class ReadOnlyUnsafeDirectByteBuf extends ReadOnlyByteBufferBuf {
   private final long memoryAddress;

   ReadOnlyUnsafeDirectByteBuf(ByteBufAllocator var1, ByteBuffer var2) {
      super(var1, var2);
      this.memoryAddress = PlatformDependent.directBufferAddress(this.buffer);
   }

   protected byte _getByte(int var1) {
      return UnsafeByteBufUtil.getByte(this.addr(var1));
   }

   protected short _getShort(int var1) {
      return UnsafeByteBufUtil.getShort(this.addr(var1));
   }

   protected int _getUnsignedMedium(int var1) {
      return UnsafeByteBufUtil.getUnsignedMedium(this.addr(var1));
   }

   protected int _getInt(int var1) {
      return UnsafeByteBufUtil.getInt(this.addr(var1));
   }

   protected long _getLong(int var1) {
      return UnsafeByteBufUtil.getLong(this.addr(var1));
   }

   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkIndex(var1, var4);
      if (var2 == null) {
         throw new NullPointerException("dst");
      } else if (var3 >= 0 && var3 <= var2.capacity() - var4) {
         if (var2.hasMemoryAddress()) {
            PlatformDependent.copyMemory(this.addr(var1), var2.memoryAddress() + (long)var3, (long)var4);
         } else if (var2.hasArray()) {
            PlatformDependent.copyMemory(this.addr(var1), var2.array(), var2.arrayOffset() + var3, (long)var4);
         } else {
            var2.setBytes(var3, (ByteBuf)this, var1, var4);
         }

         return this;
      } else {
         throw new IndexOutOfBoundsException("dstIndex: " + var3);
      }
   }

   public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkIndex(var1, var4);
      if (var2 == null) {
         throw new NullPointerException("dst");
      } else if (var3 >= 0 && var3 <= var2.length - var4) {
         if (var4 != 0) {
            PlatformDependent.copyMemory(this.addr(var1), var2, var3, (long)var4);
         }

         return this;
      } else {
         throw new IndexOutOfBoundsException(String.format("dstIndex: %d, length: %d (expected: range(0, %d))", var3, var4, var2.length));
      }
   }

   public ByteBuf getBytes(int var1, ByteBuffer var2) {
      this.checkIndex(var1);
      if (var2 == null) {
         throw new NullPointerException("dst");
      } else {
         int var3 = Math.min(this.capacity() - var1, var2.remaining());
         ByteBuffer var4 = this.internalNioBuffer();
         var4.clear().position(var1).limit(var1 + var3);
         var2.put(var4);
         return this;
      }
   }

   public ByteBuf copy(int var1, int var2) {
      this.checkIndex(var1, var2);
      ByteBuf var3 = this.alloc().directBuffer(var2, this.maxCapacity());
      if (var2 != 0) {
         if (var3.hasMemoryAddress()) {
            PlatformDependent.copyMemory(this.addr(var1), var3.memoryAddress(), (long)var2);
            var3.setIndex(0, var2);
         } else {
            var3.writeBytes((ByteBuf)this, var1, var2);
         }
      }

      return var3;
   }

   public boolean hasMemoryAddress() {
      return true;
   }

   public long memoryAddress() {
      return this.memoryAddress;
   }

   private long addr(int var1) {
      return this.memoryAddress + (long)var1;
   }
}
