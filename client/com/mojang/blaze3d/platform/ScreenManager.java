package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallback;
import org.lwjgl.glfw.GLFWMonitorCallbackI;
import org.slf4j.Logger;

public class ScreenManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Long2ObjectMap<Monitor> monitors = new Long2ObjectOpenHashMap();
   private final MonitorCreator monitorCreator;

   public ScreenManager(MonitorCreator var1) {
      super();
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
      RenderSystem.assertOnRenderThread();
      if (var3 == 262145) {
         this.monitors.put(var1, this.monitorCreator.createMonitor(var1));
         LOGGER.debug("Monitor {} connected. Current monitors: {}", var1, this.monitors);
      } else if (var3 == 262146) {
         this.monitors.remove(var1);
         LOGGER.debug("Monitor {} disconnected. Current monitors: {}", var1, this.monitors);
      }

   }

   @Nullable
   public Monitor getMonitor(long var1) {
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
         long var10 = GLFW.glfwGetPrimaryMonitor();
         LOGGER.debug("Selecting monitor - primary: {}, current monitors: {}", var10, this.monitors);
         ObjectIterator var12 = this.monitors.values().iterator();

         while(var12.hasNext()) {
            Monitor var13 = (Monitor)var12.next();
            int var14 = var13.getX();
            int var15 = var14 + var13.getCurrentMode().getWidth();
            int var16 = var13.getY();
            int var17 = var16 + var13.getCurrentMode().getHeight();
            int var18 = clamp(var4, var14, var15);
            int var19 = clamp(var5, var14, var15);
            int var20 = clamp(var6, var16, var17);
            int var21 = clamp(var7, var16, var17);
            int var22 = Math.max(0, var19 - var18);
            int var23 = Math.max(0, var21 - var20);
            int var24 = var22 * var23;
            if (var24 > var8) {
               var9 = var13;
               var8 = var24;
            } else if (var24 == var8 && var10 == var13.getMonitor()) {
               LOGGER.debug("Primary monitor {} is preferred to monitor {}", var13, var9);
               var9 = var13;
            }
         }

         LOGGER.debug("Selected monitor: {}", var9);
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
      RenderSystem.assertOnRenderThread();
      GLFWMonitorCallback var1 = GLFW.glfwSetMonitorCallback((GLFWMonitorCallbackI)null);
      if (var1 != null) {
         var1.free();
      }

   }
}
