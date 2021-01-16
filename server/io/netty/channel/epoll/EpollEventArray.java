package io.netty.channel.epoll;

import io.netty.util.internal.PlatformDependent;

final class EpollEventArray {
   private static final int EPOLL_EVENT_SIZE = Native.sizeofEpollEvent();
   private static final int EPOLL_DATA_OFFSET = Native.offsetofEpollData();
   private long memoryAddress;
   private int length;

   EpollEventArray(int var1) {
      super();
      if (var1 < 1) {
         throw new IllegalArgumentException("length must be >= 1 but was " + var1);
      } else {
         this.length = var1;
         this.memoryAddress = allocate(var1);
      }
   }

   private static long allocate(int var0) {
      return PlatformDependent.allocateMemory((long)(var0 * EPOLL_EVENT_SIZE));
   }

   long memoryAddress() {
      return this.memoryAddress;
   }

   int length() {
      return this.length;
   }

   void increase() {
      this.length <<= 1;
      this.free();
      this.memoryAddress = allocate(this.length);
   }

   void free() {
      PlatformDependent.freeMemory(this.memoryAddress);
   }

   int events(int var1) {
      return PlatformDependent.getInt(this.memoryAddress + (long)(var1 * EPOLL_EVENT_SIZE));
   }

   int fd(int var1) {
      return PlatformDependent.getInt(this.memoryAddress + (long)(var1 * EPOLL_EVENT_SIZE) + (long)EPOLL_DATA_OFFSET);
   }
}
