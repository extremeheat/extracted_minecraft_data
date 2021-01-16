package io.netty.channel.unix;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

public final class IovArray implements ChannelOutboundBuffer.MessageProcessor {
   private static final int ADDRESS_SIZE = PlatformDependent.addressSize();
   private static final int IOV_SIZE;
   private static final int CAPACITY;
   private final long memoryAddress;
   private int count;
   private long size;
   private long maxBytes;

   public IovArray() {
      super();
      this.maxBytes = Limits.SSIZE_MAX;
      this.memoryAddress = PlatformDependent.allocateMemory((long)CAPACITY);
   }

   public void clear() {
      this.count = 0;
      this.size = 0L;
   }

   public boolean add(ByteBuf var1) {
      if (this.count == Limits.IOV_MAX) {
         return false;
      } else if (var1.hasMemoryAddress() && var1.nioBufferCount() == 1) {
         int var8 = var1.readableBytes();
         return var8 == 0 || this.add(var1.memoryAddress(), var1.readerIndex(), var8);
      } else {
         ByteBuffer[] var2 = var1.nioBuffers();
         ByteBuffer[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            ByteBuffer var6 = var3[var5];
            int var7 = var6.remaining();
            if (var7 != 0 && (!this.add(PlatformDependent.directBufferAddress(var6), var6.position(), var7) || this.count == Limits.IOV_MAX)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean add(long var1, int var3, int var4) {
      long var5 = this.memoryAddress(this.count);
      long var7 = var5 + (long)ADDRESS_SIZE;
      if (this.maxBytes - (long)var4 < this.size && this.count > 0) {
         return false;
      } else {
         this.size += (long)var4;
         ++this.count;
         if (ADDRESS_SIZE == 8) {
            PlatformDependent.putLong(var5, var1 + (long)var3);
            PlatformDependent.putLong(var7, (long)var4);
         } else {
            assert ADDRESS_SIZE == 4;

            PlatformDependent.putInt(var5, (int)var1 + var3);
            PlatformDependent.putInt(var7, var4);
         }

         return true;
      }
   }

   public int count() {
      return this.count;
   }

   public long size() {
      return this.size;
   }

   public void maxBytes(long var1) {
      this.maxBytes = Math.min(Limits.SSIZE_MAX, ObjectUtil.checkPositive(var1, "maxBytes"));
   }

   public long maxBytes() {
      return this.maxBytes;
   }

   public long memoryAddress(int var1) {
      return this.memoryAddress + (long)(IOV_SIZE * var1);
   }

   public void release() {
      PlatformDependent.freeMemory(this.memoryAddress);
   }

   public boolean processMessage(Object var1) throws Exception {
      return var1 instanceof ByteBuf && this.add((ByteBuf)var1);
   }

   static {
      IOV_SIZE = 2 * ADDRESS_SIZE;
      CAPACITY = Limits.IOV_MAX * IOV_SIZE;
   }
}
