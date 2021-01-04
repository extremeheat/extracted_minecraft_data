package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.EvokerFangsModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.EvokerFangs;

public class EvokerFangsRenderer extends EntityRenderer<EvokerFangs> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/illager/evoker_fangs.png");
   private final EvokerFangsModel<EvokerFangs> model = new EvokerFangsModel();

   public EvokerFangsRenderer(EntityRenderDispatcher var1) {
      super(var1);
   }

   public void render(EvokerFangs var1, double var2, double var4, double var6, float var8, float var9) {
      float var10 = var1.getAnimationProgress(var9);
      if (var10 != 0.0F) {
         float var11 = 2.0F;
         if (var10 > 0.9F) {
            var11 = (float)((double)var11 * ((1.0D - (double)var10) / 0.10000000149011612D));
         }

         GlStateManager.pushMatrix();
         GlStateManager.disableCull();
         GlStateManager.enableAlphaTest();
         this.bindTexture(var1);
         GlStateManager.translatef((float)var2, (float)var4, (float)var6);
         GlStateManager.rotatef(90.0F - var1.yRot, 0.0F, 1.0F, 0.0F);
         GlStateManager.scalef(-var11, -var11, var11);
         float var12 = 0.03125F;
         GlStateManager.translatef(0.0F, -0.626F, 0.0F);
         this.model.render(var1, var10, 0.0F, 0.0F, var1.yRot, var1.xRot, 0.03125F);
         GlStateManager.popMatrix();
         GlStateManager.enableCull();
         super.render(var1, var2, var4, var6, var8, var9);
      }
   }

   protected ResourceLocation getTextureLocation(EvokerFangs var1) {
      return TEXTURE_LOCATION;
   }
}
