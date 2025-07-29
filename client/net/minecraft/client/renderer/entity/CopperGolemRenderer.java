package net.minecraft.client.renderer.entity;

import java.util.function.Function;
import net.minecraft.client.model.CopperGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.LivingEntityEmissiveLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.CopperGolemRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.coppergolem.CopperGolem;
import net.minecraft.world.entity.animal.coppergolem.CopperGolemOxidationLevels;

public class CopperGolemRenderer extends MobRenderer<CopperGolem, CopperGolemRenderState, CopperGolemModel> {
   public CopperGolemRenderer(EntityRendererProvider.Context var1) {
      super(var1, new CopperGolemModel(var1.bakeLayer(ModelLayers.COPPER_GOLEM)), 0.5F);
      this.addLayer(new LivingEntityEmissiveLayer(this, getEyeTextureLocationProvider(), (var0, var1x) -> 1.0F, new CopperGolemModel(var1.bakeLayer(ModelLayers.COPPER_GOLEM)), RenderType::eyes, true));
      this.addLayer(new ItemInHandLayer(this));
   }

   public ResourceLocation getTextureLocation(CopperGolemRenderState var1) {
      return CopperGolemOxidationLevels.getOxidationLevel(var1.weathering).texture();
   }

   private static Function<CopperGolemRenderState, ResourceLocation> getEyeTextureLocationProvider() {
      return (var0) -> CopperGolemOxidationLevels.getOxidationLevel(var0.weathering).eyeTexture();
   }

   public CopperGolemRenderState createRenderState() {
      return new CopperGolemRenderState();
   }

   public void extractRenderState(CopperGolem var1, CopperGolemRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      ArmedEntityRenderState.extractArmedEntityRenderState(var1, var2, this.itemModelResolver);
      var2.weathering = var1.getWeatherState();
      var2.copperGolemState = var1.getState();
      var2.walkAnimationState.copyFrom(var1.getWalkAnimationState());
      var2.walkWithItemAnimationState.copyFrom(var1.getWalkWithItemAnimationState());
      var2.idleAnimationState.copyFrom(var1.getIdleAnimationState());
      var2.interactionGetItem.copyFrom(var1.getInteractionGetItemAnimationState());
      var2.interactionGetNoItem.copyFrom(var1.getInteractionGetNoItemAnimationState());
      var2.interactionDropItem.copyFrom(var1.getInteractionDropItemAnimationState());
      var2.interactionDropNoItem.copyFrom(var1.getInteractionDropNoItemAnimationState());
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((CopperGolemRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
