package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HoglinModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zoglin;

public class ZoglinRenderer extends MobRenderer<Zoglin, HoglinModel<Zoglin>> {
   private static final ResourceLocation ZOGLIN_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/hoglin/zoglin.png");

   public ZoglinRenderer(EntityRendererProvider.Context var1) {
      super(var1, new HoglinModel(var1.bakeLayer(ModelLayers.ZOGLIN)), 0.7F);
   }

   public ResourceLocation getTextureLocation(Zoglin var1) {
      return ZOGLIN_LOCATION;
   }
}
