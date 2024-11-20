package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.TriState;

public abstract class AbstractTexture implements AutoCloseable {
   public static final int NOT_ASSIGNED = -1;
   protected int id = -1;
   protected boolean defaultBlur;
   private int wrapS = 10497;
   private int wrapT = 10497;
   private int minFilter = 9986;
   private int magFilter = 9729;

   public AbstractTexture() {
      super();
   }

   public void setClamp(boolean var1) {
      RenderSystem.assertOnRenderThreadOrInit();
      char var2;
      char var3;
      if (var1) {
         var2 = '\u812f';
         var3 = '\u812f';
      } else {
         var2 = 10497;
         var3 = 10497;
      }

      boolean var4 = this.wrapS != var2;
      boolean var5 = this.wrapT != var3;
      if (var4 || var5) {
         this.bind();
         if (var4) {
            GlStateManager._texParameter(3553, 10242, var2);
            this.wrapS = var2;
         }

         if (var5) {
            GlStateManager._texParameter(3553, 10243, var3);
            this.wrapT = var3;
         }
      }

   }

   public void setFilter(TriState var1, boolean var2) {
      this.setFilter(var1.toBoolean(this.defaultBlur), var2);
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

      boolean var5 = this.minFilter != var3;
      boolean var6 = this.magFilter != var4;
      if (var6 || var5) {
         this.bind();
         if (var5) {
            GlStateManager._texParameter(3553, 10241, var3);
            this.minFilter = var3;
         }

         if (var6) {
            GlStateManager._texParameter(3553, 10240, var4);
            this.magFilter = var4;
         }
      }

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
