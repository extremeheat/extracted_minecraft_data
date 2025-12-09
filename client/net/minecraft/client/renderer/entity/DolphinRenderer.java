package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.animal.dolphin.DolphinModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.DolphinCarryingItemLayer;
import net.minecraft.client.renderer.entity.state.DolphinRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.dolphin.Dolphin;

public class DolphinRenderer extends AgeableMobRenderer<Dolphin, DolphinRenderState, DolphinModel> {
   private static final Identifier DOLPHIN_LOCATION = Identifier.withDefaultNamespace("textures/entity/dolphin.png");

   public DolphinRenderer(EntityRendererProvider.Context var1) {
      super(var1, new DolphinModel(var1.bakeLayer(ModelLayers.DOLPHIN)), new DolphinModel(var1.bakeLayer(ModelLayers.DOLPHIN_BABY)), 0.7F);
      this.addLayer(new DolphinCarryingItemLayer(this));
   }

   public Identifier getTextureLocation(DolphinRenderState var1) {
      return DOLPHIN_LOCATION;
   }

   public DolphinRenderState createRenderState() {
      return new DolphinRenderState();
   }

   public void extractRenderState(Dolphin var1, DolphinRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      HoldingEntityRenderState.extractHoldingEntityRenderState(var1, var2, this.itemModelResolver);
      var2.isMoving = var1.getDeltaMovement().horizontalDistanceSqr() > 1.0E-7;
   }

   // $FF: synthetic method
   public Identifier getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((DolphinRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
