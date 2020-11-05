package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HoglinModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zoglin;

public class ZoglinRenderer extends MobRenderer<Zoglin, HoglinModel<Zoglin>> {
   private static final ResourceLocation ZOGLIN_LOCATION = new ResourceLocation("textures/entity/hoglin/zoglin.png");

   public ZoglinRenderer(EntityRenderDispatcher var1) {
      super(var1, new HoglinModel(), 0.7F);
   }

   public ResourceLocation getTextureLocation(Zoglin var1) {
      return ZOGLIN_LOCATION;
   }
}
