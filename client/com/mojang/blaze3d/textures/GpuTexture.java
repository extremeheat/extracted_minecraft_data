package com.mojang.blaze3d.textures;

import com.mojang.blaze3d.GpuOutOfMemoryException;
import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.IntBuffer;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class GpuTexture implements AutoCloseable {
   private final int id;
   private final TextureFormat format;
   private final int width;
   private final int height;
   private final int mipLevels;
   private final String label;
   private boolean closed;
   private AddressMode addressModeU;
   private AddressMode addressModeV;
   private FilterMode minFilter;
   private FilterMode magFilter;
   private boolean useMipmaps;
   private boolean modesDirty;

   public GpuTexture(@Nullable Supplier<String> var1, TextureFormat var2, int var3, int var4, int var5) {
      this((String)null, var2, var3, var4, var5);
   }

   public GpuTexture(@Nullable String var1, TextureFormat var2, int var3, int var4, int var5) {
      super();
      this.addressModeU = AddressMode.REPEAT;
      this.addressModeV = AddressMode.REPEAT;
      this.minFilter = FilterMode.NEAREST;
      this.magFilter = FilterMode.LINEAR;
      this.useMipmaps = true;
      this.modesDirty = true;
      this.format = var2;
      this.width = var3;
      this.height = var4;
      this.id = GlStateManager._genTexture();
      this.mipLevels = var5;
      this.label = var1 == null ? String.valueOf(this.id) : var1;
      if (var5 < 1) {
         throw new IllegalArgumentException("mipLevels must be at least 1");
      } else {
         this.bind();
         GlStateManager._texParameter(3553, 33085, var5 - 1);
         GlStateManager._texParameter(3553, 33082, 0);
         GlStateManager._texParameter(3553, 33083, var5 - 1);
         if (var2 == TextureFormat.DEPTH32) {
            GlStateManager._texParameter(3553, 34892, 0);
         }

         for(int var6 = 0; var6 < var5; ++var6) {
            GlStateManager._texImage2D(3553, var6, var2.glInternalFormatId(), var3 >> var6, var4 >> var6, 0, var2.glExternalFormatId(), var2.glType(), (IntBuffer)null);
         }

         int var7 = GlStateManager._getError();
         if (var7 == 1285) {
            throw new GpuOutOfMemoryException("Could not allocate texture of " + var3 + "x" + var4 + " for " + this.label);
         } else if (var7 != 0) {
            throw new IllegalStateException("OpenGL error " + var7);
         }
      }
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
      this.modesDirty = true;
   }

   public void setTextureFilter(FilterMode var1, boolean var2) {
      this.setTextureFilter(var1, var1, var2);
   }

   public void setTextureFilter(FilterMode var1, FilterMode var2, boolean var3) {
      this.minFilter = var1;
      this.magFilter = var2;
      this.useMipmaps = var3;
      this.modesDirty = true;
   }

   public void write(NativeImage var1) {
      if (var1.getWidth() == this.width && var1.getHeight() == this.height) {
         this.write(var1, 0, 0, 0, this.width, this.height, 0, 0);
      } else {
         int var10002 = this.width;
         throw new IllegalArgumentException("Cannot replace texture of size " + var10002 + "x" + this.height + " with image of size " + var1.getWidth() + "x" + var1.getHeight());
      }
   }

   public void write(NativeImage var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      if (var2 >= 0 && var2 < this.mipLevels) {
         if (var7 + var5 <= var1.getWidth() && var8 + var6 <= var1.getHeight()) {
            if (var3 + var5 <= this.getWidth(var2) && var4 + var6 <= this.getHeight(var2)) {
               this.bind();
               GlStateManager._pixelStore(3314, var1.getWidth());
               GlStateManager._pixelStore(3316, var7);
               GlStateManager._pixelStore(3315, var8);
               GlStateManager._pixelStore(3317, var1.format().components());
               GlStateManager._texSubImage2D(3553, var2, var3, var4, var5, var6, var1.format().glFormat(), 5121, var1.getPointer());
            } else {
               throw new IllegalArgumentException("Dest texture (" + var5 + "x" + var6 + ") is not large enough to write a rectangle of " + var5 + "x" + var6 + " at " + var3 + "x" + var4 + " (at mip level " + var2 + ")");
            }
         } else {
            int var10002 = var1.getWidth();
            throw new IllegalArgumentException("Copy source (" + var10002 + "x" + var1.getHeight() + ") is not large enough to read a rectangle of " + var5 + "x" + var6 + " from " + var7 + "x" + var8);
         }
      } else {
         throw new IllegalArgumentException("Invalid mipLevel " + var2 + ", must be >= 0 and < " + this.mipLevels);
      }
   }

   public void copyToBuffer(GpuBuffer var1, int var2, Runnable var3, int var4) {
      this.copyToBuffer(var1, var2, var3, var4, 0, 0, this.getWidth(var4), this.getHeight(var4));
   }

   public void copyToBuffer(GpuBuffer var1, int var2, Runnable var3, int var4, int var5, int var6, int var7, int var8) {
      if (var4 >= 0 && var4 < this.mipLevels) {
         if (this.getWidth(var4) * this.getHeight(var4) * this.format.pixelSize() + var2 > var1.size()) {
            int var10002 = var1.size();
            throw new IllegalArgumentException("Buffer of size " + var10002 + " is not large enough to hold " + var7 + "x" + var8 + " pixels (" + this.format.pixelSize() + " bytes each) starting from offset " + var2);
         } else if (var1.type() != BufferType.PIXEL_PACK) {
            throw new IllegalArgumentException("Buffer of type " + String.valueOf(var1.type()) + " cannot be used to retrieve a texture");
         } else if (var5 + var7 <= this.getWidth(var4) && var6 + var8 <= this.getHeight(var4)) {
            int var9 = GlStateManager.glGenFramebuffers();
            GlStateManager._glBindFramebuffer(36008, var9);
            var1.bind();
            GlStateManager._glFramebufferTexture2D(36008, 36064, 3553, this.id, var4);
            GlStateManager._pixelStore(3330, var7);
            GlStateManager._readPixels(var5, var6, var7, var8, this.format.glExternalFormatId(), this.format.glType(), (long)var2);
            RenderSystem.queueFencedTask(var3);
            GlStateManager._glBindFramebuffer(36008, 0);
            GlStateManager._glDeleteFramebuffers(var9);
            var1.unbind();
            int var10 = GlStateManager._getError();
            if (var10 != 0) {
               throw new IllegalStateException("Couldn't perform copyTobuffer for texture " + this.label + ": GL error " + var10);
            }
         } else {
            throw new IllegalArgumentException("Copy source texture (" + this.getWidth(var4) + "x" + this.getHeight(var4) + ") is not large enough to read a rectangle of " + var7 + "x" + var8 + " from " + var5 + "," + var6);
         }
      } else {
         throw new IllegalArgumentException("Invalid mipLevel " + var4 + ", must be >= 0 and < " + this.mipLevels);
      }
   }

   public void write(IntBuffer var1, NativeImage.Format var2, int var3, int var4, int var5, int var6, int var7) {
      if (var3 >= 0 && var3 < this.mipLevels) {
         if (var6 * var7 > var1.remaining()) {
            throw new IllegalArgumentException("Copy would overrun the source buffer (remaining length of " + var1.remaining() + ", but copy is " + var6 + "x" + var7 + ")");
         } else if (var4 + var6 <= this.getWidth(var3) && var5 + var7 <= this.getHeight(var3)) {
            this.bind();
            GlStateManager._pixelStore(3314, var6);
            GlStateManager._pixelStore(3316, 0);
            GlStateManager._pixelStore(3315, 0);
            GlStateManager._pixelStore(3317, var2.components());
            GlStateManager._texSubImage2D(3553, var3, var4, var5, var6, var7, var2.glFormat(), 5121, var1);
         } else {
            throw new IllegalArgumentException("Dest texture (" + var6 + "x" + var7 + ") is not large enough to write a rectangle of " + var6 + "x" + var7 + " at " + var4 + "x" + var5);
         }
      } else {
         throw new IllegalArgumentException("Invalid mipLevel, must be >= 0 and < " + this.mipLevels);
      }
   }

   public void bind() {
      GlStateManager._bindTexture(this.id);
      if (this.modesDirty) {
         GlStateManager._texParameter(3553, 10242, this.addressModeU.id);
         GlStateManager._texParameter(3553, 10243, this.addressModeV.id);
         switch (this.minFilter) {
            case NEAREST -> GlStateManager._texParameter(3553, 10241, this.useMipmaps ? 9986 : 9728);
            case LINEAR -> GlStateManager._texParameter(3553, 10241, this.useMipmaps ? 9987 : 9729);
         }

         switch (this.magFilter) {
            case NEAREST -> GlStateManager._texParameter(3553, 10240, 9728);
            case LINEAR -> GlStateManager._texParameter(3553, 10240, 9729);
         }

         this.modesDirty = false;
      }

   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         GlStateManager._deleteTexture(this.id);
      }
   }

   public int glId() {
      return this.id;
   }

   public String getLabel() {
      return this.label;
   }
}
