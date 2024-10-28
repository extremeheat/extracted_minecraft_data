package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.CamelModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.camel.Camel;

public class CamelRenderer extends MobRenderer<Camel, CamelModel<Camel>> {
   private static final ResourceLocation CAMEL_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/camel/camel.png");

   public CamelRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2) {
      super(var1, new CamelModel(var1.bakeLayer(var2)), 0.7F);
   }

   public ResourceLocation getTextureLocation(Camel var1) {
      return CAMEL_LOCATION;
   }
}
