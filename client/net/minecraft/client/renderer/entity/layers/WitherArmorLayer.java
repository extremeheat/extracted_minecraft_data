package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.WitherBossModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.wither.WitherBoss;

public class WitherArmorLayer extends RenderLayer<WitherBoss, WitherBossModel<WitherBoss>> {
   private static final ResourceLocation WITHER_ARMOR_LOCATION = new ResourceLocation("textures/entity/wither/wither_armor.png");
   private final WitherBossModel<WitherBoss> model = new WitherBossModel(0.5F);

   public WitherArmorLayer(RenderLayerParent<WitherBoss, WitherBossModel<WitherBoss>> var1) {
      super(var1);
   }

   public void render(WitherBoss var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (var1.isPowered()) {
         GlStateManager.depthMask(!var1.isInvisible());
         this.bindTexture(WITHER_ARMOR_LOCATION);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         float var9 = (float)var1.tickCount + var4;
         float var10 = Mth.cos(var9 * 0.02F) * 3.0F;
         float var11 = var9 * 0.01F;
         GlStateManager.translatef(var10, var11, 0.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.enableBlend();
         float var12 = 0.5F;
         GlStateManager.color4f(0.5F, 0.5F, 0.5F, 1.0F);
         GlStateManager.disableLighting();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
         this.model.prepareMobModel(var1, var2, var3, var4);
         ((WitherBossModel)this.getParentModel()).copyPropertiesTo(this.model);
         GameRenderer var13 = Minecraft.getInstance().gameRenderer;
         var13.resetFogColor(true);
         this.model.render(var1, var2, var3, var5, var6, var7, var8);
         var13.resetFogColor(false);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         GlStateManager.matrixMode(5888);
         GlStateManager.enableLighting();
         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
      }
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
