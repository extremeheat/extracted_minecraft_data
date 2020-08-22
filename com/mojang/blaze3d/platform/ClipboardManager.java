package com.mojang.blaze3d.platform;

import java.nio.ByteBuffer;
import net.minecraft.SharedConstants;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;

public class ClipboardManager {
   private final ByteBuffer clipboardScratchBuffer = ByteBuffer.allocateDirect(1024);

   public String getClipboard(long var1, GLFWErrorCallbackI var3) {
      GLFWErrorCallback var4 = GLFW.glfwSetErrorCallback(var3);
      String var5 = GLFW.glfwGetClipboardString(var1);
      var5 = var5 != null ? SharedConstants.filterUnicodeSupplementary(var5) : "";
      GLFWErrorCallback var6 = GLFW.glfwSetErrorCallback(var4);
      if (var6 != null) {
         var6.free();
      }

      return var5;
   }

   private void setClipboard(long var1, ByteBuffer var3, String var4) {
      MemoryUtil.memUTF8(var4, true, var3);
      GLFW.glfwSetClipboardString(var1, var3);
   }

   public void setClipboard(long var1, String var3) {
      int var4 = MemoryUtil.memLengthUTF8(var3, true);
      if (var4 < this.clipboardScratchBuffer.capacity()) {
         this.setClipboard(var1, this.clipboardScratchBuffer, var3);
         this.clipboardScratchBuffer.clear();
      } else {
         ByteBuffer var5 = ByteBuffer.allocateDirect(var4);
         this.setClipboard(var1, var5, var3);
      }

   }
}
