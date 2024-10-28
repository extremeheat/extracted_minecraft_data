package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.PiglinRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;

public class PiglinModel extends AbstractPiglinModel<PiglinRenderState> {
   public PiglinModel(ModelPart var1) {
      super(var1);
   }

   public void setupAnim(PiglinRenderState var1) {
      super.setupAnim((HumanoidRenderState)var1);
      float var2 = 0.5235988F;
      float var3 = var1.attackTime;
      PiglinArmPose var4 = var1.armPose;
      if (var4 == PiglinArmPose.DANCING) {
         float var5 = var1.ageInTicks / 60.0F;
         this.rightEar.zRot = 0.5235988F + 0.017453292F * Mth.sin(var5 * 30.0F) * 10.0F;
         this.leftEar.zRot = -0.5235988F - 0.017453292F * Mth.cos(var5 * 30.0F) * 10.0F;
         ModelPart var10000 = this.head;
         var10000.x += Mth.sin(var5 * 10.0F);
         var10000 = this.head;
         var10000.y += Mth.sin(var5 * 40.0F) + 0.4F;
         this.rightArm.zRot = 0.017453292F * (70.0F + Mth.cos(var5 * 40.0F) * 10.0F);
         this.leftArm.zRot = this.rightArm.zRot * -1.0F;
         var10000 = this.rightArm;
         var10000.y += Mth.sin(var5 * 40.0F) * 0.5F - 0.5F;
         var10000 = this.leftArm;
         var10000.y += Mth.sin(var5 * 40.0F) * 0.5F + 0.5F;
         var10000 = this.body;
         var10000.y += Mth.sin(var5 * 40.0F) * 0.35F;
      } else if (var4 == PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON && var3 == 0.0F) {
         this.holdWeaponHigh(var1);
      } else if (var4 == PiglinArmPose.CROSSBOW_HOLD) {
         AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, var1.mainArm == HumanoidArm.RIGHT);
      } else if (var4 == PiglinArmPose.CROSSBOW_CHARGE) {
         AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, var1.maxCrossbowChageDuration, var1.ticksUsingItem, var1.mainArm == HumanoidArm.RIGHT);
      } else if (var4 == PiglinArmPose.ADMIRING_ITEM) {
         this.head.xRot = 0.5F;
         this.head.yRot = 0.0F;
         if (var1.mainArm == HumanoidArm.LEFT) {
            this.rightArm.yRot = -0.5F;
            this.rightArm.xRot = -0.9F;
         } else {
            this.leftArm.yRot = 0.5F;
            this.leftArm.xRot = -0.9F;
         }
      }

   }

   protected void setupAttackAnimation(PiglinRenderState var1, float var2) {
      float var3 = var1.attackTime;
      if (var3 > 0.0F && var1.armPose == PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON) {
         AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, var1.mainArm, var3, var1.ageInTicks);
      } else {
         super.setupAttackAnimation(var1, var2);
      }
   }

   private void holdWeaponHigh(PiglinRenderState var1) {
      if (var1.mainArm == HumanoidArm.LEFT) {
         this.leftArm.xRot = -1.8F;
      } else {
         this.rightArm.xRot = -1.8F;
      }

   }

   public void setAllVisible(boolean var1) {
      super.setAllVisible(var1);
      this.leftSleeve.visible = var1;
      this.rightSleeve.visible = var1;
      this.leftPants.visible = var1;
      this.rightPants.visible = var1;
      this.jacket.visible = var1;
   }
}
