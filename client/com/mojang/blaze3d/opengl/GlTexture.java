package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.textures.GpuTexture;
import java.util.ArrayList;
import java.util.List;

public class GlTexture extends GpuTexture implements FrameBufferAttachment {
   private static final int EMPTY = -1;
   protected final int id;
   private final FrameBufferCache frameBufferCache;
   private final List<FrameBufferCache.CacheKey> fboKeys = new ArrayList();
   protected boolean closed;
   private int views;

   protected GlTexture(final @GpuTexture.Usage int usage, final String label, final GpuFormat format, final int width, final int height, final int depthOrLayers, final int mipLevels, final int id, final FrameBufferCache frameBufferCache) {
      super(usage, label, format, width, height, depthOrLayers, mipLevels);
      this.id = id;
      this.frameBufferCache = frameBufferCache;
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         if (this.views == 0) {
            this.destroyImmediately();
         }

      }
   }

   private void destroyImmediately() {
      while(!this.fboKeys.isEmpty()) {
         this.frameBufferCache.destroyFbo((FrameBufferCache.CacheKey)this.fboKeys.getLast());
      }

      GlStateManager._deleteTexture(this.id);
   }

   public boolean isClosed() {
      return this.closed;
   }

   public int glId() {
      return this.id;
   }

   public int fboMipLevel() {
      return 0;
   }

   public void addAssociatedFbo(final FrameBufferCache.CacheKey fboKey) {
      this.fboKeys.add(fboKey);
   }

   public void removeAssociatedFbo(final FrameBufferCache.CacheKey fboKey) {
      this.fboKeys.remove(fboKey);
   }

   public void addViews() {
      ++this.views;
   }

   public void removeViews() {
      --this.views;
      if (this.closed && this.views == 0) {
         this.destroyImmediately();
      }

   }
}
