package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import org.jspecify.annotations.Nullable;

public class GlTexture extends GpuTexture {
   private static final int EMPTY = -1;
   protected final int id;
   private int firstFboId = -1;
   private int firstFboDepthId = -1;
   private @Nullable Int2IntMap fboCache;
   protected boolean closed;
   private int views;

   protected GlTexture(final @GpuTexture.Usage int usage, final String label, final TextureFormat format, final int width, final int height, final int depthOrLayers, final int mipLevels, final int id) {
      super(usage, label, format, width, height, depthOrLayers, mipLevels);
      this.id = id;
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
      GlStateManager._deleteTexture(this.id);
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

   public boolean isClosed() {
      return this.closed;
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
      dsa.bindFrameBufferTextures(fbo, this.id, depthid, 0, 0);
      return fbo;
   }

   public int glId() {
      return this.id;
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
