package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.GoatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.goat.Goat;

public class GoatRenderer extends MobRenderer<Goat, GoatModel<Goat>> {
   private static final ResourceLocation GOAT_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/goat/goat.png");

   public GoatRenderer(EntityRendererProvider.Context var1) {
      super(var1, new GoatModel(var1.bakeLayer(ModelLayers.GOAT)), 0.7F);
   }

   public ResourceLocation getTextureLocation(Goat var1) {
      return GOAT_LOCATION;
   }
}
