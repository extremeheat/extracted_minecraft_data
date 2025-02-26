package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public interface GpuDevice {
   CommandEncoder createCommandEncoder();

   GpuTexture createTexture(@Nullable Supplier<String> var1, TextureFormat var2, int var3, int var4, int var5);

   GpuTexture createTexture(@Nullable String var1, TextureFormat var2, int var3, int var4, int var5);

   GpuBuffer createBuffer(@Nullable Supplier<String> var1, BufferType var2, BufferUsage var3, int var4);

   GpuBuffer createBuffer(@Nullable Supplier<String> var1, BufferType var2, BufferUsage var3, ByteBuffer var4);

   String getImplementationInformation();

   List<String> getLastDebugMessages();

   boolean isDebuggingEnabled();

   String getVendor();

   String getBackendName();

   String getVersion();

   String getRenderer();

   int getMaxTextureSize();

   default CompiledRenderPipeline precompilePipeline(RenderPipeline var1) {
      return this.precompilePipeline(var1, (BiFunction)null);
   }

   CompiledRenderPipeline precompilePipeline(RenderPipeline var1, @Nullable BiFunction<ResourceLocation, ShaderType, String> var2);

   void clearPipelineCache();
}
