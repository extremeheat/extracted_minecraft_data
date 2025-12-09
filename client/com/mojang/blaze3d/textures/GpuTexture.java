package com.mojang.blaze3d.textures;

import com.mojang.blaze3d.DontObfuscate;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
   private final @GpuTexture.Usage int usage;
   private final String label;

   public GpuTexture(@GpuTexture.Usage int var1, String var2, TextureFormat var3, int var4, int var5, int var6, int var7) {
      super();
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

   public @GpuTexture.Usage int usage() {
      return this.usage;
   }

   public String getLabel() {
      return this.label;
   }

   public abstract void close();

   public abstract boolean isClosed();

   @Retention(RetentionPolicy.CLASS)
   @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.TYPE_USE})
   public @interface Usage {
   }
}
