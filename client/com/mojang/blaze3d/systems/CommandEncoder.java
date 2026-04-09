package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.logging.LogUtils;
import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class CommandEncoder {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final GpuDeviceBackend device;
   private final CommandEncoderBackend backend;
   private boolean isInRenderPass;
   private final @Nullable TracyGpuProfiler profiler;

   public CommandEncoder(final @Nullable TracyGpuProfiler profiler, final GpuDeviceBackend device, final CommandEncoderBackend backend) {
      super();
      this.profiler = profiler;
      this.device = device;
      this.backend = backend;
   }

   protected CommandEncoderBackend backend() {
      return this.backend;
   }

   public void submit() {
      this.backend.submit();
      if (this.profiler != null) {
         this.profiler.endFrame();
      }

   }

   protected boolean isInRenderPass() {
      return this.isInRenderPass;
   }

   public RenderPass createRenderPass(final Supplier<String> label, final GpuTextureView colorTexture, final OptionalInt clearColor) {
      return this.createRenderPass(label, colorTexture, clearColor, (GpuTextureView)null, OptionalDouble.empty());
   }

   public RenderPass createRenderPass(final Supplier<String> label, final GpuTextureView colorTexture, final OptionalInt clearColor, final @Nullable GpuTextureView depthTexture, final OptionalDouble clearDepth) {
      if (this.isInRenderPass) {
         throw new IllegalStateException("Close the existing render pass before creating a new one!");
      } else {
         if (clearDepth.isPresent() && depthTexture == null) {
            LOGGER.warn("Depth clear value was provided but no depth texture is being used");
         }

         if (colorTexture.isClosed()) {
            throw new IllegalStateException("Color texture is closed");
         } else if ((colorTexture.texture().usage() & 8) == 0) {
            throw new IllegalStateException("Color texture must have USAGE_RENDER_ATTACHMENT");
         } else if (colorTexture.texture().getDepthOrLayers() > 1) {
            throw new UnsupportedOperationException("Textures with multiple depths or layers are not yet supported as an attachment");
         } else {
            if (depthTexture != null) {
               if (depthTexture.isClosed()) {
                  throw new IllegalStateException("Depth texture is closed");
               }

               if ((depthTexture.texture().usage() & 8) == 0) {
                  throw new IllegalStateException("Depth texture must have USAGE_RENDER_ATTACHMENT");
               }

               if (depthTexture.texture().getDepthOrLayers() > 1) {
                  throw new UnsupportedOperationException("Textures with multiple depths or layers are not yet supported as an attachment");
               }
            }

            this.isInRenderPass = true;
            if (this.profiler != null) {
               this.profiler.pushZone(this, (String)label.get());
            }

            return new RenderPass(this.backend.createRenderPass(label, colorTexture, clearColor, depthTexture, clearDepth), this.device, this::submitRenderPass);
         }
      }
   }

   protected void submitRenderPass() {
      if (!this.isInRenderPass) {
         throw new IllegalStateException("Can't submit a render pass if one isn't open");
      } else {
         this.isInRenderPass = false;
         this.backend.submitRenderPass();
         if (this.profiler != null) {
            this.profiler.popZone(this);
         }

      }
   }

   public void clearColorTexture(final GpuTexture colorTexture, final int clearColor) {
      if (this.isInRenderPass) {
         throw new IllegalStateException("Close the existing render pass before creating a new one!");
      } else {
         this.verifyColorTexture(colorTexture);
         this.backend.clearColorTexture(colorTexture, clearColor);
      }
   }

   public void clearColorAndDepthTextures(final GpuTexture colorTexture, final int clearColor, final GpuTexture depthTexture, final double clearDepth) {
      if (this.isInRenderPass) {
         throw new IllegalStateException("Close the existing render pass before creating a new one!");
      } else {
         this.verifyColorTexture(colorTexture);
         this.verifyDepthTexture(depthTexture);
         this.backend.clearColorAndDepthTextures(colorTexture, clearColor, depthTexture, clearDepth);
      }
   }

   public void clearColorAndDepthTextures(final GpuTexture colorTexture, final int clearColor, final GpuTexture depthTexture, final double clearDepth, final int regionX, final int regionY, final int regionWidth, final int regionHeight) {
      if (this.isInRenderPass) {
         throw new IllegalStateException("Close the existing render pass before creating a new one!");
      } else {
         this.verifyColorTexture(colorTexture);
         this.verifyDepthTexture(depthTexture);
         this.verifyRegion(colorTexture, regionX, regionY, regionWidth, regionHeight);
         this.backend.clearColorAndDepthTextures(colorTexture, clearColor, depthTexture, clearDepth, regionX, regionY, regionWidth, regionHeight);
      }
   }

   public void clearDepthTexture(final GpuTexture depthTexture, final double clearDepth) {
      if (this.isInRenderPass) {
         throw new IllegalStateException("Close the existing render pass before creating a new one!");
      } else {
         this.verifyDepthTexture(depthTexture);
         this.backend.clearDepthTexture(depthTexture, clearDepth);
      }
   }

   public void writeToBuffer(final GpuBufferSlice destination, final ByteBuffer data) {
      if (this.isInRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else {
         this.backend.writeToBuffer(destination, data);
      }
   }

   public GpuBuffer.MappedView mapBuffer(final GpuBuffer buffer, final boolean read, final boolean write) {
      return this.mapBuffer(buffer.slice(), read, write);
   }

   public GpuBuffer.MappedView mapBuffer(final GpuBufferSlice slice, final boolean read, final boolean write) {
      if (this.isInRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else {
         GpuBuffer buffer = slice.buffer();
         if (buffer.isClosed()) {
            throw new IllegalStateException("Buffer already closed");
         } else if (!read && !write) {
            throw new IllegalArgumentException("At least read or write must be true");
         } else if (read && (buffer.usage() & 1) == 0) {
            throw new IllegalStateException("Buffer is not readable");
         } else if (write && (buffer.usage() & 2) == 0) {
            throw new IllegalStateException("Buffer is not writable");
         } else if (slice.offset() + slice.length() > buffer.size()) {
            long var10002 = slice.length();
            throw new IllegalArgumentException("Cannot map more data than this buffer can hold (attempting to map " + var10002 + " bytes at offset " + slice.offset() + " from " + buffer.size() + " size buffer)");
         } else {
            return this.backend.mapBuffer(slice, read, write);
         }
      }
   }

   public void copyToBuffer(final GpuBufferSlice source, final GpuBufferSlice target) {
      if (this.isInRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else {
         GpuBuffer sourceBuffer = source.buffer();
         if (sourceBuffer.isClosed()) {
            throw new IllegalStateException("Source buffer already closed");
         } else if ((sourceBuffer.usage() & 16) == 0) {
            throw new IllegalStateException("Source buffer needs USAGE_COPY_SRC to be a source for a copy");
         } else {
            GpuBuffer targetBuffer = target.buffer();
            if (targetBuffer.isClosed()) {
               throw new IllegalStateException("Target buffer already closed");
            } else if ((targetBuffer.usage() & 8) == 0) {
               throw new IllegalStateException("Target buffer needs USAGE_COPY_DST to be a destination for a copy");
            } else if (source.length() != target.length()) {
               long var6 = source.length();
               throw new IllegalArgumentException("Cannot copy from slice of size " + var6 + " to slice of size " + target.length() + ", they must be equal");
            } else if (source.offset() + source.length() > sourceBuffer.size()) {
               long var5 = source.length();
               throw new IllegalArgumentException("Cannot copy more data than the source buffer holds (attempting to copy " + var5 + " bytes at offset " + source.offset() + " from " + sourceBuffer.size() + " size buffer)");
            } else if (target.offset() + target.length() > targetBuffer.size()) {
               long var10002 = target.length();
               throw new IllegalArgumentException("Cannot copy more data than the target buffer can hold (attempting to copy " + var10002 + " bytes at offset " + target.offset() + " to " + targetBuffer.size() + " size buffer)");
            } else {
               this.backend.copyToBuffer(source, target);
            }
         }
      }
   }

   public void writeToTexture(final GpuTexture destination, final NativeImage source) {
      int width = destination.getWidth(0);
      int height = destination.getHeight(0);
      if (source.getWidth() == width && source.getHeight() == height) {
         if (destination.isClosed()) {
            throw new IllegalStateException("Destination texture is closed");
         } else if ((destination.usage() & 1) == 0) {
            throw new IllegalStateException("Color texture must have USAGE_COPY_DST to be a destination for a write");
         } else {
            this.writeToTexture(destination, source, 0, 0, 0, 0, width, height, 0, 0);
         }
      } else {
         throw new IllegalArgumentException("Cannot replace texture of size " + width + "x" + height + " with image of size " + source.getWidth() + "x" + source.getHeight());
      }
   }

   public void writeToTexture(final GpuTexture destination, final NativeImage source, final int mipLevel, final int depthOrLayer, final int destX, final int destY, final int width, final int height, final int sourceX, final int sourceY) {
      if (this.isInRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else if (mipLevel >= 0 && mipLevel < destination.getMipLevels()) {
         if (sourceX + width <= source.getWidth() && sourceY + height <= source.getHeight()) {
            if (destX + width <= destination.getWidth(mipLevel) && destY + height <= destination.getHeight(mipLevel)) {
               if (destination.isClosed()) {
                  throw new IllegalStateException("Destination texture is closed");
               } else if ((destination.usage() & 1) == 0) {
                  throw new IllegalStateException("Color texture must have USAGE_COPY_DST to be a destination for a write");
               } else if (depthOrLayer >= destination.getDepthOrLayers()) {
                  throw new UnsupportedOperationException("Depth or layer is out of range, must be >= 0 and < " + destination.getDepthOrLayers());
               } else {
                  this.backend.writeToTexture(destination, source, mipLevel, depthOrLayer, destX, destY, width, height, sourceX, sourceY);
               }
            } else {
               throw new IllegalArgumentException("Dest texture (" + width + "x" + height + ") is not large enough to write a rectangle of " + width + "x" + height + " at " + destX + "x" + destY + " (at mip level " + mipLevel + ")");
            }
         } else {
            int var10002 = source.getWidth();
            throw new IllegalArgumentException("Copy source (" + var10002 + "x" + source.getHeight() + ") is not large enough to read a rectangle of " + width + "x" + height + " from " + sourceX + "x" + sourceY);
         }
      } else {
         throw new IllegalArgumentException("Invalid mipLevel " + mipLevel + ", must be >= 0 and < " + destination.getMipLevels());
      }
   }

   public void writeToTexture(final GpuTexture destination, final ByteBuffer source, final NativeImage.Format format, final int mipLevel, final int depthOrLayer, final int destX, final int destY, final int width, final int height) {
      if (this.isInRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else if (mipLevel >= 0 && mipLevel < destination.getMipLevels()) {
         if (width * height * format.components() > source.remaining()) {
            throw new IllegalArgumentException("Copy would overrun the source buffer (remaining length of " + source.remaining() + ", but copy is " + width + "x" + height + " of format " + String.valueOf(format) + ")");
         } else if (destX + width <= destination.getWidth(mipLevel) && destY + height <= destination.getHeight(mipLevel)) {
            if (destination.isClosed()) {
               throw new IllegalStateException("Destination texture is closed");
            } else if ((destination.usage() & 1) == 0) {
               throw new IllegalStateException("Color texture must have USAGE_COPY_DST to be a destination for a write");
            } else if (depthOrLayer >= destination.getDepthOrLayers()) {
               throw new UnsupportedOperationException("Depth or layer is out of range, must be >= 0 and < " + destination.getDepthOrLayers());
            } else {
               this.backend.writeToTexture(destination, source, format, mipLevel, depthOrLayer, destX, destY, width, height);
            }
         } else {
            throw new IllegalArgumentException("Dest texture (" + destination.getWidth(mipLevel) + "x" + destination.getHeight(mipLevel) + ") is not large enough to write a rectangle of " + width + "x" + height + " at " + destX + "x" + destY);
         }
      } else {
         throw new IllegalArgumentException("Invalid mipLevel, must be >= 0 and < " + destination.getMipLevels());
      }
   }

   public void copyTextureToBuffer(final GpuTexture source, final GpuBuffer destination, final long offset, final Runnable callback, final int mipLevel) {
      if (this.isInRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else {
         this.backend.copyTextureToBuffer(source, destination, offset, callback, mipLevel);
      }
   }

   public void copyTextureToBuffer(final GpuTexture source, final GpuBuffer destination, final long offset, final Runnable callback, final int mipLevel, final int x, final int y, final int width, final int height) {
      if (this.isInRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else if (mipLevel >= 0 && mipLevel < source.getMipLevels()) {
         if ((long)width * (long)height * (long)source.getFormat().pixelSize() + offset > destination.size()) {
            long var10002 = destination.size();
            throw new IllegalArgumentException("Buffer of size " + var10002 + " is not large enough to hold " + width + "x" + height + " pixels (" + source.getFormat().pixelSize() + " bytes each) starting from offset " + offset);
         } else if ((source.usage() & 2) == 0) {
            throw new IllegalArgumentException("Texture needs USAGE_COPY_SRC to be a source for a copy");
         } else if ((destination.usage() & 8) == 0) {
            throw new IllegalArgumentException("Buffer needs USAGE_COPY_DST to be a destination for a copy");
         } else if (x + width <= source.getWidth(mipLevel) && y + height <= source.getHeight(mipLevel)) {
            if (source.isClosed()) {
               throw new IllegalStateException("Source texture is closed");
            } else if (destination.isClosed()) {
               throw new IllegalStateException("Destination buffer is closed");
            } else if (source.getDepthOrLayers() > 1) {
               throw new UnsupportedOperationException("Textures with multiple depths or layers are not yet supported for copying");
            } else {
               this.backend.copyTextureToBuffer(source, destination, offset, callback, mipLevel, x, y, width, height);
            }
         } else {
            throw new IllegalArgumentException("Copy source texture (" + source.getWidth(mipLevel) + "x" + source.getHeight(mipLevel) + ") is not large enough to read a rectangle of " + width + "x" + height + " from " + x + "," + y);
         }
      } else {
         throw new IllegalArgumentException("Invalid mipLevel " + mipLevel + ", must be >= 0 and < " + source.getMipLevels());
      }
   }

   public void copyTextureToTexture(final GpuTexture source, final GpuTexture destination, final int mipLevel, final int destX, final int destY, final int sourceX, final int sourceY, final int width, final int height) {
      if (this.isInRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else if (mipLevel >= 0 && mipLevel < source.getMipLevels() && mipLevel < destination.getMipLevels()) {
         if (destX + width <= destination.getWidth(mipLevel) && destY + height <= destination.getHeight(mipLevel)) {
            if (sourceX + width <= source.getWidth(mipLevel) && sourceY + height <= source.getHeight(mipLevel)) {
               if (source.isClosed()) {
                  throw new IllegalStateException("Source texture is closed");
               } else if (destination.isClosed()) {
                  throw new IllegalStateException("Destination texture is closed");
               } else if ((source.usage() & 2) == 0) {
                  throw new IllegalArgumentException("Texture needs USAGE_COPY_SRC to be a source for a copy");
               } else if ((destination.usage() & 1) == 0) {
                  throw new IllegalArgumentException("Texture needs USAGE_COPY_DST to be a destination for a copy");
               } else if (source.getDepthOrLayers() > 1) {
                  throw new UnsupportedOperationException("Textures with multiple depths or layers are not yet supported for copying");
               } else if (destination.getDepthOrLayers() > 1) {
                  throw new UnsupportedOperationException("Textures with multiple depths or layers are not yet supported for copying");
               } else {
                  this.backend.copyTextureToTexture(source, destination, mipLevel, destX, destY, sourceX, sourceY, width, height);
               }
            } else {
               throw new IllegalArgumentException("Source texture (" + source.getWidth(mipLevel) + "x" + source.getHeight(mipLevel) + ") is not large enough to read a rectangle of " + width + "x" + height + " at " + sourceX + "x" + sourceY);
            }
         } else {
            throw new IllegalArgumentException("Dest texture (" + destination.getWidth(mipLevel) + "x" + destination.getHeight(mipLevel) + ") is not large enough to write a rectangle of " + width + "x" + height + " at " + destX + "x" + destY);
         }
      } else {
         throw new IllegalArgumentException("Invalid mipLevel " + mipLevel + ", must be >= 0 and < " + source.getMipLevels() + " and < " + destination.getMipLevels());
      }
   }

   public GpuFence createFence() {
      if (this.isInRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else {
         return this.backend.createFence();
      }
   }

   public void writeTimestamp(final GpuQueryPool pool, final int index) {
      if (index >= 0 && index <= pool.size()) {
         this.backend.writeTimestamp(pool, index);
      } else {
         throw new IllegalStateException("Index " + index + " is out of range for query pool of size " + pool.size());
      }
   }

   private void verifyColorTexture(final GpuTexture colorTexture) {
      if (!colorTexture.getFormat().hasColorAspect()) {
         throw new IllegalStateException("Trying to clear a non-color texture as color");
      } else if (colorTexture.isClosed()) {
         throw new IllegalStateException("Color texture is closed");
      } else if ((colorTexture.usage() & 8) == 0) {
         throw new IllegalStateException("Color texture must have USAGE_RENDER_ATTACHMENT");
      } else if ((colorTexture.usage() & 1) == 0) {
         throw new IllegalStateException("Color texture must have USAGE_COPY_DST");
      } else if (colorTexture.getDepthOrLayers() > 1) {
         throw new UnsupportedOperationException("Clearing a texture with multiple layers or depths is not yet supported");
      }
   }

   private void verifyDepthTexture(final GpuTexture depthTexture) {
      if (!depthTexture.getFormat().hasDepthAspect()) {
         throw new IllegalStateException("Trying to clear a non-depth texture as depth");
      } else if (depthTexture.isClosed()) {
         throw new IllegalStateException("Depth texture is closed");
      } else if ((depthTexture.usage() & 8) == 0) {
         throw new IllegalStateException("Depth texture must have USAGE_RENDER_ATTACHMENT");
      } else if ((depthTexture.usage() & 1) == 0) {
         throw new IllegalStateException("Depth texture must have USAGE_COPY_DST");
      } else if (depthTexture.getDepthOrLayers() > 1) {
         throw new UnsupportedOperationException("Clearing a texture with multiple layers or depths is not yet supported");
      }
   }

   private void verifyRegion(final GpuTexture colorTexture, final int regionX, final int regionY, final int regionWidth, final int regionHeight) {
      if (regionX >= 0 && regionX < colorTexture.getWidth(0)) {
         if (regionY >= 0 && regionY < colorTexture.getHeight(0)) {
            if (regionWidth <= 0) {
               throw new IllegalArgumentException("regionWidth should be greater than 0");
            } else if (regionX + regionWidth > colorTexture.getWidth(0)) {
               throw new IllegalArgumentException("regionWidth + regionX should be less than the texture width");
            } else if (regionHeight <= 0) {
               throw new IllegalArgumentException("regionHeight should be greater than 0");
            } else if (regionY + regionHeight > colorTexture.getHeight(0)) {
               throw new IllegalArgumentException("regionWidth + regionX should be less than the texture height");
            }
         } else {
            throw new IllegalArgumentException("regionY should not be outside of the texture");
         }
      } else {
         throw new IllegalArgumentException("regionX should not be outside of the texture");
      }
   }
}
