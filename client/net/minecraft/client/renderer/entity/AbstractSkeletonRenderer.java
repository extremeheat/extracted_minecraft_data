package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.Items;

public abstract class AbstractSkeletonRenderer<T extends AbstractSkeleton, S extends SkeletonRenderState> extends HumanoidMobRenderer<T, S, SkeletonModel<S>> {
   public AbstractSkeletonRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2, ModelLayerLocation var3, ModelLayerLocation var4) {
      this(var1, var3, var4, new SkeletonModel(var1.bakeLayer(var2)));
   }

   public AbstractSkeletonRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2, ModelLayerLocation var3, SkeletonModel<S> var4) {
      super(var1, var4, 0.5F);
      this.addLayer(new HumanoidArmorLayer(this, new SkeletonModel(var1.bakeLayer(var2)), new SkeletonModel(var1.bakeLayer(var3)), var1.getEquipmentRenderer()));
   }

   public void extractRenderState(T var1, S var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isAggressive = var1.isAggressive();
      var2.isShaking = var1.isShaking();
      var2.isHoldingBow = var1.getMainHandItem().is(Items.BOW);
   }

   protected boolean isShaking(S var1) {
      return var1.isShaking;
   }

   protected HumanoidModel.ArmPose getArmPose(AbstractSkeleton var1, HumanoidArm var2) {
      return var1.getMainArm() == var2 && var1.isAggressive() && var1.getMainHandItem().is(Items.BOW) ? HumanoidModel.ArmPose.BOW_AND_ARROW : HumanoidModel.ArmPose.EMPTY;
   }
}
