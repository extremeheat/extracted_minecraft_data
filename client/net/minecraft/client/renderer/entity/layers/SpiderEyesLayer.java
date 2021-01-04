package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class SpiderEyesLayer<T extends Entity, M extends SpiderModel<T>> extends RenderLayer<T, M> {
   private static final ResourceLocation SPIDER_EYES_LOCATION = new ResourceLocation("textures/entity/spider_eyes.png");

   public SpiderEyesLayer(RenderLayerParent<T, M> var1) {
      super(var1);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.bindTexture(SPIDER_EYES_LOCATION);
      GlStateManager.enableBlend();
      GlStateManager.disableAlphaTest();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
      if (var1.isInvisible()) {
         GlStateManager.depthMask(false);
      } else {
         GlStateManager.depthMask(true);
      }

      char var9 = '\uf0f0';
      int var10 = var9 % 65536;
      int var11 = var9 / 65536;
      GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)var10, (float)var11);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GameRenderer var12 = Minecraft.getInstance().gameRenderer;
      var12.resetFogColor(true);
      ((SpiderModel)this.getParentModel()).render(var1, var2, var3, var5, var6, var7, var8);
      var12.resetFogColor(false);
      int var13 = var1.getLightColor();
      var10 = var13 % 65536;
      var11 = var13 / 65536;
      GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)var10, (float)var11);
      this.setLightColor(var1);
      GlStateManager.depthMask(true);
      GlStateManager.disableBlend();
      GlStateManager.enableAlphaTest();
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
