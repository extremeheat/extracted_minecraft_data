package com.mojang.blaze3d.platform;

import java.nio.ByteBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.MemoryUtil.MemoryAllocator;

public class MemoryTracker {
   private static final MemoryAllocator ALLOCATOR = MemoryUtil.getAllocator(false);

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
         throw new OutOfMemoryError("Failed to resize buffer from " + var0.capacity() + " bytes to " + var1 + " bytes");
      } else {
         return MemoryUtil.memByteBuffer(var2, var1);
      }
   }
}
