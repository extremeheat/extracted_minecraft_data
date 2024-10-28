package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SkeletonClothingLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Stray;

public class StrayRenderer extends AbstractSkeletonRenderer<Stray, SkeletonRenderState> {
   private static final ResourceLocation STRAY_SKELETON_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/skeleton/stray.png");
   private static final ResourceLocation STRAY_CLOTHES_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/skeleton/stray_overlay.png");

   public StrayRenderer(EntityRendererProvider.Context var1) {
      super(var1, ModelLayers.STRAY, ModelLayers.STRAY_INNER_ARMOR, ModelLayers.STRAY_OUTER_ARMOR);
      this.addLayer(new SkeletonClothingLayer(this, var1.getModelSet(), ModelLayers.STRAY_OUTER_LAYER, STRAY_CLOTHES_LOCATION));
   }

   public ResourceLocation getTextureLocation(SkeletonRenderState var1) {
      return STRAY_SKELETON_LOCATION;
   }

   public SkeletonRenderState createRenderState() {
      return new SkeletonRenderState();
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
