package com.mojang.renderpearl.api.device;

import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.commands.CommandEncoder;
import com.mojang.renderpearl.api.commands.GpuQueryPool;
import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.api.pipeline.ShaderType;
import com.mojang.renderpearl.api.pipeline.SpvModule;
import com.mojang.renderpearl.api.textures.AddressMode;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

public interface GpuDevice {
   GpuSurface createSurface(long windowHandle, final BooleanSupplier isIconified);

   CommandEncoder createCommandEncoder();

   GpuSampler createSampler(AddressMode addressModeU, AddressMode addressModeV, FilterMode minFilter, FilterMode magFilter, int maxAnisotropy, OptionalDouble maxLod);

   GpuTexture createTexture(@Nullable Supplier<String> label, @GpuTexture.Usage int usage, GpuFormat format, int width, int height, int depthOrLayers, int mipLevels);

   GpuTexture createTexture(@Nullable String label, @GpuTexture.Usage int usage, GpuFormat format, int width, int height, int depthOrLayers, int mipLevels);

   GpuTextureView createTextureView(GpuTexture texture);

   GpuTextureView createTextureView(GpuTexture texture, int baseMipLevel, int mipLevels);

   GpuBuffer createBuffer(@Nullable Supplier<String> label, @GpuBuffer.Usage int usage, long size);

   GpuBuffer createBuffer(@Nullable Supplier<String> label, @GpuBuffer.Usage int usage, ByteBuffer data);

   List<String> getLastDebugMessages();

   boolean isDebuggingEnabled();

   SpvModule createSpvModule(String name, ByteBuffer spv, ShaderType type, String entryPoint);

   CompiledRenderPipeline.Pending compilePipeline(CompiledRenderPipeline.CreateInfo createInfo);

   void close();

   GpuQueryPool createTimestampQueryPool(int size);

   DeviceInfo getDeviceInfo();
}
