package io.netty.channel.kqueue;

import io.netty.channel.unix.Limits;
import io.netty.util.internal.PlatformDependent;

final class NativeLongArray {
   private long memoryAddress;
   private int capacity;
   private int size;

   NativeLongArray(int var1) {
      super();
      if (var1 < 1) {
         throw new IllegalArgumentException("capacity must be >= 1 but was " + var1);
      } else {
         this.memoryAddress = PlatformDependent.allocateMemory((long)(var1 * Limits.SIZEOF_JLONG));
         this.capacity = var1;
      }
   }

   void add(long var1) {
      this.checkSize();
      PlatformDependent.putLong(this.memoryOffset(this.size++), var1);
   }

   void clear() {
      this.size = 0;
   }

   boolean isEmpty() {
      return this.size == 0;
   }

   void free() {
      PlatformDependent.freeMemory(this.memoryAddress);
      this.memoryAddress = 0L;
   }

   long memoryAddress() {
      return this.memoryAddress;
   }

   long memoryAddressEnd() {
      return this.memoryOffset(this.size);
   }

   private long memoryOffset(int var1) {
      return this.memoryAddress + (long)(var1 * Limits.SIZEOF_JLONG);
   }

   private void checkSize() {
      if (this.size == this.capacity) {
         this.realloc();
      }

   }

   private void realloc() {
      int var1 = this.capacity <= 65536 ? this.capacity << 1 : this.capacity + this.capacity >> 1;
      long var2 = PlatformDependent.reallocateMemory(this.memoryAddress, (long)(var1 * Limits.SIZEOF_JLONG));
      if (var2 == 0L) {
         throw new OutOfMemoryError("unable to allocate " + var1 + " new bytes! Existing capacity is: " + this.capacity);
      } else {
         this.memoryAddress = var2;
         this.capacity = var1;
      }
   }

   public String toString() {
      return "memoryAddress: " + this.memoryAddress + " capacity: " + this.capacity + " size: " + this.size;
   }
}
