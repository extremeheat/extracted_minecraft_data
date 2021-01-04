package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class EnderEyesLayer<T extends LivingEntity> extends RenderLayer<T, EndermanModel<T>> {
   private static final ResourceLocation ENDERMAN_EYES_LOCATION = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");

   public EnderEyesLayer(RenderLayerParent<T, EndermanModel<T>> var1) {
      super(var1);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.bindTexture(ENDERMAN_EYES_LOCATION);
      GlStateManager.enableBlend();
      GlStateManager.disableAlphaTest();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
      GlStateManager.disableLighting();
      GlStateManager.depthMask(!var1.isInvisible());
      char var9 = '\uf0f0';
      char var10 = '\uf0f0';
      boolean var11 = false;
      GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 61680.0F, 0.0F);
      GlStateManager.enableLighting();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GameRenderer var12 = Minecraft.getInstance().gameRenderer;
      var12.resetFogColor(true);
      ((EndermanModel)this.getParentModel()).render(var1, var2, var3, var5, var6, var7, var8);
      var12.resetFogColor(false);
      this.setLightColor(var1);
      GlStateManager.depthMask(true);
      GlStateManager.disableBlend();
      GlStateManager.enableAlphaTest();
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
