package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.StrayClothingLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;

public class StrayRenderer extends SkeletonRenderer {
   private static final ResourceLocation STRAY_SKELETON_LOCATION = new ResourceLocation("textures/entity/skeleton/stray.png");

   public StrayRenderer(EntityRendererProvider.Context var1) {
      super(var1, ModelLayers.STRAY, ModelLayers.STRAY_INNER_ARMOR, ModelLayers.STRAY_OUTER_ARMOR);
      this.addLayer(new StrayClothingLayer(this, var1.getModelSet()));
   }

   public ResourceLocation getTextureLocation(AbstractSkeleton var1) {
      return STRAY_SKELETON_LOCATION;
   }
}
