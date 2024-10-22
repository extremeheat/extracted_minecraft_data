package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.FrogModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.FrogRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.frog.Frog;

public class FrogRenderer extends MobRenderer<Frog, FrogRenderState, FrogModel> {
   public FrogRenderer(EntityRendererProvider.Context var1) {
      super(var1, new FrogModel(var1.bakeLayer(ModelLayers.FROG)), 0.3F);
   }

   public ResourceLocation getTextureLocation(FrogRenderState var1) {
      return var1.texture;
   }

   public FrogRenderState createRenderState() {
      return new FrogRenderState();
   }

   public void extractRenderState(Frog var1, FrogRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isSwimming = var1.isInWaterOrBubble();
      var2.jumpAnimationState.copyFrom(var1.jumpAnimationState);
      var2.croakAnimationState.copyFrom(var1.croakAnimationState);
      var2.tongueAnimationState.copyFrom(var1.tongueAnimationState);
      var2.swimIdleAnimationState.copyFrom(var1.swimIdleAnimationState);
      var2.texture = var1.getVariant().value().texture();
   }
}
