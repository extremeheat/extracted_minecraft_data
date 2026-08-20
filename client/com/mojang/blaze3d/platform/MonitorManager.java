package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.nio.IntBuffer;
import org.jspecify.annotations.Nullable;
import org.lwjgl.sdl.SDLVideo;
import org.slf4j.Logger;

public class MonitorManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Int2ObjectMap<Monitor> monitors = new Int2ObjectOpenHashMap();

   public MonitorManager() {
      super();
      IntBuffer displays = SDLVideo.SDL_GetDisplays();
      if (displays != null) {
         for(int i = 0; i < displays.limit(); ++i) {
            this.addDisplay(displays.get(i));
         }
      }

   }

   public void onDisplayConnected(final int id) {
      RenderSystem.assertOnRenderThread();
      Monitor monitor = this.addDisplay(id);
      if (monitor != null) {
         LOGGER.debug("Monitor {} connected. Current monitors: {}", monitor, this.monitors);
      }

   }

   public void onDisplayDisconnected(final int id) {
      RenderSystem.assertOnRenderThread();
      Monitor monitor = (Monitor)this.monitors.remove(id);
      LOGGER.debug("Monitor {} disconnected. Current monitors: {}", monitor, this.monitors);
   }

   private @Nullable Monitor addDisplay(final int id) {
      Monitor monitor = Monitor.tryCreate(id);
      if (monitor != null) {
         this.monitors.put(id, monitor);
      }

      return monitor;
   }

   public @Nullable Monitor getMonitor(final int id) {
      return (Monitor)this.monitors.get(id);
   }

   public @Nullable Monitor findBestMonitor(final Window window) {
      return this.getMonitor(SDLVideo.SDL_GetDisplayForWindow(window.handle()));
   }
}
