package com.mojang.blaze3d.textures;

public abstract class GpuSampler implements AutoCloseable {
   public GpuSampler() {
      super();
   }

   public abstract AddressMode getAddressModeU();

   public abstract AddressMode getAddressModeV();

   public abstract FilterMode getMinFilter();

   public abstract FilterMode getMagFilter();

   public abstract void close();
}
