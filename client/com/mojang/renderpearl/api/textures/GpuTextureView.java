package com.mojang.renderpearl.api.textures;

import com.mojang.renderpearl.util.UncheckedAutoCloseable;

public interface GpuTextureView extends UncheckedAutoCloseable {
   boolean isClosed();

   GpuTexture texture();

   int baseMipLevel();

   int mipLevels();

   int getWidth(int mipLevel);

   int getHeight(int mipLevel);
}
