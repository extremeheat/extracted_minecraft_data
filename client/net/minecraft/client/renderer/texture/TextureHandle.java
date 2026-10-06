package net.minecraft.client.renderer.texture;

import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;

public interface TextureHandle {
   GpuTextureView textureView();

   GpuSampler sampler();
}
