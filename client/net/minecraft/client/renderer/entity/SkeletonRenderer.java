package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;

public class SkeletonRenderer extends HumanoidMobRenderer<AbstractSkeleton, SkeletonModel<AbstractSkeleton>> {
   private static final ResourceLocation SKELETON_LOCATION = new ResourceLocation("textures/entity/skeleton/skeleton.png");

   public SkeletonRenderer(EntityRendererProvider.Context var1) {
      this(var1, ModelLayers.SKELETON, ModelLayers.SKELETON_INNER_ARMOR, ModelLayers.SKELETON_OUTER_ARMOR);
   }

   public SkeletonRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2, ModelLayerLocation var3, ModelLayerLocation var4) {
      super(var1, new SkeletonModel(var1.bakeLayer(var2)), 0.5F);
      this.addLayer(new HumanoidArmorLayer(this, new SkeletonModel(var1.bakeLayer(var3)), new SkeletonModel(var1.bakeLayer(var4))));
   }

   public ResourceLocation getTextureLocation(AbstractSkeleton var1) {
      return SKELETON_LOCATION;
   }

   protected boolean isShaking(AbstractSkeleton var1) {
      return var1.isShaking();
   }

   // $FF: synthetic method
   protected boolean isShaking(LivingEntity var1) {
      return this.isShaking((AbstractSkeleton)var1);
   }
}
