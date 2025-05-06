package com.mojang.blaze3d.textures;

import com.mojang.blaze3d.DontObfuscate;

@DontObfuscate
public abstract class GpuTextureView implements AutoCloseable {
   private final GpuTexture texture;
   private final int baseMipLevel;
   private final int mipLevels;

   public GpuTextureView(GpuTexture var1, int var2, int var3) {
      super();
      this.texture = var1;
      this.baseMipLevel = var2;
      this.mipLevels = var3;
   }

   public abstract void close();

   public GpuTexture texture() {
      return this.texture;
   }

   public int baseMipLevel() {
      return this.baseMipLevel;
   }

   public int mipLevels() {
      return this.mipLevels;
   }

   public int getWidth(int var1) {
      return this.texture.getWidth(var1 + this.baseMipLevel);
   }

   public int getHeight(int var1) {
      return this.texture.getHeight(var1 + this.baseMipLevel);
   }

   public abstract boolean isClosed();
}
