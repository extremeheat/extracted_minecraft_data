package com.mojang.renderpearl.backend.opengl;

import com.mojang.renderpearl.api.GpuFormat;

public interface FrameBufferAttachment {
   int glId();

   int fboMipLevel();

   void addAssociatedFbo(FrameBufferCache.CacheKey fboKey);

   void removeAssociatedFbo(FrameBufferCache.CacheKey fboKey);

   GpuFormat getFormat();
}
