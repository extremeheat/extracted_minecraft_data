package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SkeletonClothingLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.skeleton.Stray;

public class StrayRenderer extends AbstractSkeletonRenderer<Stray, SkeletonRenderState> {
   private static final Identifier STRAY_SKELETON_LOCATION = Identifier.withDefaultNamespace("textures/entity/skeleton/stray.png");
   private static final Identifier STRAY_CLOTHES_LOCATION = Identifier.withDefaultNamespace("textures/entity/skeleton/stray_overlay.png");

   public StrayRenderer(EntityRendererProvider.Context var1) {
      super(var1, ModelLayers.STRAY, ModelLayers.STRAY_ARMOR);
      this.addLayer(new SkeletonClothingLayer(this, var1.getModelSet(), ModelLayers.STRAY_OUTER_LAYER, STRAY_CLOTHES_LOCATION));
   }

   public Identifier getTextureLocation(SkeletonRenderState var1) {
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
