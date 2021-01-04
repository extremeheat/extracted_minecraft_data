package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;

public abstract class AbstractTexture implements TextureObject {
   protected int id = -1;
   protected boolean blur;
   protected boolean mipmap;
   protected boolean oldBlur;
   protected boolean oldMipmap;

   public AbstractTexture() {
      super();
   }

   public void setFilter(boolean var1, boolean var2) {
      this.blur = var1;
      this.mipmap = var2;
      int var3;
      short var4;
      if (var1) {
         var3 = var2 ? 9987 : 9729;
         var4 = 9729;
      } else {
         var3 = var2 ? 9986 : 9728;
         var4 = 9728;
      }

      GlStateManager.texParameter(3553, 10241, var3);
      GlStateManager.texParameter(3553, 10240, var4);
   }

   public void pushFilter(boolean var1, boolean var2) {
      this.oldBlur = this.blur;
      this.oldMipmap = this.mipmap;
      this.setFilter(var1, var2);
   }

   public void popFilter() {
      this.setFilter(this.oldBlur, this.oldMipmap);
   }

   public int getId() {
      if (this.id == -1) {
         this.id = TextureUtil.generateTextureId();
      }

      return this.id;
   }

   public void releaseId() {
      if (this.id != -1) {
         TextureUtil.releaseTextureId(this.id);
         this.id = -1;
      }

   }
}
