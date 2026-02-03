package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.TracyFrameCapture;
import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.shaders.GpuDebugOptions;
import com.mojang.blaze3d.shaders.ShaderSource;
import com.mojang.blaze3d.systems.BackendCreationException;
import com.mojang.blaze3d.systems.GpuBackend;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.WindowAndDevice;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.main.SilentInitException;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

public final class Window implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int BASE_WIDTH = 320;
   public static final int BASE_HEIGHT = 240;
   private final GLFWErrorCallback defaultErrorCallback = GLFWErrorCallback.create(this::defaultErrorCallback);
   private final WindowEventHandler eventHandler;
   private final ScreenManager screenManager;
   private final long handle;
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
   private int guiScale;
   private String errorSection = "";
   private boolean dirty;
   private boolean vsync;
   private boolean iconified;
   private boolean minimized;
   private boolean allowCursorChanges;
   private CursorType currentCursor;

   public Window(final WindowEventHandler eventHandler, final DisplayData displayData, final @Nullable String fullscreenVideoModeString, final String title, final GpuBackend[] backends, final ShaderSource defaultShaderSource, final GpuDebugOptions debugOptions) {
      super();
      this.currentCursor = CursorType.DEFAULT;
      this.screenManager = new ScreenManager(Monitor::new);
      this.setBootErrorCallback();
      this.setErrorSection("Pre startup");
      this.eventHandler = eventHandler;
      Optional<VideoMode> optionsMode = VideoMode.read(fullscreenVideoModeString);
      if (optionsMode.isPresent()) {
         this.preferredFullscreenVideoMode = optionsMode;
      } else if (displayData.fullscreenWidth().isPresent() && displayData.fullscreenHeight().isPresent()) {
         this.preferredFullscreenVideoMode = Optional.of(new VideoMode(displayData.fullscreenWidth().getAsInt(), displayData.fullscreenHeight().getAsInt(), 8, 8, 8, 60));
      } else {
         this.preferredFullscreenVideoMode = Optional.empty();
      }

      this.actuallyFullscreen = this.fullscreen = displayData.isFullscreen();
      Monitor initialMonitor = this.screenManager.getMonitor(GLFW.glfwGetPrimaryMonitor());
      this.windowedWidth = this.width = Math.max(displayData.width(), 1);
      this.windowedHeight = this.height = Math.max(displayData.height(), 1);
      WindowAndDevice windowAndDevice = this.initializeBackend(backends, this.width, this.height, title, this.fullscreen && initialMonitor != null ? initialMonitor.getMonitor() : 0L, defaultShaderSource, debugOptions);
      this.handle = windowAndDevice.window();
      RenderSystem.initRenderer(windowAndDevice.device());
      if (initialMonitor != null) {
         VideoMode mode = initialMonitor.getPreferredVidMode(this.fullscreen ? this.preferredFullscreenVideoMode : Optional.empty());
         this.windowedX = this.x = initialMonitor.getX() + mode.getWidth() / 2 - this.width / 2;
         this.windowedY = this.y = initialMonitor.getY() + mode.getHeight() / 2 - this.height / 2;
      } else {
         int[] actualX = new int[1];
         int[] actualY = new int[1];
         GLFW.glfwGetWindowPos(this.handle, actualX, actualY);
         this.windowedX = this.x = actualX[0];
         this.windowedY = this.y = actualY[0];
      }

      this.setMode();
      this.refreshFramebufferSize();
      GLFW.glfwSetFramebufferSizeCallback(this.handle, this::onFramebufferResize);
      GLFW.glfwSetWindowPosCallback(this.handle, this::onMove);
      GLFW.glfwSetWindowSizeCallback(this.handle, this::onResize);
      GLFW.glfwSetWindowFocusCallback(this.handle, this::onFocus);
      GLFW.glfwSetCursorEnterCallback(this.handle, this::onEnter);
      GLFW.glfwSetWindowIconifyCallback(this.handle, this::onIconify);
   }

   private WindowAndDevice initializeBackend(final GpuBackend[] backends, final int width, final int height, final String title, final long initialMonitor, final ShaderSource defaultShaderSource, final GpuDebugOptions debugOptions) {
      StringBuilder error = new StringBuilder("No supported graphics backend was found.");

      for(GpuBackend backend : backends) {
         try {
            return backend.createDeviceWithWindow(width, height, title, initialMonitor, defaultShaderSource, debugOptions);
         } catch (BackendCreationException exception) {
            error.append("\n\n- Tried ").append(backend.getName()).append(": \n  ").append(exception.getMessage());
         }
      }

      TinyFileDialogs.tinyfd_messageBox("Minecraft", error.toString(), "ok", "error", false);
      throw new WindowInitFailed(error.toString());
   }

   public static String getPlatform() {
      int platform = GLFW.glfwGetPlatform();
      String var10000;
      switch (platform) {
         case 0 -> var10000 = "<error>";
         case 393217 -> var10000 = "win32";
         case 393218 -> var10000 = "cocoa";
         case 393219 -> var10000 = "wayland";
         case 393220 -> var10000 = "x11";
         case 393221 -> var10000 = "null";
         default -> var10000 = String.format(Locale.ROOT, "unknown (%08X)", platform);
      }

      return var10000;
   }

   public int getRefreshRate() {
      RenderSystem.assertOnRenderThread();
      return GLX._getRefreshRate(this);
   }

   public boolean shouldClose() {
      return GLX._shouldClose(this);
   }

   public static void checkGlfwError(final BiConsumer<Integer, String> errorConsumer) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         PointerBuffer errorDescription = stack.mallocPointer(1);
         int errorCode = GLFW.glfwGetError(errorDescription);
         if (errorCode != 0) {
            long errorDescriptionAddress = errorDescription.get();
            String errorMessage = errorDescriptionAddress == 0L ? "" : MemoryUtil.memUTF8(errorDescriptionAddress);
            errorConsumer.accept(errorCode, errorMessage);
         }
      } catch (Throwable var8) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (stack != null) {
         stack.close();
      }

   }

   public void setIcon(final PackResources resources, final IconSet iconSet) throws IOException {
      int platform = GLFW.glfwGetPlatform();
      switch (platform) {
         case 393217:
         case 393220:
            List<IoSupplier<InputStream>> iconStreams = iconSet.getStandardIcons(resources);
            List<ByteBuffer> allocatedBuffers = new ArrayList(iconStreams.size());

            try {
               MemoryStack stack = MemoryStack.stackPush();

               try {
                  GLFWImage.Buffer icons = GLFWImage.malloc(iconStreams.size(), stack);

                  for(int i = 0; i < iconStreams.size(); ++i) {
                     NativeImage image = NativeImage.read((InputStream)((IoSupplier)iconStreams.get(i)).get());

                     try {
                        ByteBuffer pixels = MemoryUtil.memAlloc(image.getWidth() * image.getHeight() * 4);
                        allocatedBuffers.add(pixels);
                        pixels.asIntBuffer().put(image.getPixelsABGR());
                        icons.position(i);
                        icons.width(image.getWidth());
                        icons.height(image.getHeight());
                        icons.pixels(pixels);
                     } catch (Throwable var20) {
                        if (image != null) {
                           try {
                              image.close();
                           } catch (Throwable var19) {
                              var20.addSuppressed(var19);
                           }
                        }

                        throw var20;
                     }

                     if (image != null) {
                        image.close();
                     }
                  }

                  GLFW.glfwSetWindowIcon(this.handle, (GLFWImage.Buffer)icons.position(0));
               } catch (Throwable var21) {
                  if (stack != null) {
                     try {
                        stack.close();
                     } catch (Throwable var18) {
                        var21.addSuppressed(var18);
                     }
                  }

                  throw var21;
               }

               if (stack != null) {
                  stack.close();
               }
               break;
            } finally {
               allocatedBuffers.forEach(MemoryUtil::memFree);
            }
         case 393218:
            MacosUtil.loadIcon(iconSet.getMacIcon(resources));
         case 393219:
         case 393221:
            break;
         default:
            LOGGER.warn("Not setting icon for unrecognized platform: {}", platform);
      }

   }

   public void setErrorSection(final String string) {
      this.errorSection = string;
   }

   private void setBootErrorCallback() {
      GLFW.glfwSetErrorCallback(Window::bootCrash);
   }

   private static void bootCrash(final int error, final long description) {
      String message = "GLFW error " + error + ": " + MemoryUtil.memUTF8(description);
      TinyFileDialogs.tinyfd_messageBox("Minecraft", message + ".\n\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).", "ok", "error", false);
      throw new WindowInitFailed(message);
   }

   public void defaultErrorCallback(final int errorCode, final long description) {
      RenderSystem.assertOnRenderThread();
      String errorString = MemoryUtil.memUTF8(description);
      LOGGER.error("########## GL ERROR ##########");
      LOGGER.error("@ {}", this.errorSection);
      LOGGER.error("{}: {}", errorCode, errorString);
   }

   public void setDefaultErrorCallback() {
      GLFWErrorCallback previousCallback = GLFW.glfwSetErrorCallback(this.defaultErrorCallback);
      if (previousCallback != null) {
         previousCallback.free();
      }

   }

   public void updateVsync(final boolean enableVsync) {
      RenderSystem.assertOnRenderThread();
      this.vsync = enableVsync;
      RenderSystem.getDevice().setVsync(enableVsync);
   }

   public void close() {
      RenderSystem.assertOnRenderThread();
      this.screenManager.shutdown();
      Callbacks.glfwFreeCallbacks(this.handle);
      this.defaultErrorCallback.close();
      GLFW.glfwDestroyWindow(this.handle);
      GLFW.glfwTerminate();
   }

   private void onMove(final long handle, final int x, final int y) {
      this.x = x;
      this.y = y;
   }

   private void onFramebufferResize(final long handle, final int newWidth, final int newHeight) {
      if (handle == this.handle) {
         int oldWidth = this.getWidth();
         int oldHeight = this.getHeight();
         if (newWidth != 0 && newHeight != 0) {
            this.minimized = false;
            this.framebufferWidth = newWidth;
            this.framebufferHeight = newHeight;
            if (this.getWidth() != oldWidth || this.getHeight() != oldHeight) {
               try {
                  this.eventHandler.resizeDisplay();
               } catch (Exception e) {
                  CrashReport report = CrashReport.forThrowable(e, "Window resize");
                  CrashReportCategory windowSizeDetails = report.addCategory("Window Dimensions");
                  windowSizeDetails.setDetail("Old", oldWidth + "x" + oldHeight);
                  windowSizeDetails.setDetail("New", newWidth + "x" + newHeight);
                  throw new ReportedException(report);
               }
            }

         } else {
            this.minimized = true;
         }
      }
   }

   private void refreshFramebufferSize() {
      int[] outWidth = new int[1];
      int[] outHeight = new int[1];
      GLFW.glfwGetFramebufferSize(this.handle, outWidth, outHeight);
      this.framebufferWidth = outWidth[0] > 0 ? outWidth[0] : 1;
      this.framebufferHeight = outHeight[0] > 0 ? outHeight[0] : 1;
   }

   private void onResize(final long handle, final int newWidth, final int newHeight) {
      this.width = newWidth;
      this.height = newHeight;
   }

   private void onFocus(final long handle, final boolean focused) {
      if (handle == this.handle) {
         this.eventHandler.setWindowActive(focused);
      }

   }

   private void onEnter(final long handle, final boolean entered) {
      if (entered) {
         this.eventHandler.cursorEntered();
      }

   }

   private void onIconify(final long handle, final boolean iconified) {
      this.iconified = iconified;
   }

   public void updateDisplay(final @Nullable TracyFrameCapture tracyFrameCapture) {
      RenderSystem.flipFrame(tracyFrameCapture);
      if (this.fullscreen != this.actuallyFullscreen) {
         this.actuallyFullscreen = this.fullscreen;
         this.updateFullscreen(this.vsync, tracyFrameCapture);
      }

   }

   public Optional<VideoMode> getPreferredFullscreenVideoMode() {
      return this.preferredFullscreenVideoMode;
   }

   public void setPreferredFullscreenVideoMode(final Optional<VideoMode> preferredFullscreenVideoMode) {
      boolean changed = !preferredFullscreenVideoMode.equals(this.preferredFullscreenVideoMode);
      this.preferredFullscreenVideoMode = preferredFullscreenVideoMode;
      if (changed) {
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
      boolean wasFullscreen = GLFW.glfwGetWindowMonitor(this.handle) != 0L;
      if (this.fullscreen) {
         Monitor monitor = this.screenManager.findBestMonitor(this);
         if (monitor == null) {
            LOGGER.warn("Failed to find suitable monitor for fullscreen mode");
            this.fullscreen = false;
         } else {
            if (MacosUtil.IS_MACOS) {
               MacosUtil.exitNativeFullscreen(this);
            }

            VideoMode mode = monitor.getPreferredVidMode(this.preferredFullscreenVideoMode);
            if (!wasFullscreen) {
               this.windowedX = this.x;
               this.windowedY = this.y;
               this.windowedWidth = this.width;
               this.windowedHeight = this.height;
            }

            this.x = 0;
            this.y = 0;
            this.width = mode.getWidth();
            this.height = mode.getHeight();
            GLFW.glfwSetWindowMonitor(this.handle, monitor.getMonitor(), this.x, this.y, this.width, this.height, mode.getRefreshRate());
            if (MacosUtil.IS_MACOS) {
               MacosUtil.clearResizableBit(this);
            }
         }
      } else {
         this.x = this.windowedX;
         this.y = this.windowedY;
         this.width = this.windowedWidth;
         this.height = this.windowedHeight;
         GLFW.glfwSetWindowMonitor(this.handle, 0L, this.x, this.y, this.width, this.height, -1);
      }

   }

   public void toggleFullScreen() {
      this.fullscreen = !this.fullscreen;
   }

   public void setWindowed(final int width, final int height) {
      this.windowedWidth = width;
      this.windowedHeight = height;
      this.fullscreen = false;
      this.setMode();
   }

   private void updateFullscreen(final boolean enableVsync, final @Nullable TracyFrameCapture tracyFrameCapture) {
      RenderSystem.assertOnRenderThread();

      try {
         this.setMode();
         this.eventHandler.resizeDisplay();
         this.updateVsync(enableVsync);
         this.updateDisplay(tracyFrameCapture);
      } catch (Exception e) {
         LOGGER.error("Couldn't toggle fullscreen", e);
      }

   }

   public int calculateScale(final int maxScale, final boolean enforceUnicode) {
      int scale;
      for(scale = 1; scale != maxScale && scale < this.framebufferWidth && scale < this.framebufferHeight && this.framebufferWidth / (scale + 1) >= 320 && this.framebufferHeight / (scale + 1) >= 240; ++scale) {
      }

      if (enforceUnicode && scale % 2 != 0) {
         ++scale;
      }

      return scale;
   }

   public void setGuiScale(final int guiScale) {
      this.guiScale = guiScale;
      double doubleGuiScale = (double)guiScale;
      int width = (int)((double)this.framebufferWidth / doubleGuiScale);
      this.guiScaledWidth = (double)this.framebufferWidth / doubleGuiScale > (double)width ? width + 1 : width;
      int height = (int)((double)this.framebufferHeight / doubleGuiScale);
      this.guiScaledHeight = (double)this.framebufferHeight / doubleGuiScale > (double)height ? height + 1 : height;
   }

   public void setTitle(final String title) {
      GLFW.glfwSetWindowTitle(this.handle, title);
   }

   public long handle() {
      return this.handle;
   }

   public boolean isFullscreen() {
      return this.fullscreen;
   }

   public boolean isIconified() {
      return this.iconified;
   }

   public int getWidth() {
      return this.framebufferWidth;
   }

   public int getHeight() {
      return this.framebufferHeight;
   }

   public void setWidth(final int width) {
      this.framebufferWidth = width;
   }

   public void setHeight(final int height) {
      this.framebufferHeight = height;
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

   public int getGuiScale() {
      return this.guiScale;
   }

   public @Nullable Monitor findBestMonitor() {
      return this.screenManager.findBestMonitor(this);
   }

   public void updateRawMouseInput(final boolean value) {
      InputConstants.updateRawMouseInput(this, value);
   }

   public void setWindowCloseCallback(final Runnable task) {
      GLFWWindowCloseCallback prev = GLFW.glfwSetWindowCloseCallback(this.handle, (id) -> task.run());
      if (prev != null) {
         prev.free();
      }

   }

   public boolean isMinimized() {
      return this.minimized;
   }

   public void setAllowCursorChanges(final boolean value) {
      this.allowCursorChanges = value;
   }

   public void selectCursor(final CursorType cursor) {
      CursorType effectiveCursor = this.allowCursorChanges ? cursor : CursorType.DEFAULT;
      if (this.currentCursor != effectiveCursor) {
         this.currentCursor = effectiveCursor;
         effectiveCursor.select(this);
      }

   }

   public float getAppropriateLineWidth() {
      return Math.max(2.5F, (float)this.getWidth() / 1920.0F * 2.5F);
   }

   public static class WindowInitFailed extends SilentInitException {
      private WindowInitFailed(final String message) {
         super(message);
      }
   }
}
