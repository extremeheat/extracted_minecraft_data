package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import org.jspecify.annotations.Nullable;

public class GlTextureView extends GpuTextureView {
   private boolean closed;
   private final Int2IntMap fboCache = new Int2IntOpenHashMap();

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
         IntIterator var1 = this.fboCache.values().iterator();

         while(var1.hasNext()) {
            int var2 = (Integer)var1.next();
            GlStateManager._glDeleteFramebuffers(var2);
         }
      }

   }

   public int getFbo(DirectStateAccess var1, @Nullable GpuTexture var2) {
      int var3 = var2 == null ? 0 : ((GlTexture)var2).id;
      return this.fboCache.computeIfAbsent(var3, (var3x) -> {
         int var4 = var1.createFrameBufferObject();
         var1.bindFrameBufferTextures(var4, this.texture().id, var3, this.baseMipLevel(), 0);
         return var4;
      });
   }

   public GlTexture texture() {
      return (GlTexture)super.texture();
   }

   // $FF: synthetic method
   public GpuTexture texture() {
      return this.texture();
   }
}
