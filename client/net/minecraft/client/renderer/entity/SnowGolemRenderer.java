package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SnowGolemHeadLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.SnowGolemRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.SnowGolem;

public class SnowGolemRenderer extends MobRenderer<SnowGolem, SnowGolemRenderState, SnowGolemModel> {
   private static final ResourceLocation SNOW_GOLEM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/snow_golem.png");

   public SnowGolemRenderer(EntityRendererProvider.Context var1) {
      super(var1, new SnowGolemModel(var1.bakeLayer(ModelLayers.SNOW_GOLEM)), 0.5F);
      this.addLayer(new SnowGolemHeadLayer(this, var1.getBlockRenderDispatcher()));
   }

   public ResourceLocation getTextureLocation(SnowGolemRenderState var1) {
      return SNOW_GOLEM_LOCATION;
   }

   public SnowGolemRenderState createRenderState() {
      return new SnowGolemRenderState();
   }

   public void extractRenderState(SnowGolem var1, SnowGolemRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.hasPumpkin = var1.hasPumpkin();
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((SnowGolemRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
