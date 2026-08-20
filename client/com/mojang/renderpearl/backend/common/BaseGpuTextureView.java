package com.mojang.renderpearl.backend.common;

import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;

public abstract class BaseGpuTextureView implements GpuTextureView {
   private final GpuTexture texture;
   private final int baseMipLevel;
   private final int mipLevels;

   protected BaseGpuTextureView(final GpuTexture texture, final int baseMipLevel, final int mipLevels) {
      super();
      this.texture = texture;
      this.baseMipLevel = baseMipLevel;
      this.mipLevels = mipLevels;
   }

   public GpuTexture texture() {
      return this.texture;
   }

   public int baseMipLevel() {
      return this.baseMipLevel;
   }

   public int mipLevels() {
      return this.mipLevels;
   }

   public int getWidth(final int mipLevel) {
      return this.texture.getWidth(mipLevel + this.baseMipLevel);
   }

   public int getHeight(final int mipLevel) {
      return this.texture.getHeight(mipLevel + this.baseMipLevel);
   }
}
