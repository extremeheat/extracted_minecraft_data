package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.dragon.DragonModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

public class EnderDragonEyesLayer extends RenderLayer<EnderDragon, DragonModel> {
   private static final ResourceLocation DRAGON_EYES_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png");

   public EnderDragonEyesLayer(RenderLayerParent<EnderDragon, DragonModel> var1) {
      super(var1);
   }

   public void render(EnderDragon var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.bindTexture(DRAGON_EYES_LOCATION);
      GlStateManager.enableBlend();
      GlStateManager.disableAlphaTest();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
      GlStateManager.disableLighting();
      GlStateManager.depthFunc(514);
      char var9 = '\uf0f0';
      char var10 = '\uf0f0';
      boolean var11 = false;
      GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 61680.0F, 0.0F);
      GlStateManager.enableLighting();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GameRenderer var12 = Minecraft.getInstance().gameRenderer;
      var12.resetFogColor(true);
      ((DragonModel)this.getParentModel()).render(var1, var2, var3, var5, var6, var7, var8);
      var12.resetFogColor(false);
      this.setLightColor(var1);
      GlStateManager.disableBlend();
      GlStateManager.enableAlphaTest();
      GlStateManager.depthFunc(515);
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
