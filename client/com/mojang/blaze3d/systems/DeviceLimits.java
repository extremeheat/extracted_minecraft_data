package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.GpuFormat;

public record DeviceLimits(int maxAnisotropy, int minUniformOffsetAlignment, int maxTextureSize, long maxMemoryAllocationSize, int maxColorAttachments) {
   public DeviceLimits {
      super();
   }

   public int maxTextureSizeForFormat(final GpuFormat format) {
      return Integer.highestOneBit(Math.min(this.maxTextureSize, (int)Math.sqrt((double)this.maxMemoryAllocationSize / (double)format.pixelSize())));
   }
}
