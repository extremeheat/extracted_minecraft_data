package com.mojang.blaze3d;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

public class Blaze3D {
   public Blaze3D() {
      super();
   }

   public static void process(RenderPipeline var0, float var1) {
      ConcurrentLinkedQueue var2 = var0.getRecordingQueue();
   }

   public static void render(RenderPipeline var0, float var1) {
      ConcurrentLinkedQueue var2 = var0.getProcessedQueue();
   }

   public static void youJustLostTheGame() {
      MemoryUtil.memSet(0L, 0, 1L);
   }

   public static double getTime() {
      return GLFW.glfwGetTime();
   }
}
