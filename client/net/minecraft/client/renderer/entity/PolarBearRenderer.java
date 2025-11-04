package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.PolarBearModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PolarBearRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.PolarBear;

public class PolarBearRenderer extends AgeableMobRenderer<PolarBear, PolarBearRenderState, PolarBearModel> {
   private static final Identifier BEAR_LOCATION = Identifier.withDefaultNamespace("textures/entity/bear/polarbear.png");

   public PolarBearRenderer(EntityRendererProvider.Context var1) {
      super(var1, new PolarBearModel(var1.bakeLayer(ModelLayers.POLAR_BEAR)), new PolarBearModel(var1.bakeLayer(ModelLayers.POLAR_BEAR_BABY)), 0.9F);
   }

   public Identifier getTextureLocation(PolarBearRenderState var1) {
      return BEAR_LOCATION;
   }

   public PolarBearRenderState createRenderState() {
      return new PolarBearRenderState();
   }

   public void extractRenderState(PolarBear var1, PolarBearRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.standScale = var1.getStandingAnimationScale(var3);
   }

   // $FF: synthetic method
   public Identifier getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((PolarBearRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
