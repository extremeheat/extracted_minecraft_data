package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import javax.annotation.Nullable;

public class GlTexture extends GpuTexture {
   protected final int id;
   private final Int2IntMap fboCache = new Int2IntOpenHashMap();
   protected boolean closed;
   private int views;

   protected GlTexture(@GpuTexture.Usage int var1, String var2, TextureFormat var3, int var4, int var5, int var6, int var7, int var8) {
      super(var1, var2, var3, var4, var5, var6, var7);
      this.id = var8;
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
      IntIterator var1 = this.fboCache.values().iterator();

      while(var1.hasNext()) {
         int var2 = (Integer)var1.next();
         GlStateManager._glDeleteFramebuffers(var2);
      }

   }

   public boolean isClosed() {
      return this.closed;
   }

   public int getFbo(DirectStateAccess var1, @Nullable GpuTexture var2) {
      int var3 = var2 == null ? 0 : ((GlTexture)var2).id;
      return this.fboCache.computeIfAbsent(var3, (var3x) -> {
         int var4 = var1.createFrameBufferObject();
         var1.bindFrameBufferTextures(var4, this.id, var3, 0, 0);
         return var4;
      });
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
