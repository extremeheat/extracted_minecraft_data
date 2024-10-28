package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.WardenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.LivingEntityEmissiveLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.WardenRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.warden.Warden;

public class WardenRenderer extends MobRenderer<Warden, WardenRenderState, WardenModel> {
   private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/warden/warden.png");
   private static final ResourceLocation BIOLUMINESCENT_LAYER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/warden/warden_bioluminescent_layer.png");
   private static final ResourceLocation HEART_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/warden/warden_heart.png");
   private static final ResourceLocation PULSATING_SPOTS_TEXTURE_1 = ResourceLocation.withDefaultNamespace("textures/entity/warden/warden_pulsating_spots_1.png");
   private static final ResourceLocation PULSATING_SPOTS_TEXTURE_2 = ResourceLocation.withDefaultNamespace("textures/entity/warden/warden_pulsating_spots_2.png");

   public WardenRenderer(EntityRendererProvider.Context var1) {
      super(var1, new WardenModel(var1.bakeLayer(ModelLayers.WARDEN)), 0.9F);
      this.addLayer(new LivingEntityEmissiveLayer(this, BIOLUMINESCENT_LAYER_TEXTURE, (var0, var1x) -> {
         return 1.0F;
      }, WardenModel::getBioluminescentLayerModelParts, RenderType::entityTranslucentEmissive));
      this.addLayer(new LivingEntityEmissiveLayer(this, PULSATING_SPOTS_TEXTURE_1, (var0, var1x) -> {
         return Math.max(0.0F, Mth.cos(var1x * 0.045F) * 0.25F);
      }, WardenModel::getPulsatingSpotsLayerModelParts, RenderType::entityTranslucentEmissive));
      this.addLayer(new LivingEntityEmissiveLayer(this, PULSATING_SPOTS_TEXTURE_2, (var0, var1x) -> {
         return Math.max(0.0F, Mth.cos(var1x * 0.045F + 3.1415927F) * 0.25F);
      }, WardenModel::getPulsatingSpotsLayerModelParts, RenderType::entityTranslucentEmissive));
      this.addLayer(new LivingEntityEmissiveLayer(this, TEXTURE, (var0, var1x) -> {
         return var0.tendrilAnimation;
      }, WardenModel::getTendrilsLayerModelParts, RenderType::entityTranslucentEmissive));
      this.addLayer(new LivingEntityEmissiveLayer(this, HEART_TEXTURE, (var0, var1x) -> {
         return var0.heartAnimation;
      }, WardenModel::getHeartLayerModelParts, RenderType::entityTranslucentEmissive));
   }

   public ResourceLocation getTextureLocation(WardenRenderState var1) {
      return TEXTURE;
   }

   public WardenRenderState createRenderState() {
      return new WardenRenderState();
   }

   public void extractRenderState(Warden var1, WardenRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.tendrilAnimation = var1.getTendrilAnimation(var3);
      var2.heartAnimation = var1.getHeartAnimation(var3);
      var2.roarAnimationState.copyFrom(var1.roarAnimationState);
      var2.sniffAnimationState.copyFrom(var1.sniffAnimationState);
      var2.emergeAnimationState.copyFrom(var1.emergeAnimationState);
      var2.diggingAnimationState.copyFrom(var1.diggingAnimationState);
      var2.attackAnimationState.copyFrom(var1.attackAnimationState);
      var2.sonicBoomAnimationState.copyFrom(var1.sonicBoomAnimationState);
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((WardenRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
