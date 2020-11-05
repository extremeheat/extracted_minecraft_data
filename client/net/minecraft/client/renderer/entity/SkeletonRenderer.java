package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;

public class SkeletonRenderer extends HumanoidMobRenderer<AbstractSkeleton, SkeletonModel<AbstractSkeleton>> {
   private static final ResourceLocation SKELETON_LOCATION = new ResourceLocation("textures/entity/skeleton/skeleton.png");

   public SkeletonRenderer(EntityRenderDispatcher var1) {
      super(var1, new SkeletonModel(), 0.5F);
      this.addLayer(new HumanoidArmorLayer(this, new SkeletonModel(0.5F, true), new SkeletonModel(1.0F, true)));
   }

   public ResourceLocation getTextureLocation(AbstractSkeleton var1) {
      return SKELETON_LOCATION;
   }
}
