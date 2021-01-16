package io.netty.channel.kqueue;

import io.netty.util.internal.PlatformDependent;

final class KQueueEventArray {
   private static final int KQUEUE_EVENT_SIZE = Native.sizeofKEvent();
   private static final int KQUEUE_IDENT_OFFSET = Native.offsetofKEventIdent();
   private static final int KQUEUE_FILTER_OFFSET = Native.offsetofKEventFilter();
   private static final int KQUEUE_FFLAGS_OFFSET = Native.offsetofKEventFFlags();
   private static final int KQUEUE_FLAGS_OFFSET = Native.offsetofKEventFlags();
   private static final int KQUEUE_DATA_OFFSET = Native.offsetofKeventData();
   private long memoryAddress;
   private int size;
   private int capacity;

   KQueueEventArray(int var1) {
      super();
      if (var1 < 1) {
         throw new IllegalArgumentException("capacity must be >= 1 but was " + var1);
      } else {
         this.memoryAddress = PlatformDependent.allocateMemory((long)(var1 * KQUEUE_EVENT_SIZE));
         this.capacity = var1;
      }
   }

   long memoryAddress() {
      return this.memoryAddress;
   }

   int capacity() {
      return this.capacity;
   }

   int size() {
      return this.size;
   }

   void clear() {
      this.size = 0;
   }

   void evSet(AbstractKQueueChannel var1, short var2, short var3, int var4) {
      this.checkSize();
      evSet(this.getKEventOffset(this.size++), var1, var1.socket.intValue(), var2, var3, var4);
   }

   private void checkSize() {
      if (this.size == this.capacity) {
         this.realloc(true);
      }

   }

   void realloc(boolean var1) {
      int var2 = this.capacity <= 65536 ? this.capacity << 1 : this.capacity + this.capacity >> 1;
      long var3 = PlatformDependent.reallocateMemory(this.memoryAddress, (long)(var2 * KQUEUE_EVENT_SIZE));
      if (var3 != 0L) {
         this.memoryAddress = var3;
         this.capacity = var2;
      } else if (var1) {
         throw new OutOfMemoryError("unable to allocate " + var2 + " new bytes! Existing capacity is: " + this.capacity);
      }
   }

   void free() {
      PlatformDependent.freeMemory(this.memoryAddress);
      this.memoryAddress = (long)(this.size = this.capacity = 0);
   }

   long getKEventOffset(int var1) {
      return this.memoryAddress + (long)(var1 * KQUEUE_EVENT_SIZE);
   }

   short flags(int var1) {
      return PlatformDependent.getShort(this.getKEventOffset(var1) + (long)KQUEUE_FLAGS_OFFSET);
   }

   short filter(int var1) {
      return PlatformDependent.getShort(this.getKEventOffset(var1) + (long)KQUEUE_FILTER_OFFSET);
   }

   short fflags(int var1) {
      return PlatformDependent.getShort(this.getKEventOffset(var1) + (long)KQUEUE_FFLAGS_OFFSET);
   }

   int fd(int var1) {
      return PlatformDependent.getInt(this.getKEventOffset(var1) + (long)KQUEUE_IDENT_OFFSET);
   }

   long data(int var1) {
      return PlatformDependent.getLong(this.getKEventOffset(var1) + (long)KQUEUE_DATA_OFFSET);
   }

   AbstractKQueueChannel channel(int var1) {
      return getChannel(this.getKEventOffset(var1));
   }

   private static native void evSet(long var0, AbstractKQueueChannel var2, int var3, short var4, short var5, int var6);

   private static native AbstractKQueueChannel getChannel(long var0);

   static native void deleteGlobalRefs(long var0, long var2);
}
