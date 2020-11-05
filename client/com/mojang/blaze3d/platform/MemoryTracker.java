package com.mojang.blaze3d.platform;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class MemoryTracker {
   public static synchronized ByteBuffer createByteBuffer(int var0) {
      return ByteBuffer.allocateDirect(var0).order(ByteOrder.nativeOrder());
   }

   public static FloatBuffer createFloatBuffer(int var0) {
      return createByteBuffer(var0 << 2).asFloatBuffer();
   }
}
