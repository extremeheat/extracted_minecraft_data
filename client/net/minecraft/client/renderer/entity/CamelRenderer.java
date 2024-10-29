package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.CamelModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.CamelRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.camel.Camel;

public class CamelRenderer extends AgeableMobRenderer<Camel, CamelRenderState, CamelModel> {
   private static final ResourceLocation CAMEL_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/camel/camel.png");

   public CamelRenderer(EntityRendererProvider.Context var1) {
      super(var1, new CamelModel(var1.bakeLayer(ModelLayers.CAMEL)), new CamelModel(var1.bakeLayer(ModelLayers.CAMEL_BABY)), 0.7F);
   }

   public ResourceLocation getTextureLocation(CamelRenderState var1) {
      return CAMEL_LOCATION;
   }

   public CamelRenderState createRenderState() {
      return new CamelRenderState();
   }

   public void extractRenderState(Camel var1, CamelRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isSaddled = var1.isSaddled();
      var2.isRidden = var1.isVehicle();
      var2.jumpCooldown = Math.max((float)var1.getJumpCooldown() - var3, 0.0F);
      var2.sitAnimationState.copyFrom(var1.sitAnimationState);
      var2.sitPoseAnimationState.copyFrom(var1.sitPoseAnimationState);
      var2.sitUpAnimationState.copyFrom(var1.sitUpAnimationState);
      var2.idleAnimationState.copyFrom(var1.idleAnimationState);
      var2.dashAnimationState.copyFrom(var1.dashAnimationState);
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((CamelRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
