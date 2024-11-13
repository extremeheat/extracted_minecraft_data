package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;

public abstract class AbstractTexture implements AutoCloseable {
   public static final int NOT_ASSIGNED = -1;
   protected int id = -1;
   protected boolean defaultBlur;

   public AbstractTexture() {
      super();
   }

   public void setFilter(boolean var1, boolean var2) {
      RenderSystem.assertOnRenderThreadOrInit();
      int var3;
      short var4;
      if (var1) {
         var3 = var2 ? 9987 : 9729;
         var4 = 9729;
      } else {
         var3 = var2 ? 9986 : 9728;
         var4 = 9728;
      }

      this.bind();
      GlStateManager._texParameter(3553, 10241, var3);
      GlStateManager._texParameter(3553, 10240, var4);
   }

   public int getId() {
      RenderSystem.assertOnRenderThreadOrInit();
      if (this.id == -1) {
         this.id = TextureUtil.generateTextureId();
      }

      return this.id;
   }

   public void releaseId() {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            if (this.id != -1) {
               TextureUtil.releaseTextureId(this.id);
               this.id = -1;
            }

         });
      } else if (this.id != -1) {
         TextureUtil.releaseTextureId(this.id);
         this.id = -1;
      }

   }

   public boolean getDefaultBlur() {
      return this.defaultBlur;
   }

   public void bind() {
      if (!RenderSystem.isOnRenderThreadOrInit()) {
         RenderSystem.recordRenderCall(() -> GlStateManager._bindTexture(this.getId()));
      } else {
         GlStateManager._bindTexture(this.getId());
      }

   }

   public void close() {
   }
}
