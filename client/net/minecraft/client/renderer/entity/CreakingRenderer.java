package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.CreakingModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.LivingEntityEmissiveLayer;
import net.minecraft.client.renderer.entity.state.CreakingRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.creaking.Creaking;

public class CreakingRenderer<T extends Creaking> extends MobRenderer<T, CreakingRenderState, CreakingModel> {
   private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/creaking/creaking.png");
   private static final ResourceLocation EYES_TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/creaking/creaking_eyes.png");

   public CreakingRenderer(EntityRendererProvider.Context var1) {
      super(var1, new CreakingModel(var1.bakeLayer(ModelLayers.CREAKING)), 0.7F);
      this.addLayer(new LivingEntityEmissiveLayer<>(this, EYES_TEXTURE_LOCATION, (var0, var1x) -> 1.0F, CreakingModel::getHeadModelParts, RenderType::eyes));
   }

   public ResourceLocation getTextureLocation(CreakingRenderState var1) {
      return TEXTURE_LOCATION;
   }

   public CreakingRenderState createRenderState() {
      return new CreakingRenderState();
   }

   public void extractRenderState(T var1, CreakingRenderState var2, float var3) {
      super.extractRenderState((T)var1, var2, var3);
      var2.attackAnimationState.copyFrom(var1.attackAnimationState);
      var2.invulnerabilityAnimationState.copyFrom(var1.invulnerabilityAnimationState);
      var2.isActive = var1.isActive();
      var2.canMove = var1.canMove();
   }
}
