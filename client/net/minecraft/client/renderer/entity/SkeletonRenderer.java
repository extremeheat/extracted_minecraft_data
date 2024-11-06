package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.Items;

public class SkeletonRenderer extends AbstractSkeletonRenderer<Skeleton, SkeletonRenderState> {
   private static final ResourceLocation SKELETON_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/skeleton/skeleton.png");

   public SkeletonRenderer(EntityRendererProvider.Context var1) {
      super(var1, ModelLayers.SKELETON, ModelLayers.SKELETON_INNER_ARMOR, ModelLayers.SKELETON_OUTER_ARMOR);
   }

   public ResourceLocation getTextureLocation(SkeletonRenderState var1) {
      return SKELETON_LOCATION;
   }

   public SkeletonRenderState createRenderState() {
      return new SkeletonRenderState();
   }

   protected HumanoidModel.ArmPose getArmPose(Skeleton var1, HumanoidArm var2) {
      return var1.getMainArm() == var2 && var1.isAggressive() && var1.getMainHandItem().is(Items.BOW) ? HumanoidModel.ArmPose.BOW_AND_ARROW : HumanoidModel.ArmPose.EMPTY;
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
