package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.ArrayList;
import java.util.List;

public class GlTextureView extends GpuTextureView implements FrameBufferAttachment {
   private static final int EMPTY = -1;
   private boolean closed;
   private final FrameBufferCache frameBufferCache;
   private final List<FrameBufferCache.CacheKey> fboKeys = new ArrayList();

   protected GlTextureView(final GlTexture texture, final int baseMipLevel, final int mipLevels, final FrameBufferCache frameBufferCache) {
      super(texture, baseMipLevel, mipLevels);
      texture.addViews();
      this.frameBufferCache = frameBufferCache;
   }

   public boolean isClosed() {
      return this.closed;
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         this.texture().removeViews();

         while(!this.fboKeys.isEmpty()) {
            this.frameBufferCache.destroyFbo((FrameBufferCache.CacheKey)this.fboKeys.getLast());
         }
      }

   }

   public GlTexture texture() {
      return (GlTexture)super.texture();
   }

   public int glId() {
      return this.texture().id;
   }

   public int fboMipLevel() {
      return this.baseMipLevel();
   }

   public void addAssociatedFbo(final FrameBufferCache.CacheKey fboKey) {
      this.fboKeys.add(fboKey);
   }

   public void removeAssociatedFbo(final FrameBufferCache.CacheKey fboKey) {
      this.fboKeys.remove(fboKey);
   }
}
