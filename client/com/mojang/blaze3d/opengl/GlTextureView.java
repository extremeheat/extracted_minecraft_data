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

   protected GlTextureView(GlTexture var1, int var2, int var3) {
      super(var1, var2, var3);
      var1.addViews();
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
               int var2 = (Integer)var1.next();
               GlStateManager._glDeleteFramebuffers(var2);
            }
         }
      }

   }

   public int getFbo(DirectStateAccess var1, @Nullable GpuTexture var2) {
      int var3 = var2 == null ? 0 : ((GlTexture)var2).id;
      if (this.firstFboDepthId == var3) {
         return this.firstFboId;
      } else if (this.firstFboId == -1) {
         this.firstFboId = this.createFbo(var1, var3);
         this.firstFboDepthId = var3;
         return this.firstFboId;
      } else {
         if (this.fboCache == null) {
            this.fboCache = new Int2IntArrayMap();
         }

         return this.fboCache.computeIfAbsent(var3, (var2x) -> this.createFbo(var1, var2x));
      }
   }

   private int createFbo(DirectStateAccess var1, int var2) {
      int var3 = var1.createFrameBufferObject();
      var1.bindFrameBufferTextures(var3, this.texture().id, var2, this.baseMipLevel(), 0);
      return var3;
   }

   public GlTexture texture() {
      return (GlTexture)super.texture();
   }

   // $FF: synthetic method
   public GpuTexture texture() {
      return this.texture();
   }
}
