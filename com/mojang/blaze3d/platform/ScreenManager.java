package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallback;
import org.lwjgl.glfw.GLFWMonitorCallbackI;

public class ScreenManager {
   private final Long2ObjectMap monitors = new Long2ObjectOpenHashMap();
   private final MonitorCreator monitorCreator;

   public ScreenManager(MonitorCreator var1) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      this.monitorCreator = var1;
      GLFW.glfwSetMonitorCallback(this::onMonitorChange);
      PointerBuffer var2 = GLFW.glfwGetMonitors();
      if (var2 != null) {
         for(int var3 = 0; var3 < var2.limit(); ++var3) {
            long var4 = var2.get(var3);
            this.monitors.put(var4, var1.createMonitor(var4));
         }
      }

   }

   private void onMonitorChange(long var1, int var3) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (var3 == 262145) {
         this.monitors.put(var1, this.monitorCreator.createMonitor(var1));
      } else if (var3 == 262146) {
         this.monitors.remove(var1);
      }

   }

   @Nullable
   public Monitor getMonitor(long var1) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      return (Monitor)this.monitors.get(var1);
   }

   @Nullable
   public Monitor findBestMonitor(Window var1) {
      long var2 = GLFW.glfwGetWindowMonitor(var1.getWindow());
      if (var2 != 0L) {
         return this.getMonitor(var2);
      } else {
         int var4 = var1.getX();
         int var5 = var4 + var1.getScreenWidth();
         int var6 = var1.getY();
         int var7 = var6 + var1.getScreenHeight();
         int var8 = -1;
         Monitor var9 = null;
         ObjectIterator var10 = this.monitors.values().iterator();

         while(var10.hasNext()) {
            Monitor var11 = (Monitor)var10.next();
            int var12 = var11.getX();
            int var13 = var12 + var11.getCurrentMode().getWidth();
            int var14 = var11.getY();
            int var15 = var14 + var11.getCurrentMode().getHeight();
            int var16 = clamp(var4, var12, var13);
            int var17 = clamp(var5, var12, var13);
            int var18 = clamp(var6, var14, var15);
            int var19 = clamp(var7, var14, var15);
            int var20 = Math.max(0, var17 - var16);
            int var21 = Math.max(0, var19 - var18);
            int var22 = var20 * var21;
            if (var22 > var8) {
               var9 = var11;
               var8 = var22;
            }
         }

         return var9;
      }
   }

   public static int clamp(int var0, int var1, int var2) {
      if (var0 < var1) {
         return var1;
      } else {
         return var0 > var2 ? var2 : var0;
      }
   }

   public void shutdown() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GLFWMonitorCallback var1 = GLFW.glfwSetMonitorCallback((GLFWMonitorCallbackI)null);
      if (var1 != null) {
         var1.free();
      }

   }
}
