package com.mojang.blaze3d.textures;

public abstract class GpuTexture implements AutoCloseable {
   private final TextureFormat format;
   private final int width;
   private final int height;
   private final int mipLevels;
   private final String label;
   protected AddressMode addressModeU;
   protected AddressMode addressModeV;
   protected FilterMode minFilter;
   protected FilterMode magFilter;
   protected boolean useMipmaps;

   public GpuTexture(String var1, TextureFormat var2, int var3, int var4, int var5) {
      super();
      this.addressModeU = AddressMode.REPEAT;
      this.addressModeV = AddressMode.REPEAT;
      this.minFilter = FilterMode.NEAREST;
      this.magFilter = FilterMode.LINEAR;
      this.useMipmaps = true;
      this.label = var1;
      this.format = var2;
      this.width = var3;
      this.height = var4;
      this.mipLevels = var5;
   }

   public int getWidth(int var1) {
      return this.width >> var1;
   }

   public int getHeight(int var1) {
      return this.height >> var1;
   }

   public int getMipLevels() {
      return this.mipLevels;
   }

   public TextureFormat getFormat() {
      return this.format;
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
      this.useMipmaps = var3;
   }

   public String getLabel() {
      return this.label;
   }

   public abstract void close();
}
