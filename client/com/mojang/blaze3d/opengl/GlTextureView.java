package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import org.jspecify.annotations.Nullable;

public class GlTextureView extends GpuTextureView {
   private static final int EMPTY = -1;
   private boolean closed;
   private int firstFboId = -1;
   private int firstFboDepthId = -1;
   private @Nullable Int2IntMap fboCache;

   protected GlTextureView(final GlTexture texture, final int baseMipLevel, final int mipLevels) {
      super(texture, baseMipLevel, mipLevels);
      texture.addViews();
   }

   public boolean isClosed() {
      return this.closed;
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         this.texture().removeViews();
         if (this.firstFboId != -1) {
            GlStateManager._glDeleteFramebuffers(this.firstFboId);
         }

         if (this.fboCache != null) {
            IntIterator var1 = this.fboCache.values().iterator();

            while(var1.hasNext()) {
               int fbo = (Integer)var1.next();
               GlStateManager._glDeleteFramebuffers(fbo);
            }
         }
      }

   }

   public int getFbo(final DirectStateAccess dsa, final @Nullable GpuTexture depth) {
      int depthId = depth == null ? 0 : ((GlTexture)depth).id;
      if (this.firstFboDepthId == depthId) {
         return this.firstFboId;
      } else if (this.firstFboId == -1) {
         this.firstFboId = this.createFbo(dsa, depthId);
         this.firstFboDepthId = depthId;
         return this.firstFboId;
      } else {
         if (this.fboCache == null) {
            this.fboCache = new Int2IntArrayMap();
         }

         return this.fboCache.computeIfAbsent(depthId, (_depthId) -> this.createFbo(dsa, _depthId));
      }
   }

   private int createFbo(final DirectStateAccess dsa, final int depthid) {
      int fbo = dsa.createFrameBufferObject();
      dsa.bindFrameBufferTextures(fbo, this.texture().id, depthid, this.baseMipLevel(), 0);
      return fbo;
   }

   public GlTexture texture() {
      return (GlTexture)super.texture();
   }
}
