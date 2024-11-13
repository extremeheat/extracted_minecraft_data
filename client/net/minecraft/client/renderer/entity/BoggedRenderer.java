package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.BoggedModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SkeletonClothingLayer;
import net.minecraft.client.renderer.entity.state.BoggedRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Bogged;

public class BoggedRenderer extends AbstractSkeletonRenderer<Bogged, BoggedRenderState> {
   private static final ResourceLocation BOGGED_SKELETON_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/skeleton/bogged.png");
   private static final ResourceLocation BOGGED_OUTER_LAYER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/skeleton/bogged_overlay.png");

   public BoggedRenderer(EntityRendererProvider.Context var1) {
      super(var1, ModelLayers.BOGGED_INNER_ARMOR, ModelLayers.BOGGED_OUTER_ARMOR, (SkeletonModel)(new BoggedModel(var1.bakeLayer(ModelLayers.BOGGED))));
      this.addLayer(new SkeletonClothingLayer(this, var1.getModelSet(), ModelLayers.BOGGED_OUTER_LAYER, BOGGED_OUTER_LAYER_LOCATION));
   }

   public ResourceLocation getTextureLocation(BoggedRenderState var1) {
      return BOGGED_SKELETON_LOCATION;
   }

   public BoggedRenderState createRenderState() {
      return new BoggedRenderState();
   }

   public void extractRenderState(Bogged var1, BoggedRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isSheared = var1.isSheared();
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((BoggedRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
