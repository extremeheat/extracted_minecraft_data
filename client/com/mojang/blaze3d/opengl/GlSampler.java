package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import org.lwjgl.opengl.GL33C;

public class GlSampler extends GpuSampler {
   private final int id;
   private final AddressMode addressModeU;
   private final AddressMode addressModeV;
   private final FilterMode minFilter;
   private final FilterMode magFilter;
   private final int maxAnisotropy;
   private boolean closed;

   public GlSampler(AddressMode var1, AddressMode var2, FilterMode var3, FilterMode var4, int var5) {
      super();
      this.addressModeU = var1;
      this.addressModeV = var2;
      this.minFilter = var3;
      this.magFilter = var4;
      this.maxAnisotropy = var5;
      this.id = GL33C.glGenSamplers();
      GL33C.glSamplerParameteri(this.id, 10242, GlConst.toGl(var1));
      GL33C.glSamplerParameteri(this.id, 10243, GlConst.toGl(var2));
      if (var5 > 1) {
         GL33C.glSamplerParameterf(this.id, 34046, (float)var5);
      }

      switch (var3) {
         case NEAREST -> GL33C.glSamplerParameteri(this.id, 10241, 9986);
         case LINEAR -> GL33C.glSamplerParameteri(this.id, 10241, 9987);
      }

      switch (var4) {
         case NEAREST -> GL33C.glSamplerParameteri(this.id, 10240, 9728);
         case LINEAR -> GL33C.glSamplerParameteri(this.id, 10240, 9729);
      }

   }

   public int getId() {
      return this.id;
   }

   public AddressMode getAddressModeU() {
      return this.addressModeU;
   }

   public AddressMode getAddressModeV() {
      return this.addressModeV;
   }

   public FilterMode getMinFilter() {
      return this.minFilter;
   }

   public FilterMode getMagFilter() {
      return this.magFilter;
   }

   public int getMaxAnisotropy() {
      return this.maxAnisotropy;
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         GL33C.glDeleteSamplers(this.id);
      }

   }

   public boolean isClosed() {
      return this.closed;
   }
}
