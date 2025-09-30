package com.mojang.blaze3d.platform;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
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
   @Nullable
   private static String cpuInfo;

   public GLX() {
      super();
   }

   public static int _getRefreshRate(Window var0) {
      RenderSystem.assertOnRenderThread();
      long var1 = GLFW.glfwGetWindowMonitor(var0.handle());
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
         LongSupplier var2 = () -> (long)(GLFW.glfwGetTime() * 1.0E9);

         for(String var4 : var0) {
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
      return GLFW.glfwWindowShouldClose(var0.handle());
   }

   public static String _getCpuInfo() {
      if (cpuInfo == null) {
         cpuInfo = "<unknown>";

         try {
            CentralProcessor var0 = (new SystemInfo()).getHardware().getProcessor();
            cpuInfo = String.format(Locale.ROOT, "%dx %s", var0.getLogicalProcessorCount(), var0.getProcessorIdentifier().getName()).replaceAll("\\s+", " ");
         } catch (Throwable var1) {
         }
      }

      return cpuInfo;
   }

   public static <T> T make(Supplier<T> var0) {
      return (T)var0.get();
   }

   public static <T> T make(T var0, Consumer<T> var1) {
      var1.accept(var0);
      return var0;
   }
}
