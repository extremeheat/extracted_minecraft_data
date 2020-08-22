package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Optional;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.main.SilentInitException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public final class Window implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final GLFWErrorCallback defaultErrorCallback = GLFWErrorCallback.create(this::defaultErrorCallback);
   private final WindowEventHandler eventHandler;
   private final ScreenManager screenManager;
   private final long window;
   private int windowedX;
   private int windowedY;
   private int windowedWidth;
   private int windowedHeight;
   private Optional preferredFullscreenVideoMode;
   private boolean fullscreen;
   private boolean actuallyFullscreen;
   private int x;
   private int y;
   private int width;
   private int height;
   private int framebufferWidth;
   private int framebufferHeight;
   private int guiScaledWidth;
   private int guiScaledHeight;
   private double guiScale;
   private String errorSection = "";
   private boolean dirty;
   private int framerateLimit;
   private boolean vsync;

   public Window(WindowEventHandler var1, ScreenManager var2, DisplayData var3, @Nullable String var4, String var5) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      this.screenManager = var2;
      this.setBootErrorCallback();
      this.setErrorSection("Pre startup");
      this.eventHandler = var1;
      Optional var6 = VideoMode.read(var4);
      if (var6.isPresent()) {
         this.preferredFullscreenVideoMode = var6;
      } else if (var3.fullscreenWidth.isPresent() && var3.fullscreenHeight.isPresent()) {
         this.preferredFullscreenVideoMode = Optional.of(new VideoMode(var3.fullscreenWidth.getAsInt(), var3.fullscreenHeight.getAsInt(), 8, 8, 8, 60));
      } else {
         this.preferredFullscreenVideoMode = Optional.empty();
      }

      this.actuallyFullscreen = this.fullscreen = var3.isFullscreen;
      Monitor var7 = var2.getMonitor(GLFW.glfwGetPrimaryMonitor());
      this.windowedWidth = this.width = var3.width > 0 ? var3.width : 1;
      this.windowedHeight = this.height = var3.height > 0 ? var3.height : 1;
      GLFW.glfwDefaultWindowHints();
      GLFW.glfwWindowHint(139265, 196609);
      GLFW.glfwWindowHint(139275, 221185);
      GLFW.glfwWindowHint(139266, 2);
      GLFW.glfwWindowHint(139267, 0);
      GLFW.glfwWindowHint(139272, 0);
      this.window = GLFW.glfwCreateWindow(this.width, this.height, var5, this.fullscreen && var7 != null ? var7.getMonitor() : 0L, 0L);
      if (var7 != null) {
         VideoMode var8 = var7.getPreferredVidMode(this.fullscreen ? this.preferredFullscreenVideoMode : Optional.empty());
         this.windowedX = this.x = var7.getX() + var8.getWidth() / 2 - this.width / 2;
         this.windowedY = this.y = var7.getY() + var8.getHeight() / 2 - this.height / 2;
      } else {
         int[] var10 = new int[1];
         int[] var9 = new int[1];
         GLFW.glfwGetWindowPos(this.window, var10, var9);
         this.windowedX = this.x = var10[0];
         this.windowedY = this.y = var9[0];
      }

      GLFW.glfwMakeContextCurrent(this.window);
      GL.createCapabilities();
      this.setMode();
      this.refreshFramebufferSize();
      GLFW.glfwSetFramebufferSizeCallback(this.window, this::onFramebufferResize);
      GLFW.glfwSetWindowPosCallback(this.window, this::onMove);
      GLFW.glfwSetWindowSizeCallback(this.window, this::onResize);
      GLFW.glfwSetWindowFocusCallback(this.window, this::onFocus);
   }

   public int getRefreshRate() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GLX._getRefreshRate(this);
   }

   public boolean shouldClose() {
      return GLX._shouldClose(this);
   }

   public static void checkGlfwError(BiConsumer var0) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      MemoryStack var1 = MemoryStack.stackPush();
      Throwable var2 = null;

      try {
         PointerBuffer var3 = var1.mallocPointer(1);
         int var4 = GLFW.glfwGetError(var3);
         if (var4 != 0) {
            long var5 = var3.get();
            String var7 = var5 == 0L ? "" : MemoryUtil.memUTF8(var5);
            var0.accept(var4, var7);
         }
      } catch (Throwable var15) {
         var2 = var15;
         throw var15;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var14) {
                  var2.addSuppressed(var14);
               }
            } else {
               var1.close();
            }
         }

      }

   }

   public void setIcon(InputStream var1, InputStream var2) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);

      try {
         MemoryStack var3 = MemoryStack.stackPush();
         Throwable var4 = null;

         try {
            if (var1 == null) {
               throw new FileNotFoundException("icons/icon_16x16.png");
            }

            if (var2 == null) {
               throw new FileNotFoundException("icons/icon_32x32.png");
            }

            IntBuffer var5 = var3.mallocInt(1);
            IntBuffer var6 = var3.mallocInt(1);
            IntBuffer var7 = var3.mallocInt(1);
            Buffer var8 = GLFWImage.mallocStack(2, var3);
            ByteBuffer var9 = this.readIconPixels(var1, var5, var6, var7);
            if (var9 == null) {
               throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
            }

            var8.position(0);
            var8.width(var5.get(0));
            var8.height(var6.get(0));
            var8.pixels(var9);
            ByteBuffer var10 = this.readIconPixels(var2, var5, var6, var7);
            if (var10 == null) {
               throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
            }

            var8.position(1);
            var8.width(var5.get(0));
            var8.height(var6.get(0));
            var8.pixels(var10);
            var8.position(0);
            GLFW.glfwSetWindowIcon(this.window, var8);
            STBImage.stbi_image_free(var9);
            STBImage.stbi_image_free(var10);
         } catch (Throwable var19) {
            var4 = var19;
            throw var19;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var18) {
                     var4.addSuppressed(var18);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (IOException var21) {
         LOGGER.error("Couldn't set icon", var21);
      }

   }

   @Nullable
   private ByteBuffer readIconPixels(InputStream var1, IntBuffer var2, IntBuffer var3, IntBuffer var4) throws IOException {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      ByteBuffer var5 = null;

      ByteBuffer var6;
      try {
         var5 = TextureUtil.readResource(var1);
         var5.rewind();
         var6 = STBImage.stbi_load_from_memory(var5, var2, var3, var4, 0);
      } finally {
         if (var5 != null) {
            MemoryUtil.memFree(var5);
         }

      }

      return var6;
   }

   public void setErrorSection(String var1) {
      this.errorSection = var1;
   }

   private void setBootErrorCallback() {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      GLFW.glfwSetErrorCallback(Window::bootCrash);
   }

   private static void bootCrash(int var0, long var1) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      String var3 = "GLFW error " + var0 + ": " + MemoryUtil.memUTF8(var1);
      TinyFileDialogs.tinyfd_messageBox("Minecraft", var3 + ".\n\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).", "ok", "error", false);
      throw new Window.WindowInitFailed(var3);
   }

   public void defaultErrorCallback(int var1, long var2) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      String var4 = MemoryUtil.memUTF8(var2);
      LOGGER.error("########## GL ERROR ##########");
      LOGGER.error("@ {}", this.errorSection);
      LOGGER.error("{}: {}", var1, var4);
   }

   public void setDefaultErrorCallback() {
      GLFWErrorCallback var1 = GLFW.glfwSetErrorCallback(this.defaultErrorCallback);
      if (var1 != null) {
         var1.free();
      }

   }

   public void updateVsync(boolean var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      this.vsync = var1;
      GLFW.glfwSwapInterval(var1 ? 1 : 0);
   }

   public void close() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      Callbacks.glfwFreeCallbacks(this.window);
      this.defaultErrorCallback.close();
      GLFW.glfwDestroyWindow(this.window);
      GLFW.glfwTerminate();
   }

   private void onMove(long var1, int var3, int var4) {
      this.x = var3;
      this.y = var4;
   }

   private void onFramebufferResize(long var1, int var3, int var4) {
      if (var1 == this.window) {
         int var5 = this.getWidth();
         int var6 = this.getHeight();
         if (var3 != 0 && var4 != 0) {
            this.framebufferWidth = var3;
            this.framebufferHeight = var4;
            if (this.getWidth() != var5 || this.getHeight() != var6) {
               this.eventHandler.resizeDisplay();
            }

         }
      }
   }

   private void refreshFramebufferSize() {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      int[] var1 = new int[1];
      int[] var2 = new int[1];
      GLFW.glfwGetFramebufferSize(this.window, var1, var2);
      this.framebufferWidth = var1[0];
      this.framebufferHeight = var2[0];
   }

   private void onResize(long var1, int var3, int var4) {
      this.width = var3;
      this.height = var4;
   }

   private void onFocus(long var1, boolean var3) {
      if (var1 == this.window) {
         this.eventHandler.setWindowActive(var3);
      }

   }

   public void setFramerateLimit(int var1) {
      this.framerateLimit = var1;
   }

   public int getFramerateLimit() {
      return this.framerateLimit;
   }

   public void updateDisplay() {
      RenderSystem.flipFrame(this.window);
      if (this.fullscreen != this.actuallyFullscreen) {
         this.actuallyFullscreen = this.fullscreen;
         this.updateFullscreen(this.vsync);
      }

   }

   public Optional getPreferredFullscreenVideoMode() {
      return this.preferredFullscreenVideoMode;
   }

   public void setPreferredFullscreenVideoMode(Optional var1) {
      boolean var2 = !var1.equals(this.preferredFullscreenVideoMode);
      this.preferredFullscreenVideoMode = var1;
      if (var2) {
         this.dirty = true;
      }

   }

   public void changeFullscreenVideoMode() {
      if (this.fullscreen && this.dirty) {
         this.dirty = false;
         this.setMode();
         this.eventHandler.resizeDisplay();
      }

   }

   private void setMode() {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      boolean var1 = GLFW.glfwGetWindowMonitor(this.window) != 0L;
      if (this.fullscreen) {
         Monitor var2 = this.screenManager.findBestMonitor(this);
         if (var2 == null) {
            LOGGER.warn("Failed to find suitable monitor for fullscreen mode");
            this.fullscreen = false;
         } else {
            VideoMode var3 = var2.getPreferredVidMode(this.preferredFullscreenVideoMode);
            if (!var1) {
               this.windowedX = this.x;
               this.windowedY = this.y;
               this.windowedWidth = this.width;
               this.windowedHeight = this.height;
            }

            this.x = 0;
            this.y = 0;
            this.width = var3.getWidth();
            this.height = var3.getHeight();
            GLFW.glfwSetWindowMonitor(this.window, var2.getMonitor(), this.x, this.y, this.width, this.height, var3.getRefreshRate());
         }
      } else {
         this.x = this.windowedX;
         this.y = this.windowedY;
         this.width = this.windowedWidth;
         this.height = this.windowedHeight;
         GLFW.glfwSetWindowMonitor(this.window, 0L, this.x, this.y, this.width, this.height, -1);
      }

   }

   public void toggleFullScreen() {
      this.fullscreen = !this.fullscreen;
   }

   private void updateFullscreen(boolean var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);

      try {
         this.setMode();
         this.eventHandler.resizeDisplay();
         this.updateVsync(var1);
         this.updateDisplay();
      } catch (Exception var3) {
         LOGGER.error("Couldn't toggle fullscreen", var3);
      }

   }

   public int calculateScale(int var1, boolean var2) {
      int var3;
      for(var3 = 1; var3 != var1 && var3 < this.framebufferWidth && var3 < this.framebufferHeight && this.framebufferWidth / (var3 + 1) >= 320 && this.framebufferHeight / (var3 + 1) >= 240; ++var3) {
      }

      if (var2 && var3 % 2 != 0) {
         ++var3;
      }

      return var3;
   }

   public void setGuiScale(double var1) {
      this.guiScale = var1;
      int var3 = (int)((double)this.framebufferWidth / var1);
      this.guiScaledWidth = (double)this.framebufferWidth / var1 > (double)var3 ? var3 + 1 : var3;
      int var4 = (int)((double)this.framebufferHeight / var1);
      this.guiScaledHeight = (double)this.framebufferHeight / var1 > (double)var4 ? var4 + 1 : var4;
   }

   public long getWindow() {
      return this.window;
   }

   public boolean isFullscreen() {
      return this.fullscreen;
   }

   public int getWidth() {
      return this.framebufferWidth;
   }

   public int getHeight() {
      return this.framebufferHeight;
   }

   public int getScreenWidth() {
      return this.width;
   }

   public int getScreenHeight() {
      return this.height;
   }

   public int getGuiScaledWidth() {
      return this.guiScaledWidth;
   }

   public int getGuiScaledHeight() {
      return this.guiScaledHeight;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public double getGuiScale() {
      return this.guiScale;
   }

   @Nullable
   public Monitor findBestMonitor() {
      return this.screenManager.findBestMonitor(this);
   }

   public void updateRawMouseInput(boolean var1) {
      InputConstants.updateRawMouseInput(this.window, var1);
   }

   public static class WindowInitFailed extends SilentInitException {
      private WindowInitFailed(String var1) {
         super(var1);
      }

      // $FF: synthetic method
      WindowInitFailed(String var1, Object var2) {
         this(var1);
      }
   }
}
