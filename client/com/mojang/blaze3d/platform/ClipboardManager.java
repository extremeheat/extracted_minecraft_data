package com.mojang.blaze3d.platform;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
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

   public String getClipboard(Window var1, GLFWErrorCallbackI var2) {
      GLFWErrorCallback var3 = GLFW.glfwSetErrorCallback(var2);
      String var4 = GLFW.glfwGetClipboardString(var1.handle());
      var4 = var4 != null ? StringDecomposer.filterBrokenSurrogates(var4) : "";
      GLFWErrorCallback var5 = GLFW.glfwSetErrorCallback(var3);
      if (var5 != null) {
         var5.free();
      }

      return var4;
   }

   private static void pushClipboard(Window var0, ByteBuffer var1, byte[] var2) {
      var1.clear();
      var1.put(var2);
      var1.put((byte)0);
      var1.flip();
      GLFW.glfwSetClipboardString(var0.handle(), var1);
   }

   public void setClipboard(Window var1, String var2) {
      byte[] var3 = var2.getBytes(StandardCharsets.UTF_8);
      int var4 = var3.length + 1;
      if (var4 < this.clipboardScratchBuffer.capacity()) {
         pushClipboard(var1, this.clipboardScratchBuffer, var3);
      } else {
         ByteBuffer var5 = MemoryUtil.memAlloc(var4);

         try {
            pushClipboard(var1, var5, var3);
         } finally {
            MemoryUtil.memFree(var5);
         }
      }

   }
}
