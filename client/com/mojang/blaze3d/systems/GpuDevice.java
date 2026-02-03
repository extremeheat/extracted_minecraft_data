package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.ShaderSource;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

public class GpuDevice {
   private final GpuDeviceBackend backend;

   public GpuDevice(final GpuDeviceBackend backend) {
      super();
      this.backend = backend;
   }

   public CommandEncoder createCommandEncoder() {
      return new CommandEncoder(this.backend.createCommandEncoder());
   }

   public GpuSampler createSampler(final AddressMode addressModeU, final AddressMode addressModeV, final FilterMode minFilter, final FilterMode magFilter, final int maxAnisotropy, final OptionalDouble maxLod) {
      if (maxAnisotropy >= 1 && maxAnisotropy <= this.backend.getMaxSupportedAnisotropy()) {
         return this.backend.createSampler(addressModeU, addressModeV, minFilter, magFilter, maxAnisotropy, maxLod);
      } else {
         int var10002 = this.getMaxSupportedAnisotropy();
         throw new IllegalArgumentException("maxAnisotropy out of range; must be >= 1 and <= " + var10002 + ", but was " + maxAnisotropy);
      }
   }

   public GpuTexture createTexture(final @Nullable Supplier<String> label, final @GpuTexture.Usage int usage, final TextureFormat format, final int width, final int height, final int depthOrLayers, final int mipLevels) {
      this.verifyTextureCreationArgs(usage, width, height, depthOrLayers, mipLevels);
      return this.backend.createTexture(label, usage, format, width, height, depthOrLayers, mipLevels);
   }

   public GpuTexture createTexture(final @Nullable String label, final @GpuTexture.Usage int usage, final TextureFormat format, final int width, final int height, final int depthOrLayers, final int mipLevels) {
      this.verifyTextureCreationArgs(usage, width, height, depthOrLayers, mipLevels);
      return this.backend.createTexture(label, usage, format, width, height, depthOrLayers, mipLevels);
   }

   private void verifyTextureCreationArgs(final @GpuTexture.Usage int usage, final int width, final int height, final int depthOrLayers, final int mipLevels) {
      if (mipLevels < 1) {
         throw new IllegalArgumentException("mipLevels must be at least 1");
      } else {
         int maxDimension = Math.max(width, height);
         int maxMipSupported = Mth.log2(maxDimension) + 1;
         if (mipLevels > maxMipSupported) {
            throw new IllegalArgumentException("mipLevels must be at most " + maxMipSupported + " for a texture of width " + width + " and height " + height + " (asked for " + mipLevels + " mipLevels)");
         } else if (depthOrLayers < 1) {
            throw new IllegalArgumentException("depthOrLayers must be at least 1");
         } else {
            boolean isCubemap = (usage & 16) != 0;
            if (isCubemap) {
               if (width != height) {
                  throw new IllegalArgumentException("Cubemap compatible textures must be square, but size is " + width + "x" + height);
               }

               if (depthOrLayers % 6 != 0) {
                  throw new IllegalArgumentException("Cubemap compatible textures must have a layer count with a multiple of 6, was " + depthOrLayers);
               }

               if (depthOrLayers > 6) {
                  throw new UnsupportedOperationException("Array textures are not yet supported");
               }
            } else if (depthOrLayers > 1) {
               throw new UnsupportedOperationException("Array or 3D textures are not yet supported");
            }

         }
      }
   }

   public GpuTextureView createTextureView(final GpuTexture texture) {
      this.verifyTextureViewCreationArgs(texture, 0, texture.getMipLevels());
      return this.backend.createTextureView(texture, 0, texture.getMipLevels());
   }

   public GpuTextureView createTextureView(final GpuTexture texture, final int baseMipLevel, final int mipLevels) {
      this.verifyTextureViewCreationArgs(texture, baseMipLevel, mipLevels);
      return this.backend.createTextureView(texture, baseMipLevel, mipLevels);
   }

   private void verifyTextureViewCreationArgs(final GpuTexture texture, final int baseMipLevel, final int mipLevels) {
      if (texture.isClosed()) {
         throw new IllegalArgumentException("Can't create texture view with closed texture");
      } else if (baseMipLevel < 0 || baseMipLevel + mipLevels > texture.getMipLevels()) {
         throw new IllegalArgumentException(mipLevels + " mip levels starting from " + baseMipLevel + " would be out of range for texture with only " + texture.getMipLevels() + " mip levels");
      }
   }

   public GpuBuffer createBuffer(final @Nullable Supplier<String> label, final @GpuBuffer.Usage int usage, final long size) {
      if (size <= 0L) {
         throw new IllegalArgumentException("Buffer size must be greater than zero");
      } else {
         return this.backend.createBuffer(label, usage, size);
      }
   }

   public GpuBuffer createBuffer(final @Nullable Supplier<String> label, final @GpuBuffer.Usage int usage, final ByteBuffer data) {
      if (!data.hasRemaining()) {
         throw new IllegalArgumentException("Buffer source must not be empty");
      } else {
         return this.backend.createBuffer(label, usage, data);
      }
   }

   public String getImplementationInformation() {
      return this.backend.getImplementationInformation();
   }

   public List<String> getLastDebugMessages() {
      return this.backend.getLastDebugMessages();
   }

   public boolean isDebuggingEnabled() {
      return this.backend.isDebuggingEnabled();
   }

   public String getVendor() {
      return this.backend.getVendor();
   }

   public String getBackendName() {
      return this.backend.getBackendName();
   }

   public String getVersion() {
      return this.backend.getVersion();
   }

   public String getRenderer() {
      return this.backend.getRenderer();
   }

   public int getMaxTextureSize() {
      return this.backend.getMaxTextureSize();
   }

   public int getUniformOffsetAlignment() {
      return this.backend.getUniformOffsetAlignment();
   }

   public CompiledRenderPipeline precompilePipeline(final RenderPipeline pipeline) {
      return this.precompilePipeline(pipeline, (ShaderSource)null);
   }

   public CompiledRenderPipeline precompilePipeline(final RenderPipeline pipeline, final @Nullable ShaderSource shaderSource) {
      return this.backend.precompilePipeline(pipeline, shaderSource);
   }

   public void clearPipelineCache() {
      this.backend.clearPipelineCache();
   }

   public List<String> getEnabledExtensions() {
      return this.backend.getEnabledExtensions();
   }

   public int getMaxSupportedAnisotropy() {
      return this.backend.getMaxSupportedAnisotropy();
   }

   public void close() {
      this.backend.close();
   }

   public void setVsync(final boolean enabled) {
      this.backend.setVsync(enabled);
   }

   public void presentFrame() {
      this.backend.presentFrame();
   }

   public boolean isZZeroToOne() {
      return this.backend.isZZeroToOne();
   }
}
