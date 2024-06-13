package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.SilentInitException;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

public final class Window implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final GLFWErrorCallback defaultErrorCallback = GLFWErrorCallback.create(this::defaultErrorCallback);
   private final WindowEventHandler eventHandler;
   private final ScreenManager screenManager;
   private final long window;
   private int windowedX;
   private int windowedY;
   private int windowedWidth;
   private int windowedHeight;
   private Optional<VideoMode> preferredFullscreenVideoMode;
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
      super();
      RenderSystem.assertInInitPhase();
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
      GLFW.glfwWindowHint(139266, 3);
      GLFW.glfwWindowHint(139267, 2);
      GLFW.glfwWindowHint(139272, 204801);
      GLFW.glfwWindowHint(139270, 1);
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
      GLFW.glfwSetCursorEnterCallback(this.window, this::onEnter);
   }

   public int getRefreshRate() {
      RenderSystem.assertOnRenderThread();
      return GLX._getRefreshRate(this);
   }

   public boolean shouldClose() {
      return GLX._shouldClose(this);
   }

   public static void checkGlfwError(BiConsumer<Integer, String> var0) {
      RenderSystem.assertInInitPhase();
      MemoryStack var1 = MemoryStack.stackPush();

      try {
         PointerBuffer var2 = var1.mallocPointer(1);
         int var3 = GLFW.glfwGetError(var2);
         if (var3 != 0) {
            long var4 = var2.get();
            String var6 = var4 == 0L ? "" : MemoryUtil.memUTF8(var4);
            var0.accept(var3, var6);
         }
      } catch (Throwable var8) {
         if (var1 != null) {
            try {
               var1.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (var1 != null) {
         var1.close();
      }
   }

   public void setIcon(PackResources var1, IconSet var2) throws IOException {
      RenderSystem.assertInInitPhase();
      int var3 = GLFW.glfwGetPlatform();
      switch (var3) {
         case 393217:
         case 393220:
            List var4 = var2.getStandardIcons(var1);
            ArrayList var5 = new ArrayList(var4.size());

            try {
               MemoryStack var6 = MemoryStack.stackPush();

               try {
                  Buffer var7 = GLFWImage.malloc(var4.size(), var6);

                  for (int var8 = 0; var8 < var4.size(); var8++) {
                     try (NativeImage var9 = NativeImage.read((InputStream)((IoSupplier)var4.get(var8)).get())) {
                        ByteBuffer var10 = MemoryUtil.memAlloc(var9.getWidth() * var9.getHeight() * 4);
                        var5.add(var10);
                        var10.asIntBuffer().put(var9.getPixelsRGBA());
                        var7.position(var8);
                        var7.width(var9.getWidth());
                        var7.height(var9.getHeight());
                        var7.pixels(var10);
                     }
                  }

                  GLFW.glfwSetWindowIcon(this.window, (Buffer)var7.position(0));
               } catch (Throwable var21) {
                  if (var6 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var18) {
                        var21.addSuppressed(var18);
                     }
                  }

                  throw var21;
               }

               if (var6 != null) {
                  var6.close();
               }
               break;
            } finally {
               var5.forEach(MemoryUtil::memFree);
            }
         case 393218:
            MacosUtil.loadIcon(var2.getMacIcon(var1));
         case 393219:
         case 393221:
            break;
         default:
            LOGGER.warn("Not setting icon for unrecognized platform: {}", var3);
      }
   }

   public void setErrorSection(String var1) {
      this.errorSection = var1;
   }

   private void setBootErrorCallback() {
      RenderSystem.assertInInitPhase();
      GLFW.glfwSetErrorCallback(Window::bootCrash);
   }

   private static void bootCrash(int var0, long var1) {
      RenderSystem.assertInInitPhase();
      String var3 = "GLFW error " + var0 + ": " + MemoryUtil.memUTF8(var1);
      TinyFileDialogs.tinyfd_messageBox(
         "Minecraft", var3 + ".\n\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).", "ok", "error", false
      );
      throw new Window.WindowInitFailed(var3);
   }

   public void defaultErrorCallback(int var1, long var2) {
      RenderSystem.assertOnRenderThread();
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
      RenderSystem.assertOnRenderThreadOrInit();
      this.vsync = var1;
      GLFW.glfwSwapInterval(var1 ? 1 : 0);
   }

   @Override
   public void close() {
      RenderSystem.assertOnRenderThread();
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
      RenderSystem.assertInInitPhase();
      int[] var1 = new int[1];
      int[] var2 = new int[1];
      GLFW.glfwGetFramebufferSize(this.window, var1, var2);
      this.framebufferWidth = var1[0] > 0 ? var1[0] : 1;
      this.framebufferHeight = var2[0] > 0 ? var2[0] : 1;
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

   private void onEnter(long var1, boolean var3) {
      if (var3) {
         this.eventHandler.cursorEntered();
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

   public Optional<VideoMode> getPreferredFullscreenVideoMode() {
      return this.preferredFullscreenVideoMode;
   }

   public void setPreferredFullscreenVideoMode(Optional<VideoMode> var1) {
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
      RenderSystem.assertInInitPhase();
      boolean var1 = GLFW.glfwGetWindowMonitor(this.window) != 0L;
      if (this.fullscreen) {
         Monitor var2 = this.screenManager.findBestMonitor(this);
         if (var2 == null) {
            LOGGER.warn("Failed to find suitable monitor for fullscreen mode");
            this.fullscreen = false;
         } else {
            if (Minecraft.ON_OSX) {
               MacosUtil.exitNativeFullscreen(this.window);
            }

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
            if (Minecraft.ON_OSX) {
               MacosUtil.clearResizableBit(this.window);
            }
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

   public void setWindowed(int var1, int var2) {
      this.windowedWidth = var1;
      this.windowedHeight = var2;
      this.fullscreen = false;
      this.setMode();
   }

   private void updateFullscreen(boolean var1) {
      RenderSystem.assertOnRenderThread();

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
      int var3 = 1;

      while (
         var3 != var1
            && var3 < this.framebufferWidth
            && var3 < this.framebufferHeight
            && this.framebufferWidth / (var3 + 1) >= 320
            && this.framebufferHeight / (var3 + 1) >= 240
      ) {
         var3++;
      }

      if (var2 && var3 % 2 != 0) {
         var3++;
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

   public void setTitle(String var1) {
      GLFW.glfwSetWindowTitle(this.window, var1);
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

   public void setWidth(int var1) {
      this.framebufferWidth = var1;
   }

   public void setHeight(int var1) {
      this.framebufferHeight = var1;
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
      WindowInitFailed(String var1) {
         super(var1);
      }
   }
}
