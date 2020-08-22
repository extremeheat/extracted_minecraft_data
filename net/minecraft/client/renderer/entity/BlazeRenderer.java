package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.BlazeModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Blaze;

public class BlazeRenderer extends MobRenderer {
   private static final ResourceLocation BLAZE_LOCATION = new ResourceLocation("textures/entity/blaze.png");

   public BlazeRenderer(EntityRenderDispatcher var1) {
      super(var1, new BlazeModel(), 0.5F);
   }

   protected int getBlockLightLevel(Blaze var1, float var2) {
      return 15;
   }

   public ResourceLocation getTextureLocation(Blaze var1) {
      return BLAZE_LOCATION;
   }
}
