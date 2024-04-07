package com.mojang.blaze3d.platform;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import org.lwjgl.system.MemoryUtil;

public class GlUtil {
   public GlUtil() {
      super();
   }

   public static ByteBuffer allocateMemory(int var0) {
      return MemoryUtil.memAlloc(var0);
   }

   public static void freeMemory(Buffer var0) {
      MemoryUtil.memFree(var0);
   }

   public static String getVendor() {
      return GlStateManager._getString(7936);
   }

   public static String getCpuInfo() {
      return GLX._getCpuInfo();
   }

   public static String getRenderer() {
      return GlStateManager._getString(7937);
   }

   public static String getOpenGLVersion() {
      return GlStateManager._getString(7938);
   }
}
