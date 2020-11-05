package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;

public class WitherSkeletonRenderer extends SkeletonRenderer {
   private static final ResourceLocation WITHER_SKELETON_LOCATION = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");

   public WitherSkeletonRenderer(EntityRendererProvider.Context var1) {
      super(var1, ModelLayers.WITHER_SKELETON, ModelLayers.WITHER_SKELETON_INNER_ARMOR, ModelLayers.WITHER_SKELETON_OUTER_ARMOR);
   }

   public ResourceLocation getTextureLocation(AbstractSkeleton var1) {
      return WITHER_SKELETON_LOCATION;
   }

   protected void scale(AbstractSkeleton var1, PoseStack var2, float var3) {
      var2.scale(1.2F, 1.2F, 1.2F);
   }
}
