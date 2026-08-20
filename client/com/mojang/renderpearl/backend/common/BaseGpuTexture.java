package com.mojang.renderpearl.backend.common;

import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.textures.GpuTexture;

public abstract class BaseGpuTexture implements GpuTexture {
   private final GpuFormat format;
   private final int width;
   private final int height;
   private final int depthOrLayers;
   private final int mipLevels;
   private final @GpuTexture.Usage int usage;
   private final String label;

   public BaseGpuTexture(final @GpuTexture.Usage int usage, final String label, final GpuFormat format, final int width, final int height, final int depthOrLayers, final int mipLevels) {
      super();
      this.usage = usage;
      this.label = label;
      this.format = format;
      this.width = width;
      this.height = height;
      this.depthOrLayers = depthOrLayers;
      this.mipLevels = mipLevels;
   }

   public int getWidth(final int mipLevel) {
      return this.width >> mipLevel;
   }

   public int getHeight(final int mipLevel) {
      return this.height >> mipLevel;
   }

   public int getDepthOrLayers() {
      return this.depthOrLayers;
   }

   public int getMipLevels() {
      return this.mipLevels;
   }

   public GpuFormat getFormat() {
      return this.format;
   }

   public @GpuTexture.Usage int usage() {
      return this.usage;
   }

   public String getLabel() {
      return this.label;
   }
}
