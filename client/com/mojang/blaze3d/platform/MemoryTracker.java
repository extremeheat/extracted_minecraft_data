package com.mojang.blaze3d.platform;

import java.nio.ByteBuffer;
import org.lwjgl.system.MemoryUtil;

public class MemoryTracker {
   private static final MemoryUtil.MemoryAllocator ALLOCATOR = MemoryUtil.getAllocator(false);

   public MemoryTracker() {
      super();
   }

   public static ByteBuffer create(int var0) {
      long var1 = ALLOCATOR.malloc((long)var0);
      if (var1 == 0L) {
         throw new OutOfMemoryError("Failed to allocate " + var0 + " bytes");
      } else {
         return MemoryUtil.memByteBuffer(var1, var0);
      }
   }

   public static ByteBuffer resize(ByteBuffer var0, int var1) {
      long var2 = ALLOCATOR.realloc(MemoryUtil.memAddress0(var0), (long)var1);
      if (var2 == 0L) {
         int var10002 = var0.capacity();
         throw new OutOfMemoryError("Failed to resize buffer from " + var10002 + " bytes to " + var1 + " bytes");
      } else {
         return MemoryUtil.memByteBuffer(var2, var1);
      }
   }

   public static void free(ByteBuffer var0) {
      ALLOCATOR.free(MemoryUtil.memAddress0(var0));
   }
}
