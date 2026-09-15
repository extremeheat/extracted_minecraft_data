package com.mojang.renderpearl.util;

import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;

public record TextureViewAndSampler(GpuTextureView view, GpuSampler sampler) {
   public TextureViewAndSampler {
      super();
   }
}
