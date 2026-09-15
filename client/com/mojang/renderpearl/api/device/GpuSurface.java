package com.mojang.renderpearl.api.device;

import com.mojang.renderpearl.api.commands.CommandEncoder;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import java.util.Collection;
import java.util.Optional;

public interface GpuSurface extends UncheckedAutoCloseable {
   void configure(Configuration config) throws SurfaceException;

   Optional<Configuration> currentConfiguration();

   Collection<PresentMode> supportedPresentModes();

   boolean isSuboptimal();

   boolean isAcquired();

   void acquireNextTexture() throws SurfaceException;

   void blitFromTexture(CommandEncoder commandEncoder, GpuTextureView textureView);

   void present();

   public static record Configuration(int width, int height, PresentMode presentMode) {
      public Configuration {
         super();
      }
   }

   public static enum PresentMode {
      IMMEDIATE,
      MAILBOX,
      FIFO,
      FIFO_RELAXED;

      private static final PresentMode[] PRESENT_MODES_VSYNC = new PresentMode[]{FIFO};
      private static final PresentMode[] PRESENT_MODES_NO_VSYNC = new PresentMode[]{IMMEDIATE, MAILBOX, FIFO};

      private PresentMode() {
      }

      public static PresentMode getSupportedVsyncMode(final Collection<PresentMode> supportedModes, final boolean vsync) {
         PresentMode[] preferred = vsync ? PRESENT_MODES_VSYNC : PRESENT_MODES_NO_VSYNC;

         for(PresentMode mode : preferred) {
            if (supportedModes.contains(mode)) {
               return mode;
            }
         }

         throw new IllegalStateException("No supported presentation mode was found");
      }

      // $FF: synthetic method
      private static PresentMode[] $values() {
         return new PresentMode[]{IMMEDIATE, MAILBOX, FIFO, FIFO_RELAXED};
      }
   }
}
