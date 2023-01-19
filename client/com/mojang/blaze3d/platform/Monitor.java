package com.mojang.blaze3d.platform;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Optional;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

public final class Monitor {
   private final long monitor;
   private final List<VideoMode> videoModes;
   private VideoMode currentMode;
   private int x;
   private int y;

   public Monitor(long var1) {
      super();
      this.monitor = var1;
      this.videoModes = Lists.newArrayList();
      this.refreshVideoModes();
   }

   public void refreshVideoModes() {
      RenderSystem.assertInInitPhase();
      this.videoModes.clear();
      Buffer var1 = GLFW.glfwGetVideoModes(this.monitor);

      for(int var2 = var1.limit() - 1; var2 >= 0; --var2) {
         var1.position(var2);
         VideoMode var3 = new VideoMode(var1);
         if (var3.getRedBits() >= 8 && var3.getGreenBits() >= 8 && var3.getBlueBits() >= 8) {
            this.videoModes.add(var3);
         }
      }

      int[] var5 = new int[1];
      int[] var6 = new int[1];
      GLFW.glfwGetMonitorPos(this.monitor, var5, var6);
      this.x = var5[0];
      this.y = var6[0];
      GLFWVidMode var4 = GLFW.glfwGetVideoMode(this.monitor);
      this.currentMode = new VideoMode(var4);
   }

   public VideoMode getPreferredVidMode(Optional<VideoMode> var1) {
      RenderSystem.assertInInitPhase();
      if (var1.isPresent()) {
         VideoMode var2 = (VideoMode)var1.get();

         for(VideoMode var4 : this.videoModes) {
            if (var4.equals(var2)) {
               return var4;
            }
         }
      }

      return this.getCurrentMode();
   }

   public int getVideoModeIndex(VideoMode var1) {
      RenderSystem.assertInInitPhase();
      return this.videoModes.indexOf(var1);
   }

   public VideoMode getCurrentMode() {
      return this.currentMode;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public VideoMode getMode(int var1) {
      return this.videoModes.get(var1);
   }

   public int getModeCount() {
      return this.videoModes.size();
   }

   public long getMonitor() {
      return this.monitor;
   }

   @Override
   public String toString() {
      return String.format("Monitor[%s %sx%s %s]", this.monitor, this.x, this.y, this.currentMode);
   }
}
