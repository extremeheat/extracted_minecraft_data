package com.mojang.blaze3d.platform;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class MemoryTracker {
   public static synchronized int genLists(int var0) {
      int var1 = GlStateManager.genLists(var0);
      if (var1 == 0) {
         int var2 = GlStateManager.getError();
         String var3 = "No error code reported";
         if (var2 != 0) {
            var3 = GLX.getErrorString(var2);
         }

         throw new IllegalStateException("glGenLists returned an ID of 0 for a count of " + var0 + ", GL error (" + var2 + "): " + var3);
      } else {
         return var1;
      }
   }

   public static synchronized void releaseLists(int var0, int var1) {
      GlStateManager.deleteLists(var0, var1);
   }

   public static synchronized void releaseList(int var0) {
      releaseLists(var0, 1);
   }

   public static synchronized ByteBuffer createByteBuffer(int var0) {
      return ByteBuffer.allocateDirect(var0).order(ByteOrder.nativeOrder());
   }

   public static FloatBuffer createFloatBuffer(int var0) {
      return createByteBuffer(var0 << 2).asFloatBuffer();
   }
}
