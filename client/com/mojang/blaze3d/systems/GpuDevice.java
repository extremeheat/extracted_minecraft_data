package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

@DontObfuscate
public interface GpuDevice {
   CommandEncoder createCommandEncoder();

   GpuTexture createTexture(@Nullable Supplier<String> var1, int var2, TextureFormat var3, int var4, int var5, int var6, int var7);

   GpuTexture createTexture(@Nullable String var1, int var2, TextureFormat var3, int var4, int var5, int var6, int var7);

   GpuTextureView createTextureView(GpuTexture var1);

   GpuTextureView createTextureView(GpuTexture var1, int var2, int var3);

   GpuBuffer createBuffer(@Nullable Supplier<String> var1, int var2, int var3);

   GpuBuffer createBuffer(@Nullable Supplier<String> var1, int var2, ByteBuffer var3);

   String getImplementationInformation();

   List<String> getLastDebugMessages();

   boolean isDebuggingEnabled();

   String getVendor();

   String getBackendName();

   String getVersion();

   String getRenderer();

   int getMaxTextureSize();

   int getUniformOffsetAlignment();

   default CompiledRenderPipeline precompilePipeline(RenderPipeline var1) {
      return this.precompilePipeline(var1, (BiFunction)null);
   }

   CompiledRenderPipeline precompilePipeline(RenderPipeline var1, @Nullable BiFunction<ResourceLocation, ShaderType, String> var2);

   void clearPipelineCache();

   List<String> getEnabledExtensions();

   void close();
}
