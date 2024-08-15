package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.HoglinRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zoglin;

public class ZoglinRenderer extends AbstractHoglinRenderer<Zoglin> {
   private static final ResourceLocation ZOGLIN_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/hoglin/zoglin.png");

   public ZoglinRenderer(EntityRendererProvider.Context var1) {
      super(var1, ModelLayers.ZOGLIN, ModelLayers.ZOGLIN_BABY, 0.7F);
   }

   public ResourceLocation getTextureLocation(HoglinRenderState var1) {
      return ZOGLIN_LOCATION;
   }
}
