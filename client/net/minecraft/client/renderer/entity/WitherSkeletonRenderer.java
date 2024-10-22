package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.WitherSkeleton;

public class WitherSkeletonRenderer extends AbstractSkeletonRenderer<WitherSkeleton, SkeletonRenderState> {
   private static final ResourceLocation WITHER_SKELETON_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/skeleton/wither_skeleton.png");

   public WitherSkeletonRenderer(EntityRendererProvider.Context var1) {
      super(var1, ModelLayers.WITHER_SKELETON, ModelLayers.WITHER_SKELETON_INNER_ARMOR, ModelLayers.WITHER_SKELETON_OUTER_ARMOR);
   }

   public ResourceLocation getTextureLocation(SkeletonRenderState var1) {
      return WITHER_SKELETON_LOCATION;
   }

   public SkeletonRenderState createRenderState() {
      return new SkeletonRenderState();
   }
}
