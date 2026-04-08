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
import org.jspecify.annotations.Nullable;

public interface GpuDeviceBackend {
   CommandEncoderBackend createCommandEncoder();

   GpuSampler createSampler(AddressMode addressModeU, AddressMode addressModeV, FilterMode minFilter, FilterMode magFilter, int maxAnisotropy, OptionalDouble maxLod);

   GpuTexture createTexture(@Nullable Supplier<String> label, final @GpuTexture.Usage int usage, TextureFormat format, int width, int height, int depthOrLayers, int mipLevels);

   GpuTexture createTexture(@Nullable String label, final @GpuTexture.Usage int usage, TextureFormat format, int width, int height, int depthOrLayers, int mipLevels);

   GpuTextureView createTextureView(GpuTexture texture);

   GpuTextureView createTextureView(GpuTexture texture, int baseMipLevel, int mipLevels);

   GpuBuffer createBuffer(@Nullable Supplier<String> label, @GpuBuffer.Usage int usage, long size);

   GpuBuffer createBuffer(@Nullable Supplier<String> label, @GpuBuffer.Usage int usage, ByteBuffer data);

   String getImplementationInformation();

   List<String> getLastDebugMessages();

   boolean isDebuggingEnabled();

   String getVendor();

   String getBackendName();

   String getVersion();

   String getRenderer();

   int getMaxTextureSize();

   int getUniformOffsetAlignment();

   CompiledRenderPipeline precompilePipeline(RenderPipeline pipeline, @Nullable ShaderSource shaderSource);

   void clearPipelineCache();

   List<String> getEnabledExtensions();

   int getMaxSupportedAnisotropy();

   void close();

   void setVsync(boolean enabled);

   void presentFrame();

   boolean isZZeroToOne();
}
