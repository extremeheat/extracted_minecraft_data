package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.BatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.BatRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ambient.Bat;

public class BatRenderer extends MobRenderer<Bat, BatRenderState, BatModel> {
   private static final ResourceLocation BAT_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/bat.png");

   public BatRenderer(EntityRendererProvider.Context var1) {
      super(var1, new BatModel(var1.bakeLayer(ModelLayers.BAT)), 0.25F);
   }

   public ResourceLocation getTextureLocation(BatRenderState var1) {
      return BAT_LOCATION;
   }

   public BatRenderState createRenderState() {
      return new BatRenderState();
   }

   public void extractRenderState(Bat var1, BatRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isResting = var1.isResting();
      var2.flyAnimationState.copyFrom(var1.flyAnimationState);
      var2.restAnimationState.copyFrom(var1.restAnimationState);
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((BatRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
