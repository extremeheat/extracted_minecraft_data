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

   public boolean isClosed() {
      return this.closed;
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
      var1.bindFrameBufferTextures(var3, this.id, var2, 0, 0);
      return var3;
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
