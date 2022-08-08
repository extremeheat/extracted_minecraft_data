package com.mojang.blaze3d.platform;

import com.google.common.base.Charsets;
import java.nio.ByteBuffer;
import net.minecraft.util.StringDecomposer;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;

public class ClipboardManager {
   public static final int FORMAT_UNAVAILABLE = 65545;
   private final ByteBuffer clipboardScratchBuffer = BufferUtils.createByteBuffer(8192);

   public ClipboardManager() {
      super();
   }

   public String getClipboard(long var1, GLFWErrorCallbackI var3) {
      GLFWErrorCallback var4 = GLFW.glfwSetErrorCallback(var3);
      String var5 = GLFW.glfwGetClipboardString(var1);
      var5 = var5 != null ? StringDecomposer.filterBrokenSurrogates(var5) : "";
      GLFWErrorCallback var6 = GLFW.glfwSetErrorCallback(var4);
      if (var6 != null) {
         var6.free();
      }

      return var5;
   }

   private static void pushClipboard(long var0, ByteBuffer var2, byte[] var3) {
      var2.clear();
      var2.put(var3);
      var2.put((byte)0);
      var2.flip();
      GLFW.glfwSetClipboardString(var0, var2);
   }

   public void setClipboard(long var1, String var3) {
      byte[] var4 = var3.getBytes(Charsets.UTF_8);
      int var5 = var4.length + 1;
      if (var5 < this.clipboardScratchBuffer.capacity()) {
         pushClipboard(var1, this.clipboardScratchBuffer, var4);
      } else {
         ByteBuffer var6 = MemoryUtil.memAlloc(var5);

         try {
            pushClipboard(var1, var6, var4);
         } finally {
            MemoryUtil.memFree(var6);
         }
      }

   }
}
