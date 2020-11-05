package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.StrayClothingLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;

public class StrayRenderer extends SkeletonRenderer {
   private static final ResourceLocation STRAY_SKELETON_LOCATION = new ResourceLocation("textures/entity/skeleton/stray.png");

   public StrayRenderer(EntityRenderDispatcher var1) {
      super(var1);
      this.addLayer(new StrayClothingLayer(this));
   }

   public ResourceLocation getTextureLocation(AbstractSkeleton var1) {
      return STRAY_SKELETON_LOCATION;
   }
}
