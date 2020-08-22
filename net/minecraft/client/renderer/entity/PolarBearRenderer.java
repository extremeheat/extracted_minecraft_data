package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PolarBearModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.PolarBear;

public class PolarBearRenderer extends MobRenderer {
   private static final ResourceLocation BEAR_LOCATION = new ResourceLocation("textures/entity/bear/polarbear.png");

   public PolarBearRenderer(EntityRenderDispatcher var1) {
      super(var1, new PolarBearModel(), 0.9F);
   }

   public ResourceLocation getTextureLocation(PolarBear var1) {
      return BEAR_LOCATION;
   }

   protected void scale(PolarBear var1, PoseStack var2, float var3) {
      var2.scale(1.2F, 1.2F, 1.2F);
      super.scale(var1, var2, var3);
   }
}
