package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DrownedModel<T extends Zombie> extends ZombieModel<T> {
   public DrownedModel(float var1, float var2, int var3, int var4) {
      super(var1, var2, var3, var4);
      this.rightArm = new ModelPart(this, 32, 48);
      this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, var1);
      this.rightArm.setPos(-5.0F, 2.0F + var2, 0.0F);
      this.rightLeg = new ModelPart(this, 16, 48);
      this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var1);
      this.rightLeg.setPos(-1.9F, 12.0F + var2, 0.0F);
   }

   public DrownedModel(float var1, boolean var2) {
      super(var1, 0.0F, 64, var2 ? 32 : 64);
   }

   public void prepareMobModel(T var1, float var2, float var3, float var4) {
      this.rightArmPose = HumanoidModel.ArmPose.EMPTY;
      this.leftArmPose = HumanoidModel.ArmPose.EMPTY;
      ItemStack var5 = var1.getItemInHand(InteractionHand.MAIN_HAND);
      if (var5.getItem() == Items.TRIDENT && var1.isAggressive()) {
         if (var1.getMainArm() == HumanoidArm.RIGHT) {
            this.rightArmPose = HumanoidModel.ArmPose.THROW_SPEAR;
         } else {
            this.leftArmPose = HumanoidModel.ArmPose.THROW_SPEAR;
         }
      }

      super.prepareMobModel(var1, var2, var3, var4);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      super.setupAnim(var1, var2, var3, var4, var5, var6);
      if (this.leftArmPose == HumanoidModel.ArmPose.THROW_SPEAR) {
         this.leftArm.xRot = this.leftArm.xRot * 0.5F - 3.1415927F;
         this.leftArm.yRot = 0.0F;
      }

      if (this.rightArmPose == HumanoidModel.ArmPose.THROW_SPEAR) {
         this.rightArm.xRot = this.rightArm.xRot * 0.5F - 3.1415927F;
         this.rightArm.yRot = 0.0F;
      }

      if (this.swimAmount > 0.0F) {
         this.rightArm.xRot = this.rotlerpRad(this.swimAmount, this.rightArm.xRot, -2.5132742F) + this.swimAmount * 0.35F * Mth.sin(0.1F * var4);
         this.leftArm.xRot = this.rotlerpRad(this.swimAmount, this.leftArm.xRot, -2.5132742F) - this.swimAmount * 0.35F * Mth.sin(0.1F * var4);
         this.rightArm.zRot = this.rotlerpRad(this.swimAmount, this.rightArm.zRot, -0.15F);
         this.leftArm.zRot = this.rotlerpRad(this.swimAmount, this.leftArm.zRot, 0.15F);
         ModelPart var10000 = this.leftLeg;
         var10000.xRot -= this.swimAmount * 0.55F * Mth.sin(0.1F * var4);
         var10000 = this.rightLeg;
         var10000.xRot += this.swimAmount * 0.55F * Mth.sin(0.1F * var4);
         this.head.xRot = 0.0F;
      }

   }
}
