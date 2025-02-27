package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.opengl.dsa.DirectStateAccess;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
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
   protected boolean modesDirty = true;

   protected GlTexture(String var1, TextureFormat var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5);
      this.id = var6;
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         GlStateManager._deleteTexture(this.id);
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
         var1.bindFrameBufferTextures(var4, this.id, var3, 0, false);
         return var4;
      });
   }

   public void flushModeChanges() {
      if (this.modesDirty) {
         GlStateManager._texParameter(3553, 10242, GlConst.toGl(this.addressModeU));
         GlStateManager._texParameter(3553, 10243, GlConst.toGl(this.addressModeV));
         switch (this.minFilter) {
            case NEAREST -> GlStateManager._texParameter(3553, 10241, this.useMipmaps ? 9986 : 9728);
            case LINEAR -> GlStateManager._texParameter(3553, 10241, this.useMipmaps ? 9987 : 9729);
         }

         switch (this.magFilter) {
            case NEAREST -> GlStateManager._texParameter(3553, 10240, 9728);
            case LINEAR -> GlStateManager._texParameter(3553, 10240, 9729);
         }

         this.modesDirty = false;
      }

   }

   public int glId() {
      return this.id;
   }

   public void setAddressMode(AddressMode var1, AddressMode var2) {
      super.setAddressMode(var1, var2);
      this.modesDirty = true;
   }

   public void setTextureFilter(FilterMode var1, FilterMode var2, boolean var3) {
      super.setTextureFilter(var1, var2, var3);
      this.modesDirty = true;
   }
}
