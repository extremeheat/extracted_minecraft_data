package com.mojang.renderpearl.api.textures;

import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import java.util.OptionalDouble;

public interface GpuSampler extends UncheckedAutoCloseable {
   AddressMode getAddressModeU();

   AddressMode getAddressModeV();

   FilterMode getMinFilter();

   FilterMode getMagFilter();

   int getMaxAnisotropy();

   OptionalDouble getMaxLod();

   boolean isClosed();
}
