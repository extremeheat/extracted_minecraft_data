package com.mojang.renderpearl.backend.opengl;

import com.mojang.renderpearl.api.textures.AddressMode;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import java.util.OptionalDouble;
import org.lwjgl.opengl.GL33C;

public class GlSampler extends GpuSampler {
   private final int id;
   private final AddressMode addressModeU;
   private final AddressMode addressModeV;
   private final FilterMode minFilter;
   private final FilterMode magFilter;
   private final int maxAnisotropy;
   private final OptionalDouble maxLod;
   private boolean closed;

   public GlSampler(final AddressMode addressModeU, final AddressMode addressModeV, final FilterMode minFilter, final FilterMode magFilter, final int maxAnisotropy, final OptionalDouble maxLod) {
      super();
      this.addressModeU = addressModeU;
      this.addressModeV = addressModeV;
      this.minFilter = minFilter;
      this.magFilter = magFilter;
      this.maxAnisotropy = maxAnisotropy;
      this.maxLod = maxLod;
      this.id = GL33C.glGenSamplers();
      GL33C.glSamplerParameteri(this.id, 10242, GlConst.toGl(addressModeU));
      GL33C.glSamplerParameteri(this.id, 10243, GlConst.toGl(addressModeV));
      if (maxAnisotropy > 1) {
         GL33C.glSamplerParameterf(this.id, 34046, (float)maxAnisotropy);
      }

      switch (minFilter) {
         case NEAREST -> GL33C.glSamplerParameteri(this.id, 10241, 9986);
         case LINEAR -> GL33C.glSamplerParameteri(this.id, 10241, 9987);
      }

      switch (magFilter) {
         case NEAREST -> GL33C.glSamplerParameteri(this.id, 10240, 9728);
         case LINEAR -> GL33C.glSamplerParameteri(this.id, 10240, 9729);
      }

      if (maxLod.isPresent()) {
         GL33C.glSamplerParameterf(this.id, 33083, (float)maxLod.getAsDouble());
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

   public OptionalDouble getMaxLod() {
      return this.maxLod;
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
