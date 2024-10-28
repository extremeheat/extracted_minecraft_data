package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.FrogModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.frog.Frog;

public class FrogRenderer extends MobRenderer<Frog, FrogModel<Frog>> {
   public FrogRenderer(EntityRendererProvider.Context var1) {
      super(var1, new FrogModel(var1.bakeLayer(ModelLayers.FROG)), 0.3F);
   }

   public ResourceLocation getTextureLocation(Frog var1) {
      return ((FrogVariant)var1.getVariant().value()).texture();
   }
}
