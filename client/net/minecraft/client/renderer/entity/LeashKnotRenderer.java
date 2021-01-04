package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.LeashKnotModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;

public class LeashKnotRenderer extends EntityRenderer<LeashFenceKnotEntity> {
   private static final ResourceLocation KNOT_LOCATION = new ResourceLocation("textures/entity/lead_knot.png");
   private final LeashKnotModel<LeashFenceKnotEntity> model = new LeashKnotModel();

   public LeashKnotRenderer(EntityRenderDispatcher var1) {
      super(var1);
   }

   public void render(LeashFenceKnotEntity var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      GlStateManager.translatef((float)var2, (float)var4, (float)var6);
      float var10 = 0.0625F;
      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      GlStateManager.enableAlphaTest();
      this.bindTexture(var1);
      if (this.solidRender) {
         GlStateManager.enableColorMaterial();
         GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(var1));
      }

      this.model.render(var1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      if (this.solidRender) {
         GlStateManager.tearDownSolidRenderingTextureCombine();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      super.render(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(LeashFenceKnotEntity var1) {
      return KNOT_LOCATION;
   }
}
