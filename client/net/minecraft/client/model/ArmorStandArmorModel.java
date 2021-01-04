package net.minecraft.client.model;

import net.minecraft.world.entity.decoration.ArmorStand;

public class ArmorStandArmorModel extends HumanoidModel<ArmorStand> {
   public ArmorStandArmorModel() {
      this(0.0F);
   }

   public ArmorStandArmorModel(float var1) {
      this(var1, 64, 32);
   }

   protected ArmorStandArmorModel(float var1, int var2, int var3) {
      super(var1, 0.0F, var2, var3);
   }

   public void setupAnim(ArmorStand var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.head.xRot = 0.017453292F * var1.getHeadPose().getX();
      this.head.yRot = 0.017453292F * var1.getHeadPose().getY();
      this.head.zRot = 0.017453292F * var1.getHeadPose().getZ();
      this.head.setPos(0.0F, 1.0F, 0.0F);
      this.body.xRot = 0.017453292F * var1.getBodyPose().getX();
      this.body.yRot = 0.017453292F * var1.getBodyPose().getY();
      this.body.zRot = 0.017453292F * var1.getBodyPose().getZ();
      this.leftArm.xRot = 0.017453292F * var1.getLeftArmPose().getX();
      this.leftArm.yRot = 0.017453292F * var1.getLeftArmPose().getY();
      this.leftArm.zRot = 0.017453292F * var1.getLeftArmPose().getZ();
      this.rightArm.xRot = 0.017453292F * var1.getRightArmPose().getX();
      this.rightArm.yRot = 0.017453292F * var1.getRightArmPose().getY();
      this.rightArm.zRot = 0.017453292F * var1.getRightArmPose().getZ();
      this.leftLeg.xRot = 0.017453292F * var1.getLeftLegPose().getX();
      this.leftLeg.yRot = 0.017453292F * var1.getLeftLegPose().getY();
      this.leftLeg.zRot = 0.017453292F * var1.getLeftLegPose().getZ();
      this.leftLeg.setPos(1.9F, 11.0F, 0.0F);
      this.rightLeg.xRot = 0.017453292F * var1.getRightLegPose().getX();
      this.rightLeg.yRot = 0.017453292F * var1.getRightLegPose().getY();
      this.rightLeg.zRot = 0.017453292F * var1.getRightLegPose().getZ();
      this.rightLeg.setPos(-1.9F, 11.0F, 0.0F);
      this.hat.copyFrom(this.head);
   }
}
