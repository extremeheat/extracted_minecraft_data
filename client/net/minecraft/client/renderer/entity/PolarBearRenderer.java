package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.PolarBearModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.PolarBear;

public class PolarBearRenderer extends MobRenderer<PolarBear, PolarBearModel<PolarBear>> {
   private static final ResourceLocation BEAR_LOCATION = new ResourceLocation("textures/entity/bear/polarbear.png");

   public PolarBearRenderer(EntityRenderDispatcher var1) {
      super(var1, new PolarBearModel(), 0.9F);
   }

   protected ResourceLocation getTextureLocation(PolarBear var1) {
      return BEAR_LOCATION;
   }

   protected void scale(PolarBear var1, float var2) {
      GlStateManager.scalef(1.2F, 1.2F, 1.2F);
      super.scale(var1, var2);
   }
}
