package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.CreakingModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.LivingEntityEmissiveLayer;
import net.minecraft.client.renderer.entity.state.CreakingRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.monster.creaking.CreakingTransient;

public class CreakingRenderer<T extends Creaking> extends MobRenderer<T, CreakingRenderState, CreakingModel> {
   private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/creaking/creaking.png");
   private static final ResourceLocation EYES_TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/creaking/creaking_eyes.png");

   public CreakingRenderer(EntityRendererProvider.Context var1) {
      super(var1, new CreakingModel(var1.bakeLayer(ModelLayers.CREAKING)), 0.7F);
      this.addLayer(new LivingEntityEmissiveLayer(this, EYES_TEXTURE_LOCATION, (var0, var1x) -> {
         return 1.0F;
      }, CreakingModel::getHeadModelParts, RenderType::eyes, true));
   }

   public ResourceLocation getTextureLocation(CreakingRenderState var1) {
      return TEXTURE_LOCATION;
   }

   public CreakingRenderState createRenderState() {
      return new CreakingRenderState();
   }

   public void extractRenderState(T var1, CreakingRenderState var2, float var3) {
      label12: {
         super.extractRenderState(var1, var2, var3);
         var2.attackAnimationState.copyFrom(var1.attackAnimationState);
         var2.invulnerabilityAnimationState.copyFrom(var1.invulnerabilityAnimationState);
         var2.deathAnimationState.copyFrom(var1.deathAnimationState);
         if (var1 instanceof CreakingTransient var4) {
            if (var1.deathAnimationState.isStarted()) {
               var2.deathTime = 0.0F;
               var2.hasRedOverlay = false;
               var2.eyesGlowing = var4.hasGlowingEyes();
               break label12;
            }
         }

         var2.eyesGlowing = var1.isActive();
      }

      var2.canMove = var1.canMove();
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((CreakingRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
