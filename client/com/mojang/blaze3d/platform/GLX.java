package com.mojang.blaze3d.platform;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.minecraft.client.renderer.CoreShaders;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

@DontObfuscate
public class GLX {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static String cpuInfo;

   public GLX() {
      super();
   }

   public static String getOpenGLVersionString() {
      RenderSystem.assertOnRenderThread();
      if (GLFW.glfwGetCurrentContext() == 0L) {
         return "NO CONTEXT";
      } else {
         String var10000 = GlStateManager._getString(7937);
         return var10000 + " GL version " + GlStateManager._getString(7938) + ", " + GlStateManager._getString(7936);
      }
   }

   public static int _getRefreshRate(Window var0) {
      RenderSystem.assertOnRenderThread();
      long var1 = GLFW.glfwGetWindowMonitor(var0.getWindow());
      if (var1 == 0L) {
         var1 = GLFW.glfwGetPrimaryMonitor();
      }

      GLFWVidMode var3 = var1 == 0L ? null : GLFW.glfwGetVideoMode(var1);
      return var3 == null ? 0 : var3.refreshRate();
   }

   public static String _getLWJGLVersion() {
      return Version.getVersion();
   }

   public static LongSupplier _initGlfw() {
      Window.checkGlfwError((var0x, var1x) -> {
         throw new IllegalStateException(String.format(Locale.ROOT, "GLFW error before init: [0x%X]%s", var0x, var1x));
      });
      ArrayList var0 = Lists.newArrayList();
      GLFWErrorCallback var1 = GLFW.glfwSetErrorCallback((var1x, var2x) -> {
         String var4 = var2x == 0L ? "" : MemoryUtil.memUTF8(var2x);
         var0.add(String.format(Locale.ROOT, "GLFW error during init: [0x%X]%s", var1x, var4));
      });
      if (!GLFW.glfwInit()) {
         throw new IllegalStateException("Failed to initialize GLFW, errors: " + Joiner.on(",").join(var0));
      } else {
         LongSupplier var2 = () -> {
            return (long)(GLFW.glfwGetTime() * 1.0E9);
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
      GLFWErrorCallback var1 = GLFW.glfwSetErrorCallback(var0);
      if (var1 != null) {
         var1.free();
      }

   }

   public static boolean _shouldClose(Window var0) {
      return GLFW.glfwWindowShouldClose(var0.getWindow());
   }

   public static void _init(int var0, boolean var1) {
      try {
         CentralProcessor var2 = (new SystemInfo()).getHardware().getProcessor();
         cpuInfo = String.format(Locale.ROOT, "%dx %s", var2.getLogicalProcessorCount(), var2.getProcessorIdentifier().getName()).replaceAll("\\s+", " ");
      } catch (Throwable var3) {
      }

      GlDebug.enableDebugCallback(var0, var1);
   }

   public static String _getCpuInfo() {
      return cpuInfo == null ? "<unknown>" : cpuInfo;
   }

   public static void _renderCrosshair(int var0, boolean var1, boolean var2, boolean var3) {
      if (var1 || var2 || var3) {
         RenderSystem.assertOnRenderThread();
         GlStateManager._depthMask(false);
         GlStateManager._disableCull();
         RenderSystem.setShader(CoreShaders.RENDERTYPE_LINES);
         Tesselator var4 = RenderSystem.renderThreadTesselator();
         BufferBuilder var5 = var4.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
         RenderSystem.lineWidth(4.0F);
         if (var1) {
            var5.addVertex(0.0F, 0.0F, 0.0F).setColor(-16777216).setNormal(1.0F, 0.0F, 0.0F);
            var5.addVertex((float)var0, 0.0F, 0.0F).setColor(-16777216).setNormal(1.0F, 0.0F, 0.0F);
         }

         if (var2) {
            var5.addVertex(0.0F, 0.0F, 0.0F).setColor(-16777216).setNormal(0.0F, 1.0F, 0.0F);
            var5.addVertex(0.0F, (float)var0, 0.0F).setColor(-16777216).setNormal(0.0F, 1.0F, 0.0F);
         }

         if (var3) {
            var5.addVertex(0.0F, 0.0F, 0.0F).setColor(-16777216).setNormal(0.0F, 0.0F, 1.0F);
            var5.addVertex(0.0F, 0.0F, (float)var0).setColor(-16777216).setNormal(0.0F, 0.0F, 1.0F);
         }

         BufferUploader.drawWithShader(var5.buildOrThrow());
         RenderSystem.lineWidth(2.0F);
         var5 = var4.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
         if (var1) {
            var5.addVertex(0.0F, 0.0F, 0.0F).setColor(-65536).setNormal(1.0F, 0.0F, 0.0F);
            var5.addVertex((float)var0, 0.0F, 0.0F).setColor(-65536).setNormal(1.0F, 0.0F, 0.0F);
         }

         if (var2) {
            var5.addVertex(0.0F, 0.0F, 0.0F).setColor(-16711936).setNormal(0.0F, 1.0F, 0.0F);
            var5.addVertex(0.0F, (float)var0, 0.0F).setColor(-16711936).setNormal(0.0F, 1.0F, 0.0F);
         }

         if (var3) {
            var5.addVertex(0.0F, 0.0F, 0.0F).setColor(-8421377).setNormal(0.0F, 0.0F, 1.0F);
            var5.addVertex(0.0F, 0.0F, (float)var0).setColor(-8421377).setNormal(0.0F, 0.0F, 1.0F);
         }

         BufferUploader.drawWithShader(var5.buildOrThrow());
         RenderSystem.lineWidth(1.0F);
         GlStateManager._enableCull();
         GlStateManager._depthMask(true);
      }
   }

   public static <T> T make(Supplier<T> var0) {
      return var0.get();
   }

   public static <T> T make(T var0, Consumer<T> var1) {
      var1.accept(var0);
      return var0;
   }
}
