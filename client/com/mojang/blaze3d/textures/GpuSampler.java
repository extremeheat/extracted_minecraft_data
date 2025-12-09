package com.mojang.blaze3d.textures;

import java.util.OptionalDouble;

public abstract class GpuSampler implements AutoCloseable {
   public GpuSampler() {
      super();
   }

   public abstract AddressMode getAddressModeU();

   public abstract AddressMode getAddressModeV();

   public abstract FilterMode getMinFilter();

   public abstract FilterMode getMagFilter();

   public abstract int getMaxAnisotropy();

   public abstract OptionalDouble getMaxLod();

   public abstract void close();
}
