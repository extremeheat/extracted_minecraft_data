package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.concurrent.Executor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public abstract class AbstractTexture implements AutoCloseable {
   public static final int NOT_ASSIGNED = -1;
   protected int id = -1;
   protected boolean blur;
   protected boolean mipmap;

   public AbstractTexture() {
      super();
   }

   public void setFilter(boolean var1, boolean var2) {
      RenderSystem.assertOnRenderThreadOrInit();
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

   public abstract void load(ResourceManager var1) throws IOException;

   public void bind() {
      if (!RenderSystem.isOnRenderThreadOrInit()) {
         RenderSystem.recordRenderCall(() -> {
            GlStateManager._bindTexture(this.getId());
         });
      } else {
         GlStateManager._bindTexture(this.getId());
      }

   }

   public void reset(TextureManager var1, ResourceManager var2, ResourceLocation var3, Executor var4) {
      var1.register(var3, this);
   }

   public void close() {
   }
}
