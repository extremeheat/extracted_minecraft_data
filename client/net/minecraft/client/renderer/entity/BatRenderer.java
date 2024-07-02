package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.BatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ambient.Bat;

public class BatRenderer extends MobRenderer<Bat, BatModel> {
   private static final ResourceLocation BAT_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/bat.png");

   public BatRenderer(EntityRendererProvider.Context var1) {
      super(var1, new BatModel(var1.bakeLayer(ModelLayers.BAT)), 0.25F);
   }

   public ResourceLocation getTextureLocation(Bat var1) {
      return BAT_LOCATION;
   }
}
