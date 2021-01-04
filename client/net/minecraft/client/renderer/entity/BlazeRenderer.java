package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.BlazeModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Blaze;

public class BlazeRenderer extends MobRenderer<Blaze, BlazeModel<Blaze>> {
   private static final ResourceLocation BLAZE_LOCATION = new ResourceLocation("textures/entity/blaze.png");

   public BlazeRenderer(EntityRenderDispatcher var1) {
      super(var1, new BlazeModel(), 0.5F);
   }

   protected ResourceLocation getTextureLocation(Blaze var1) {
      return BLAZE_LOCATION;
   }
}
