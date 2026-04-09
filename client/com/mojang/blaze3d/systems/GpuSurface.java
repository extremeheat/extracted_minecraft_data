package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.Collection;
import java.util.Optional;

public class GpuSurface implements AutoCloseable {
   private final GpuSurfaceBackend backend;
   private boolean hasImageAcquired = false;
   private boolean hasBlittedTexture = false;
   private Optional<Configuration> currentConfiguration = Optional.empty();

   public GpuSurface(final GpuSurfaceBackend backend) {
      super();
      this.backend = backend;
   }

   public void close() {
      if (this.hasImageAcquired) {
         throw new IllegalStateException("Cannot close a surface while it is acquired");
      } else {
         this.backend.close();
      }
   }

   public void configure(final Configuration config) throws SurfaceException {
      if (this.hasImageAcquired) {
         throw new IllegalStateException("Cannot configure a surface while it is acquired");
      } else if (!this.supportedPresentModes().contains(config.presentMode())) {
         String var10002 = String.valueOf(config.presentMode());
         throw new SurfaceException("Surface does not support present mode " + var10002 + " (supported: " + String.valueOf(this.supportedPresentModes()) + ")");
      } else {
         this.backend.configure(config);
         this.currentConfiguration = Optional.of(config);
      }
   }

   public Optional<Configuration> currentConfiguration() {
      return this.currentConfiguration;
   }

   public Collection<PresentMode> supportedPresentModes() {
      return this.backend.supportedPresentModes();
   }

   public boolean isSuboptimal() {
      return this.backend.isSuboptimal();
   }

   public boolean isAcquired() {
      return this.hasImageAcquired;
   }

   public void acquireNextTexture() throws SurfaceException {
      if (this.hasImageAcquired) {
         throw new IllegalStateException("Cannot acquire a surface while it is already acquired");
      } else if (this.currentConfiguration.isEmpty()) {
         throw new IllegalStateException("Cannot acquire an unconfigured surface");
      } else {
         this.backend.acquireNextTexture();
         this.hasImageAcquired = true;
         this.hasBlittedTexture = false;
      }
   }

   public void blitFromTexture(final CommandEncoder commandEncoder, final GpuTextureView textureView) {
      if (commandEncoder.isInRenderPass()) {
         throw new IllegalStateException("Close the existing render pass before presenting with a command encoder");
      } else if (!textureView.texture().getFormat().hasColorAspect()) {
         throw new IllegalStateException("Cannot present a non-color texture!");
      } else if ((textureView.texture().usage() & 2) == 0) {
         throw new IllegalStateException("Color texture must have USAGE_COPY_SRC to presented to the screen");
      } else if (textureView.texture().getDepthOrLayers() > 1) {
         throw new UnsupportedOperationException("Textures with multiple depths or layers are not yet supported for presentation");
      } else if (!this.hasImageAcquired) {
         throw new IllegalStateException("Cannot present to an unacquired surface");
      } else if (this.hasBlittedTexture) {
         throw new IllegalStateException("Already blitted to this frame!");
      } else {
         this.backend.blitFromTexture(commandEncoder.backend(), textureView);
         this.hasBlittedTexture = true;
      }
   }

   public void present() {
      if (!this.hasImageAcquired) {
         throw new IllegalStateException("Cannot present to a surface if it isn't acquired");
      } else if (!this.hasBlittedTexture) {
         throw new IllegalStateException("Must blit to surface before presenting!");
      } else {
         this.backend.present();
         this.hasImageAcquired = false;
      }
   }

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

      private static final PresentMode[] PRESENT_MODES_VSYNC = new PresentMode[]{FIFO_RELAXED, FIFO};
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
