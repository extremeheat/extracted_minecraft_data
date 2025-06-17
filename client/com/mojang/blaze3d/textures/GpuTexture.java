package com.mojang.blaze3d.textures;

import com.mojang.blaze3d.DontObfuscate;

@DontObfuscate
public abstract class GpuTexture implements AutoCloseable {
   public static final int USAGE_COPY_DST = 1;
   public static final int USAGE_COPY_SRC = 2;
   public static final int USAGE_TEXTURE_BINDING = 4;
   public static final int USAGE_RENDER_ATTACHMENT = 8;
   public static final int USAGE_CUBEMAP_COMPATIBLE = 16;
   private final TextureFormat format;
   private final int width;
   private final int height;
   private final int depthOrLayers;
   private final int mipLevels;
   private final int usage;
   private final String label;
   protected AddressMode addressModeU;
   protected AddressMode addressModeV;
   protected FilterMode minFilter;
   protected FilterMode magFilter;
   protected boolean useMipmaps;

   public GpuTexture(int var1, String var2, TextureFormat var3, int var4, int var5, int var6, int var7) {
      super();
      this.addressModeU = AddressMode.REPEAT;
      this.addressModeV = AddressMode.REPEAT;
      this.minFilter = FilterMode.NEAREST;
      this.magFilter = FilterMode.LINEAR;
      this.useMipmaps = true;
      this.usage = var1;
      this.label = var2;
      this.format = var3;
      this.width = var4;
      this.height = var5;
      this.depthOrLayers = var6;
      this.mipLevels = var7;
   }

   public int getWidth(int var1) {
      return this.width >> var1;
   }

   public int getHeight(int var1) {
      return this.height >> var1;
   }

   public int getDepthOrLayers() {
      return this.depthOrLayers;
   }

   public int getMipLevels() {
      return this.mipLevels;
   }

   public TextureFormat getFormat() {
      return this.format;
   }

   public int usage() {
      return this.usage;
   }

   public void setAddressMode(AddressMode var1) {
      this.setAddressMode(var1, var1);
   }

   public void setAddressMode(AddressMode var1, AddressMode var2) {
      this.addressModeU = var1;
      this.addressModeV = var2;
   }

   public void setTextureFilter(FilterMode var1, boolean var2) {
      this.setTextureFilter(var1, var1, var2);
   }

   public void setTextureFilter(FilterMode var1, FilterMode var2, boolean var3) {
      this.minFilter = var1;
      this.magFilter = var2;
      this.setUseMipmaps(var3);
   }

   public void setUseMipmaps(boolean var1) {
      this.useMipmaps = var1;
   }

   public String getLabel() {
      return this.label;
   }

   public abstract void close();

   public abstract boolean isClosed();
}
