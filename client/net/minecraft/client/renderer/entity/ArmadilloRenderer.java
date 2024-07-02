package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ArmadilloModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.armadillo.Armadillo;

public class ArmadilloRenderer extends MobRenderer<Armadillo, ArmadilloModel> {
   private static final ResourceLocation ARMADILLO_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/armadillo.png");

   public ArmadilloRenderer(EntityRendererProvider.Context var1) {
      super(var1, new ArmadilloModel(var1.bakeLayer(ModelLayers.ARMADILLO)), 0.4F);
   }

   public ResourceLocation getTextureLocation(Armadillo var1) {
      return ARMADILLO_LOCATION;
   }
}
