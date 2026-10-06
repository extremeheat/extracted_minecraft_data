package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.renderpearl.api.device.GpuBackend;
import com.mojang.renderpearl.api.device.GpuDevice;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.InputQuirks;
import net.minecraft.client.main.SilentInitException;
import net.minecraft.server.packs.PackMetadataResources;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.lwjgl.sdl.SDLError;
import org.lwjgl.sdl.SDLHints;
import org.lwjgl.sdl.SDLPlatform;
import org.lwjgl.sdl.SDLSurface;
import org.lwjgl.sdl.SDLVideo;
import org.lwjgl.sdl.SDL_DisplayMode;
import org.lwjgl.sdl.SDL_Event;
import org.lwjgl.sdl.SDL_Rect;
import org.lwjgl.sdl.SDL_Surface;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;

public final class Window implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int MIN_WINDOW_WIDTH = 320;
   public static final int MIN_WINDOW_HEIGHT = 240;
   public static final int BASE_WIDTH = 320;
   public static final int BASE_HEIGHT = 240;
   private static final int BORDERLESS_FULLSCREEN_PADDING = 1;
   private final WindowEventHandler eventHandler;
   private final MonitorManager monitorManager;
   private final long handle;
   private final boolean wayland;
   private final boolean needsBorderlessFullscreenPadding;
   private int windowedX;
   private int windowedY;
   private int windowedWidth;
   private int windowedHeight;
   private Optional<VideoMode> preferredFullscreenVideoMode;
   private boolean fullscreenRequested;
   private boolean fullscreen;
   private int x;
   private int y;
   private int width;
   private int height;
   private int framebufferWidth;
   private int framebufferHeight;
   private int guiScaledWidth;
   private int guiScaledHeight;
   private int guiScale;
   private String errorSection = "Startup";
   private boolean dirty;
   private boolean iconified;
   private boolean focused = true;
   private boolean shouldClose;
   private @Nullable Runnable closeCallback;
   private boolean allowCursorChanges;
   private boolean quitShortcuts;
   private CursorType currentCursor;
   private boolean exclusiveFullscreen;
   private boolean borderlessFullscreen;

   public Window(final WindowEventHandler eventHandler, final DisplayData displayData, final @Nullable String fullscreenVideoModeString, final boolean exclusiveFullscreen, final String title, final MonitorManager monitorManager, final GpuBackend backend, final GpuDevice gpuDevice, final int maximumSize) {
      super();
      this.currentCursor = CursorType.DEFAULT;
      this.monitorManager = monitorManager;
      this.exclusiveFullscreen = exclusiveFullscreen;
      this.eventHandler = eventHandler;
      Optional<VideoMode> optionsMode = VideoMode.read(fullscreenVideoModeString);
      if (optionsMode.isPresent()) {
         this.preferredFullscreenVideoMode = optionsMode;
      } else if (displayData.fullscreenWidth().isPresent() && displayData.fullscreenHeight().isPresent()) {
         this.preferredFullscreenVideoMode = Optional.of(new VideoMode(displayData.fullscreenWidth().getAsInt(), displayData.fullscreenHeight().getAsInt(), 8, 8, 8, 60));
      } else {
         this.preferredFullscreenVideoMode = Optional.empty();
      }

      this.fullscreenRequested = displayData.isFullscreen();
      Monitor initialMonitor = monitorManager.getMonitor(SDLVideo.SDL_GetPrimaryDisplay());
      this.width = Math.max(displayData.width(), 320);
      this.height = Math.max(displayData.height(), 240);
      this.setWindowedSize(this.width, this.height);
      this.handle = this.createWindow(backend, this.width, this.height, title);
      this.setWindowMaxSize(maximumSize, maximumSize);
      String videoDriver = SDLVideo.SDL_GetCurrentVideoDriver();
      this.wayland = "wayland".equals(videoDriver);
      LOGGER.info("Created window using SDL video driver: {}", videoDriver);
      this.needsBorderlessFullscreenPadding = gpuDevice.getDeviceInfo().hintsAndWorkarounds().needsImeFullscreenWorkaround();
      MacosUtil.disableCloseWindowMenuItem();
      if (initialMonitor != null) {
         this.windowedX = this.x = initialMonitor.x() + (initialMonitor.w() - this.width) / 2;
         this.windowedY = this.y = initialMonitor.y() + (initialMonitor.h() - this.height) / 2;
      } else {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            IntBuffer actualX = stack.mallocInt(1);
            IntBuffer actualY = stack.mallocInt(1);
            if (!SDLVideo.SDL_GetWindowPosition(this.handle, actualX, actualY)) {
               throw new IllegalStateException("Failed to query initial window position: " + SDLError.SDL_GetError());
            }

            this.windowedX = this.x = actualX.get(0);
            this.windowedY = this.y = actualY.get(0);
         } catch (Throwable var17) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var16) {
                  var17.addSuppressed(var16);
               }
            }

            throw var17;
         }

         if (stack != null) {
            stack.close();
         }
      }

      this.setMode();
      this.refreshFramebufferSize();
   }

   public static String getPlatform() {
      return (String)Objects.requireNonNullElse(SDLPlatform.SDL_GetPlatform(), "unknown platform");
   }

   private static @Nullable SDL_Surface createIconSurface(final NativeImage image) {
      int pitch = image.getWidth() * 4;
      return SDLSurface.SDL_CreateSurfaceFrom(image.getWidth(), image.getHeight(), 376840196, image.getPixelBytes(), pitch);
   }

   private long createWindow(final GpuBackend backend, final int width, final int height, final String title) {
      long flags = 8224L;
      long windowHandle = backend.createWindow(title, width, height, 8224L);
      if (windowHandle == 0L) {
         String var10002 = SDLError.SDL_GetError();
         throw new IllegalStateException("Failed to create window: " + (String)Objects.requireNonNullElse(var10002, "<no error>"));
      } else {
         SDLVideo.SDL_SetWindowMinimumSize(windowHandle, 320, 240);
         return windowHandle;
      }
   }

   public @Nullable VideoMode getActiveVideoMode() {
      RenderSystem.assertOnRenderThread();
      SDL_DisplayMode mode = this.getActiveDisplayMode();
      return mode == null ? null : new VideoMode(mode);
   }

   private @Nullable SDL_DisplayMode getActiveDisplayMode() {
      SDL_DisplayMode windowMode = SDLVideo.SDL_GetWindowFullscreenMode(this.handle);
      if (windowMode != null) {
         return windowMode;
      } else {
         int displayId = SDLVideo.SDL_GetDisplayForWindow(this.handle);
         return displayId == 0 ? null : SDLVideo.SDL_GetCurrentDisplayMode(displayId);
      }
   }

   public boolean shouldClose() {
      return this.shouldClose;
   }

   public void handleEvent(final SDL_Event event) {
      switch (event.type()) {
         case 256:
         case 528:
            this.onQuitRequested();
            break;
         case 257:
            this.requestClose();
            break;
         case 338:
            this.monitorManager.onDisplayConnected(event.display().displayID());
            break;
         case 339:
            this.monitorManager.onDisplayDisconnected(event.display().displayID());
            break;
         case 342:
            this.onDisplayModeChanged(event.display().displayID());
            break;
         case 517:
            this.onMove(event.window().data1(), event.window().data2());
            break;
         case 518:
            this.onResize(event.window().data1(), event.window().data2());
            break;
         case 519:
            this.onFramebufferResize(event.window().data1(), event.window().data2());
            break;
         case 521:
            this.onIconified(true);
            break;
         case 522:
         case 523:
            this.onIconified(false);
            break;
         case 524:
         case 525:
            this.eventHandler.cursorEntered();
            break;
         case 526:
            this.onFocus(true);
            break;
         case 527:
            this.onFocus(false);
            break;
         case 531:
            this.onWindowDisplayChanged();
            break;
         case 535:
         case 536:
            this.updateFullscreenState();
      }

   }

   private void onDisplayModeChanged(final int displayId) {
      this.monitorManager.onDisplayModeChanged(displayId);
      this.refreshFramebufferSize();
      this.eventHandler.framebufferSizeChanged();
   }

   private void onWindowDisplayChanged() {
      this.refreshMouseState();
      this.eventHandler.framebufferSizeChanged();
   }

   private void onQuitRequested() {
      if (this.quitShortcuts || !InputQuirks.isQuitShortcutDown()) {
         this.requestClose();
      }

   }

   private void updateFullscreenState() {
      boolean newFullscreen = this.isWindowFullscreen();
      if (this.fullscreen != newFullscreen) {
         this.fullscreen = newFullscreen;
         this.eventHandler.fullscreenStateChanged(newFullscreen);
      }

   }

   private void requestClose() {
      this.shouldClose = true;
      if (this.closeCallback != null) {
         this.closeCallback.run();
      }

   }

   public void setIcon(final PackMetadataResources resources, final IconSet iconSet) throws IOException {
      Util.OS platform = Util.getPlatform();
      switch (platform) {
         case WINDOWS:
         case LINUX:
         case SOLARIS:
         case OSX:
            this.setIcon(iconSet.getStandardIcons(resources));
            break;
         default:
            LOGGER.warn("Not setting icon for unrecognized platform: {}", platform);
      }

   }

   private void setIcon(final List<IoSupplier<InputStream>> iconStreams) throws IOException {
      if (!iconStreams.isEmpty()) {
         List<NativeImage> images = new ArrayList(iconStreams.size());

         try {
            SDL_Surface primarySurface = createIconSurface((IoSupplier)iconStreams.getFirst(), images);
            if (primarySurface != null) {
               for(IoSupplier<InputStream> iconStream : iconStreams.subList(1, iconStreams.size())) {
                  SDL_Surface surface = createIconSurface(iconStream, images);
                  if (surface != null) {
                     if (!SDLSurface.SDL_AddSurfaceAlternateImage(primarySurface, surface)) {
                        LOGGER.warn("Failed to add {}x{} icon as alternate window icon image: {}", new Object[]{surface.w(), surface.h(), SDLError.SDL_GetError()});
                     }

                     SDLSurface.SDL_DestroySurface(surface);
                  }
               }

               if (!SDLVideo.SDL_SetWindowIcon(this.handle, primarySurface)) {
                  LOGGER.warn("Failed to set window icon: {}", SDLError.SDL_GetError());
               }

               SDLSurface.SDL_DestroySurface(primarySurface);
               return;
            }
         } finally {
            images.forEach(NativeImage::close);
         }

      }
   }

   private static @Nullable SDL_Surface createIconSurface(final IoSupplier<InputStream> iconStream, final List<NativeImage> images) throws IOException {
      NativeImage image = NativeImage.read(iconStream.get());
      images.add(image);
      SDL_Surface surface = createIconSurface(image);
      if (surface == null) {
         LOGGER.warn("Failed to create SDL surface for {}x{} icon: {}", new Object[]{image.getWidth(), image.getHeight(), SDLError.SDL_GetError()});
      }

      return surface;
   }

   public String getErrorSection() {
      return this.errorSection;
   }

   public void setErrorSection(final String string) {
      this.errorSection = string;
   }

   public void close() {
      RenderSystem.assertOnRenderThread();
      SDLVideo.SDL_DestroyWindow(this.handle);
   }

   private void onMove(final int x, final int y) {
      this.x = x;
      this.y = y;
      if (!this.isWindowFullscreen()) {
         this.windowedX = x;
         this.windowedY = y;
      }

   }

   private void onResize(final int newWidth, final int newHeight) {
      this.width = newWidth;
      this.height = newHeight;
      if (!this.isWindowFullscreen()) {
         this.setWindowedSize(newWidth, newHeight);
      }

      this.refreshMouseState();
   }

   private void refreshMouseState() {
      this.updateWindowMouseGrab();
      Minecraft.getInstance().mouseHandler.refreshMouseState();
   }

   private void onFramebufferResize(final int newWidth, final int newHeight) {
      if (newWidth > 0 && newHeight > 0) {
         int oldWidth = this.getWidth();
         int oldHeight = this.getHeight();
         this.framebufferWidth = newWidth - this.framebufferWidthPadding();
         this.framebufferHeight = newHeight;

         try {
            this.eventHandler.framebufferSizeChanged();
         } catch (Exception e) {
            CrashReport report = CrashReport.forThrowable(e, "Window resize");
            CrashReportCategory windowSizeDetails = report.addCategory("Window Dimensions");
            windowSizeDetails.setDetail("Old", oldWidth + "x" + oldHeight);
            windowSizeDetails.setDetail("New", newWidth + "x" + newHeight);
            throw new ReportedException(report);
         }
      }
   }

   private void refreshFramebufferSize() {
      FramebufferSize size = this.queryFramebufferSize();
      this.framebufferWidth = size.width() - this.framebufferWidthPadding();
      this.framebufferHeight = size.height();
   }

   private int framebufferWidthPadding() {
      return this.needsBorderlessFullscreenPadding && this.borderlessFullscreen ? 1 : 0;
   }

   public FramebufferSize queryFramebufferSize() {
      MemoryStack stack = MemoryStack.stackPush();

      FramebufferSize var4;
      try {
         IntBuffer outWidth = stack.mallocInt(1);
         IntBuffer outHeight = stack.mallocInt(1);
         if (!SDLVideo.SDL_GetWindowSizeInPixels(this.handle, outWidth, outHeight)) {
            throw new IllegalStateException("Failed to query window size in pixels: " + SDLError.SDL_GetError());
         }

         var4 = new FramebufferSize(Math.max(outWidth.get(0), 1), Math.max(outHeight.get(0), 1));
      } catch (Throwable var6) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (stack != null) {
         stack.close();
      }

      return var4;
   }

   private void onFocus(final boolean focused) {
      this.focused = focused;
   }

   private void onIconified(final boolean iconified) {
      this.iconified = iconified;
      Minecraft.getInstance().invalidateSurfaceConfiguration();
   }

   public void updateFullscreenIfChanged() {
      RenderSystem.assertOnRenderThread();
      if (this.fullscreenRequested != this.fullscreen) {
         this.setMode();
         this.eventHandler.framebufferSizeChanged();
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
      RenderSystem.assertOnRenderThread();
      if (this.fullscreenRequested && this.dirty) {
         this.dirty = false;
         this.setMode();
         this.eventHandler.framebufferSizeChanged();
      }

   }

   private void setMode() {
      RenderSystem.assertOnRenderThread();
      if (this.fullscreenRequested && !this.fullscreen) {
         this.windowedX = this.x;
         this.windowedY = this.y;
         this.setWindowedSize(this.width, this.height);
      }

      boolean success = this.fullscreenRequested ? this.applyFullscreen() : this.applyWindowed();
      if (!success) {
         LOGGER.error("Couldn't {} fullscreen: {}", this.fullscreenRequested ? "enter" : "leave", SDLError.SDL_GetError());
         this.fullscreenRequested = this.fullscreen;
         this.eventHandler.fullscreenStateChanged(this.fullscreen);
      } else {
         this.syncWindow();
         this.updateFullscreenState();
         this.refreshFramebufferSize();
         if (!this.isExclusiveFullscreen()) {
            this.updateWindowMouseGrab();
         }

         if (this.exclusiveFullscreen && this.fullscreen && !this.isExclusiveFullscreen()) {
            LOGGER.info("Exclusive fullscreen request resolved to borderless desktop");
         }

      }
   }

   private void updateWindowMouseGrab() {
      boolean shouldGrabMouse = this.fullscreen && this.isExclusiveFullscreen();
      if (!SDLVideo.SDL_SetWindowMouseGrab(this.handle, shouldGrabMouse)) {
         LOGGER.warn("Failed to update window mouse grab state: {}", SDLError.SDL_GetError());
      }

   }

   private boolean isWindowFullscreen() {
      return this.borderlessFullscreen || (SDLVideo.SDL_GetWindowFlags(this.handle) & 1L) != 0L;
   }

   public boolean isExclusiveFullscreen() {
      return this.isWindowFullscreen() && SDLVideo.SDL_GetWindowFullscreenMode(this.handle) != null;
   }

   private boolean applyFullscreen() {
      if (this.useBorderlessFullscreenWindow()) {
         return this.applyBorderlessFullscreenWindow();
      } else {
         this.leaveBorderlessFullscreenWindow();
         return this.applyFullscreenMode() && SDLVideo.SDL_SetWindowFullscreen(this.handle, true);
      }
   }

   private boolean useBorderlessFullscreenWindow() {
      return Util.getPlatform() == Util.OS.WINDOWS && !this.exclusiveFullscreen;
   }

   private boolean applySdlBorderlessFullscreen() {
      this.leaveBorderlessFullscreenWindow();
      return this.applyBorderlessFullscreen() && SDLVideo.SDL_SetWindowFullscreen(this.handle, true);
   }

   private void leaveBorderlessFullscreenWindow() {
      if (this.borderlessFullscreen) {
         this.borderlessFullscreen = false;
         if (!SDLVideo.SDL_SetWindowBordered(this.handle, true)) {
            LOGGER.warn("Failed to restore window decorations: {}", SDLError.SDL_GetError());
         }

      }
   }

   private boolean applyFullscreenMode() {
      return !this.exclusiveFullscreen ? this.applyBorderlessFullscreen() : this.applyExclusiveFullscreen();
   }

   private boolean applyExclusiveFullscreen() {
      Monitor monitor = this.monitorManager.findBestMonitor(this);
      if (monitor == null) {
         LOGGER.warn("Failed to find suitable monitor for exclusive fullscreen, falling back to borderless fullscreen");
         return this.applyBorderlessFullscreen();
      } else {
         VideoMode videoMode = monitor.getPreferredVideoMode(this.preferredFullscreenVideoMode);
         LOGGER.info("Exclusive target {} on monitor {}", videoMode, monitor);
         MemoryStack stack = MemoryStack.stackPush();

         boolean var8;
         label47: {
            try {
               SDL_DisplayMode mode = SDL_DisplayMode.malloc(stack);
               if (SDLVideo.SDL_GetClosestFullscreenDisplayMode(monitor.id(), videoMode.getWidth(), videoMode.getHeight(), videoMode.getRefreshRate(), true, mode)) {
                  var8 = SDLVideo.SDL_SetWindowFullscreenMode(this.handle, mode);
                  break label47;
               }

               LOGGER.warn("No matching exclusive fullscreen mode found for {}, falling back to borderless fullscreen", videoMode);
               var8 = this.applyBorderlessFullscreen();
            } catch (Throwable var7) {
               if (stack != null) {
                  try {
                     stack.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (stack != null) {
               stack.close();
            }

            return var8;
         }

         if (stack != null) {
            stack.close();
         }

         return var8;
      }
   }

   private boolean applyWindowed() {
      this.leaveBorderlessFullscreenWindow();
      return !SDLVideo.SDL_SetWindowFullscreen(this.handle, false) ? false : this.setWindowSizeAndPosition(this.windowedX, this.windowedY, this.windowedWidth, this.windowedHeight);
   }

   private boolean applyBorderlessFullscreenWindow() {
      Monitor monitor = this.monitorManager.findBestMonitor(this);
      if (monitor == null) {
         LOGGER.warn("Failed to find suitable monitor for borderless fullscreen, falling back to SDL borderless fullscreen");
         return this.applySdlBorderlessFullscreen();
      } else {
         MemoryStack stack = MemoryStack.stackPush();

         boolean var8;
         label65: {
            label66: {
               try {
                  SDL_Rect bounds = SDL_Rect.malloc(stack);
                  if (!SDLVideo.SDL_GetDisplayBounds(monitor.id(), bounds)) {
                     LOGGER.warn("Failed to query bounds of monitor {}, falling back to SDL borderless fullscreen: {}", monitor, SDLError.SDL_GetError());
                     var8 = this.applySdlBorderlessFullscreen();
                     break label65;
                  }

                  if (this.applyBorderlessFullscreen() && SDLVideo.SDL_SetWindowFullscreen(this.handle, false)) {
                     this.restoreWindow();
                     this.borderlessFullscreen = true;
                     if (!SDLVideo.SDL_SetWindowBordered(this.handle, false)) {
                        LOGGER.warn("Failed to remove window decorations for borderless fullscreen: {}", SDLError.SDL_GetError());
                     }

                     var8 = this.setWindowSizeAndPosition(bounds.x(), bounds.y(), bounds.w() + this.framebufferWidthPadding(), bounds.h());
                     break label66;
                  }

                  var8 = false;
               } catch (Throwable var6) {
                  if (stack != null) {
                     try {
                        stack.close();
                     } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                     }
                  }

                  throw var6;
               }

               if (stack != null) {
                  stack.close();
               }

               return var8;
            }

            if (stack != null) {
               stack.close();
            }

            return var8;
         }

         if (stack != null) {
            stack.close();
         }

         return var8;
      }
   }

   private void restoreWindow() {
      if (!SDLVideo.SDL_RestoreWindow(this.handle)) {
         LOGGER.warn("Failed to restore window before entering fullscreen: {}", SDLError.SDL_GetError());
      } else {
         this.syncWindow();
      }
   }

   private void syncWindow() {
      if (!SDLVideo.SDL_SyncWindow(this.handle)) {
         LOGGER.warn("Failed to synchronize SDL window: {}", SDLError.SDL_GetError());
      }

   }

   private boolean setWindowSizeAndPosition(final int windowX, final int windowY, final int windowWidth, final int windowHeight) {
      this.x = windowX;
      this.y = windowY;
      this.width = windowWidth;
      this.height = windowHeight;
      if (!SDLVideo.SDL_SetWindowSize(this.handle, this.width, this.height)) {
         return false;
      } else {
         if (!SDLVideo.SDL_SetWindowPosition(this.handle, this.x, this.y)) {
            LOGGER.debug("Window manager declined window positioning: {}", SDLError.SDL_GetError());
         }

         return true;
      }
   }

   private boolean applyBorderlessFullscreen() {
      return SDLVideo.SDL_SetWindowFullscreenMode(this.handle, (SDL_DisplayMode)null);
   }

   public void setExclusiveFullscreen(final boolean exclusiveFullscreen) {
      if (this.exclusiveFullscreen != exclusiveFullscreen) {
         this.exclusiveFullscreen = exclusiveFullscreen;
         if (this.fullscreenRequested) {
            this.setMode();
            this.eventHandler.framebufferSizeChanged();
         }
      }

   }

   public void setWindowed(final int width, final int height) {
      this.setWindowedSize(width, height);
      this.fullscreenRequested = false;
      this.setMode();
   }

   private void setWindowedSize(final int width, final int height) {
      this.windowedWidth = Math.max(width, 320);
      this.windowedHeight = Math.max(height, 240);
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

   public void setTitle(final String title) {
      SDLVideo.SDL_SetWindowTitle(this.handle, title);
   }

   public void setWindowMaxSize(final int width, final int height) {
      SDLVideo.SDL_SetWindowMaximumSize(this.handle, width, height);
   }

   public long handle() {
      return this.handle;
   }

   public boolean isWayland() {
      return this.wayland;
   }

   public void setFullscreen(final boolean fullscreen) {
      this.fullscreenRequested = fullscreen;
   }

   public boolean isIconified() {
      return this.iconified;
   }

   public boolean isFocused() {
      return this.focused;
   }

   public int getWidth() {
      return this.framebufferWidth;
   }

   public void setWidth(final int width) {
      this.framebufferWidth = width;
   }

   public int getHeight() {
      return this.framebufferHeight;
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

   public void setGuiScale(final int guiScale) {
      this.guiScale = guiScale;
      this.guiScaledWidth = (int)Math.ceil((double)this.framebufferWidth / (double)guiScale);
      this.guiScaledHeight = (int)Math.ceil((double)this.framebufferHeight / (double)guiScale);
   }

   public float getPixelDensity() {
      float density = SDLVideo.SDL_GetWindowPixelDensity(this.handle);
      return density > 0.0F ? density : 1.0F;
   }

   public @Nullable Monitor findBestMonitor() {
      return this.monitorManager.findBestMonitor(this);
   }

   public void setWindowCloseCallback(final Runnable task) {
      this.closeCallback = task;
   }

   public void setAllowCursorChanges(final boolean value) {
      this.allowCursorChanges = value;
   }

   public void setQuitShortcuts(final boolean value) {
      this.quitShortcuts = value;
      SDLHints.SDL_SetHint("SDL_WINDOWS_CLOSE_ON_ALT_F4", value ? "1" : "0");
   }

   public void selectCursor(final CursorType cursor) {
      CursorType effectiveCursor = this.allowCursorChanges ? cursor : CursorType.DEFAULT;
      if (this.currentCursor != effectiveCursor) {
         this.currentCursor = effectiveCursor;
         effectiveCursor.select();
      }

   }

   public float getAppropriateLineWidth() {
      return Math.max(2.5F, (float)this.getWidth() / 1920.0F * 2.5F);
   }

   public static record FramebufferSize(int width, int height) {
      public FramebufferSize {
         super();
      }
   }

   public static class WindowInitFailed extends SilentInitException {
      public WindowInitFailed(final String message) {
         super(message);
      }
   }
}
