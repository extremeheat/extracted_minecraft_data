package net.minecraft.client.model.monster.piglin;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.PiglinRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;

public class PiglinModel extends AbstractPiglinModel<PiglinRenderState> {
   public PiglinModel(final ModelPart root) {
      super(root);
   }

   public void setupAnim(final PiglinRenderState state) {
      super.setupAnim(state);
      float defaultAngle = 0.5235988F;
      float attackTime = state.attackTime;
      PiglinArmPose pose = state.armPose;
      if (pose == PiglinArmPose.DANCING) {
         float dancePos = state.ageInTicks / 60.0F;
         this.rightEar.zRot = 0.5235988F + 0.017453292F * Mth.sin((double)(dancePos * 30.0F)) * 10.0F;
         this.leftEar.zRot = -0.5235988F - 0.017453292F * Mth.cos((double)(dancePos * 30.0F)) * 10.0F;
         ModelPart var10000 = this.head;
         var10000.x += Mth.sin((double)(dancePos * 10.0F));
         var10000 = this.head;
         var10000.y += Mth.sin((double)(dancePos * 40.0F)) + 0.4F;
         this.rightArm.zRot = 0.017453292F * (70.0F + Mth.cos((double)(dancePos * 40.0F)) * 10.0F);
         this.leftArm.zRot = this.rightArm.zRot * -1.0F;
         var10000 = this.rightArm;
         var10000.y += Mth.sin((double)(dancePos * 40.0F)) * 0.5F - 0.5F;
         var10000 = this.leftArm;
         var10000.y += Mth.sin((double)(dancePos * 40.0F)) * 0.5F + 0.5F;
         var10000 = this.body;
         var10000.y += Mth.sin((double)(dancePos * 40.0F)) * 0.35F;
      } else if (pose == PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON && attackTime == 0.0F) {
         this.holdWeaponHigh(state);
      } else if (pose == PiglinArmPose.CROSSBOW_HOLD) {
         AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, state.mainArm == HumanoidArm.RIGHT);
      } else if (pose == PiglinArmPose.CROSSBOW_CHARGE) {
         AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, state.maxCrossbowChageDuration, state.ticksUsingItem, state.mainArm == HumanoidArm.RIGHT);
      } else if (pose == PiglinArmPose.ADMIRING_ITEM) {
         this.head.xRot = 0.5F;
         this.head.yRot = 0.0F;
         if (state.mainArm == HumanoidArm.LEFT) {
            this.rightArm.yRot = -0.5F;
            this.rightArm.xRot = -0.9F;
         } else {
            this.leftArm.yRot = 0.5F;
            this.leftArm.xRot = -0.9F;
         }
      }

   }

   protected void setupAttackAnimation(final PiglinRenderState state) {
      float attackTime = state.attackTime;
      if (attackTime > 0.0F && state.armPose == PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON) {
         AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, state.mainArm, attackTime, state.ageInTicks);
      } else {
         super.setupAttackAnimation(state);
      }
   }

   private void holdWeaponHigh(final PiglinRenderState state) {
      if (state.mainArm == HumanoidArm.LEFT) {
         this.leftArm.xRot = -1.8F;
      } else {
         this.rightArm.xRot = -1.8F;
      }

   }

   public void setAllVisible(final boolean visible) {
      super.setAllVisible(visible);
      this.leftSleeve.visible = visible;
      this.rightSleeve.visible = visible;
      this.leftPants.visible = visible;
      this.rightPants.visible = visible;
      this.jacket.visible = visible;
   }
}
