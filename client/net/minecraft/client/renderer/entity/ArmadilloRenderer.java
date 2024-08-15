package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ArmadilloModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.ArmadilloRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.armadillo.Armadillo;

public class ArmadilloRenderer extends AgeableMobRenderer<Armadillo, ArmadilloRenderState, ArmadilloModel> {
   private static final ResourceLocation ARMADILLO_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/armadillo.png");

   public ArmadilloRenderer(EntityRendererProvider.Context var1) {
      super(var1, new ArmadilloModel(var1.bakeLayer(ModelLayers.ARMADILLO)), new ArmadilloModel(var1.bakeLayer(ModelLayers.ARMADILLO_BABY)), 0.4F);
   }

   public ResourceLocation getTextureLocation(ArmadilloRenderState var1) {
      return ARMADILLO_LOCATION;
   }

   public ArmadilloRenderState createRenderState() {
      return new ArmadilloRenderState();
   }

   public void extractRenderState(Armadillo var1, ArmadilloRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isHidingInShell = var1.shouldHideInShell();
      var2.peekAnimationState.copyFrom(var1.peekAnimationState);
      var2.rollOutAnimationState.copyFrom(var1.rollOutAnimationState);
      var2.rollUpAnimationState.copyFrom(var1.rollUpAnimationState);
   }
}
