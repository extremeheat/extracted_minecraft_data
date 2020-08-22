package com.mojang.blaze3d.platform;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import oshi.SystemInfo;
import oshi.hardware.Processor;

public class GLX {
   private static final Logger LOGGER = LogManager.getLogger();
   private static String capsString = "";
   private static String cpuInfo;
   private static final Map LOOKUP_MAP = (Map)make(Maps.newHashMap(), (var0) -> {
      var0.put(0, "No error");
      var0.put(1280, "Enum parameter is invalid for this function");
      var0.put(1281, "Parameter is invalid for this function");
      var0.put(1282, "Current state is invalid for this function");
      var0.put(1283, "Stack overflow");
      var0.put(1284, "Stack underflow");
      var0.put(1285, "Out of memory");
      var0.put(1286, "Operation on incomplete framebuffer");
      var0.put(1286, "Operation on incomplete framebuffer");
   });

   public static String getOpenGLVersionString() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GLFW.glfwGetCurrentContext() == 0L ? "NO CONTEXT" : GlStateManager._getString(7937) + " GL version " + GlStateManager._getString(7938) + ", " + GlStateManager._getString(7936);
   }

   public static int _getRefreshRate(Window var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      long var1 = GLFW.glfwGetWindowMonitor(var0.getWindow());
      if (var1 == 0L) {
         var1 = GLFW.glfwGetPrimaryMonitor();
      }

      GLFWVidMode var3 = var1 == 0L ? null : GLFW.glfwGetVideoMode(var1);
      return var3 == null ? 0 : var3.refreshRate();
   }

   public static String _getLWJGLVersion() {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      return Version.getVersion();
   }

   public static LongSupplier _initGlfw() {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      Window.checkGlfwError((var0x, var1x) -> {
         throw new IllegalStateException(String.format("GLFW error before init: [0x%X]%s", var0x, var1x));
      });
      ArrayList var0 = Lists.newArrayList();
      GLFWErrorCallback var1 = GLFW.glfwSetErrorCallback((var1x, var2x) -> {
         var0.add(String.format("GLFW error during init: [0x%X]%s", var1x, var2x));
      });
      if (!GLFW.glfwInit()) {
         throw new IllegalStateException("Failed to initialize GLFW, errors: " + Joiner.on(",").join(var0));
      } else {
         LongSupplier var2 = () -> {
            return (long)(GLFW.glfwGetTime() * 1.0E9D);
         };
         Iterator var3 = var0.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            LOGGER.error("GLFW error collected during initialization: {}", var4);
         }

         RenderSystem.setErrorCallback(var1);
         return var2;
      }
   }

   public static void _setGlfwErrorCallback(GLFWErrorCallbackI var0) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      GLFWErrorCallback var1 = GLFW.glfwSetErrorCallback(var0);
      if (var1 != null) {
         var1.free();
      }

   }

   public static boolean _shouldClose(Window var0) {
      return GLFW.glfwWindowShouldClose(var0.getWindow());
   }

   public static void _setupNvFogDistance() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (GL.getCapabilities().GL_NV_fog_distance) {
         GlStateManager._fogi(34138, 34139);
      }

   }

   public static void _init(int var0, boolean var1) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      GLCapabilities var2 = GL.getCapabilities();
      capsString = "Using framebuffer using " + GlStateManager._init_fbo(var2);

      try {
         Processor[] var3 = (new SystemInfo()).getHardware().getProcessors();
         cpuInfo = String.format("%dx %s", var3.length, var3[0]).replaceAll("\\s+", " ");
      } catch (Throwable var4) {
      }

      GlDebug.enableDebugCallback(var0, var1);
   }

   public static String _getCapsString() {
      return capsString;
   }

   public static String _getCpuInfo() {
      return cpuInfo == null ? "<unknown>" : cpuInfo;
   }

   public static void _renderCrosshair(int var0, boolean var1, boolean var2, boolean var3) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GlStateManager._disableTexture();
      GlStateManager._depthMask(false);
      Tesselator var4 = RenderSystem.renderThreadTesselator();
      BufferBuilder var5 = var4.getBuilder();
      GL11.glLineWidth(4.0F);
      var5.begin(1, DefaultVertexFormat.POSITION_COLOR);
      if (var1) {
         var5.vertex(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         var5.vertex((double)var0, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
      }

      if (var2) {
         var5.vertex(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         var5.vertex(0.0D, (double)var0, 0.0D).color(0, 0, 0, 255).endVertex();
      }

      if (var3) {
         var5.vertex(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         var5.vertex(0.0D, 0.0D, (double)var0).color(0, 0, 0, 255).endVertex();
      }

      var4.end();
      GL11.glLineWidth(2.0F);
      var5.begin(1, DefaultVertexFormat.POSITION_COLOR);
      if (var1) {
         var5.vertex(0.0D, 0.0D, 0.0D).color(255, 0, 0, 255).endVertex();
         var5.vertex((double)var0, 0.0D, 0.0D).color(255, 0, 0, 255).endVertex();
      }

      if (var2) {
         var5.vertex(0.0D, 0.0D, 0.0D).color(0, 255, 0, 255).endVertex();
         var5.vertex(0.0D, (double)var0, 0.0D).color(0, 255, 0, 255).endVertex();
      }

      if (var3) {
         var5.vertex(0.0D, 0.0D, 0.0D).color(127, 127, 255, 255).endVertex();
         var5.vertex(0.0D, 0.0D, (double)var0).color(127, 127, 255, 255).endVertex();
      }

      var4.end();
      GL11.glLineWidth(1.0F);
      GlStateManager._depthMask(true);
      GlStateManager._enableTexture();
   }

   public static String getErrorString(int var0) {
      return (String)LOOKUP_MAP.get(var0);
   }

   public static Object make(Supplier var0) {
      return var0.get();
   }

   public static Object make(Object var0, Consumer var1) {
      var1.accept(var0);
      return var0;
   }
}
