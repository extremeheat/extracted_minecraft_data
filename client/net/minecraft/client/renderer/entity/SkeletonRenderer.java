package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;

public class SkeletonRenderer<T extends AbstractSkeleton> extends HumanoidMobRenderer<T, SkeletonModel<T>> {
   private static final ResourceLocation SKELETON_LOCATION = new ResourceLocation("textures/entity/skeleton/skeleton.png");

   public SkeletonRenderer(EntityRendererProvider.Context var1) {
      this(var1, ModelLayers.SKELETON, ModelLayers.SKELETON_INNER_ARMOR, ModelLayers.SKELETON_OUTER_ARMOR);
   }

   public SkeletonRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2, ModelLayerLocation var3, ModelLayerLocation var4) {
      this(var1, var3, var4, new SkeletonModel<>(var1.bakeLayer(var2)));
   }

   public SkeletonRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2, ModelLayerLocation var3, SkeletonModel<T> var4) {
      super(var1, var4, 0.5F);
      this.addLayer(new HumanoidArmorLayer<>(this, new SkeletonModel(var1.bakeLayer(var2)), new SkeletonModel(var1.bakeLayer(var3)), var1.getModelManager()));
   }

   public ResourceLocation getTextureLocation(T var1) {
      return SKELETON_LOCATION;
   }

   protected boolean isShaking(T var1) {
      return var1.isShaking();
   }
}