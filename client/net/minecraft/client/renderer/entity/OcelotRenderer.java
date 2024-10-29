package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.FelineRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Ocelot;

public class OcelotRenderer extends AgeableMobRenderer<Ocelot, FelineRenderState, OcelotModel> {
   private static final ResourceLocation CAT_OCELOT_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/cat/ocelot.png");

   public OcelotRenderer(EntityRendererProvider.Context var1) {
      super(var1, new OcelotModel(var1.bakeLayer(ModelLayers.OCELOT)), new OcelotModel(var1.bakeLayer(ModelLayers.OCELOT_BABY)), 0.4F);
   }

   public ResourceLocation getTextureLocation(FelineRenderState var1) {
      return CAT_OCELOT_LOCATION;
   }

   public FelineRenderState createRenderState() {
      return new FelineRenderState();
   }

   public void extractRenderState(Ocelot var1, FelineRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isCrouching = var1.isCrouching();
      var2.isSprinting = var1.isSprinting();
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((FelineRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
