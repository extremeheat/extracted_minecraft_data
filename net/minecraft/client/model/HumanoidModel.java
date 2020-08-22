package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Function;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;

public class HumanoidModel extends AgeableListModel implements ArmedModel, HeadedModel {
   public ModelPart head;
   public ModelPart hat;
   public ModelPart body;
   public ModelPart rightArm;
   public ModelPart leftArm;
   public ModelPart rightLeg;
   public ModelPart leftLeg;
   public HumanoidModel.ArmPose leftArmPose;
   public HumanoidModel.ArmPose rightArmPose;
   public boolean crouching;
   public float swimAmount;
   private float itemUseTicks;

   public HumanoidModel(float var1) {
      this(RenderType::entityCutoutNoCull, var1, 0.0F, 64, 32);
   }

   protected HumanoidModel(float var1, float var2, int var3, int var4) {
      this(RenderType::entityCutoutNoCull, var1, var2, var3, var4);
   }

   public HumanoidModel(Function var1, float var2, float var3, int var4, int var5) {
      super(var1, true, 16.0F, 0.0F, 2.0F, 2.0F, 24.0F);
      this.leftArmPose = HumanoidModel.ArmPose.EMPTY;
      this.rightArmPose = HumanoidModel.ArmPose.EMPTY;
      this.texWidth = var4;
      this.texHeight = var5;
      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, var2);
      this.head.setPos(0.0F, 0.0F + var3, 0.0F);
      this.hat = new ModelPart(this, 32, 0);
      this.hat.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, var2 + 0.5F);
      this.hat.setPos(0.0F, 0.0F + var3, 0.0F);
      this.body = new ModelPart(this, 16, 16);
      this.body.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, var2);
      this.body.setPos(0.0F, 0.0F + var3, 0.0F);
      this.rightArm = new ModelPart(this, 40, 16);
      this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, var2);
      this.rightArm.setPos(-5.0F, 2.0F + var3, 0.0F);
      this.leftArm = new ModelPart(this, 40, 16);
      this.leftArm.mirror = true;
      this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, var2);
      this.leftArm.setPos(5.0F, 2.0F + var3, 0.0F);
      this.rightLeg = new ModelPart(this, 0, 16);
      this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var2);
      this.rightLeg.setPos(-1.9F, 12.0F + var3, 0.0F);
      this.leftLeg = new ModelPart(this, 0, 16);
      this.leftLeg.mirror = true;
      this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var2);
      this.leftLeg.setPos(1.9F, 12.0F + var3, 0.0F);
   }

   protected Iterable headParts() {
      return ImmutableList.of(this.head);
   }

   protected Iterable bodyParts() {
      return ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg, this.hat);
   }

   public void prepareMobModel(LivingEntity var1, float var2, float var3, float var4) {
      this.swimAmount = var1.getSwimAmount(var4);
      this.itemUseTicks = (float)var1.getTicksUsingItem();
      super.prepareMobModel(var1, var2, var3, var4);
   }

   public void setupAnim(LivingEntity var1, float var2, float var3, float var4, float var5, float var6) {
      boolean var7 = var1.getFallFlyingTicks() > 4;
      boolean var8 = var1.isVisuallySwimming();
      this.head.yRot = var5 * 0.017453292F;
      if (var7) {
         this.head.xRot = -0.7853982F;
      } else if (this.swimAmount > 0.0F) {
         if (var8) {
            this.head.xRot = this.rotlerpRad(this.head.xRot, -0.7853982F, this.swimAmount);
         } else {
            this.head.xRot = this.rotlerpRad(this.head.xRot, var6 * 0.017453292F, this.swimAmount);
         }
      } else {
         this.head.xRot = var6 * 0.017453292F;
      }

      this.body.yRot = 0.0F;
      this.rightArm.z = 0.0F;
      this.rightArm.x = -5.0F;
      this.leftArm.z = 0.0F;
      this.leftArm.x = 5.0F;
      float var9 = 1.0F;
      if (var7) {
         var9 = (float)var1.getDeltaMovement().lengthSqr();
         var9 /= 0.2F;
         var9 *= var9 * var9;
      }

      if (var9 < 1.0F) {
         var9 = 1.0F;
      }

      this.rightArm.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 2.0F * var3 * 0.5F / var9;
      this.leftArm.xRot = Mth.cos(var2 * 0.6662F) * 2.0F * var3 * 0.5F / var9;
      this.rightArm.zRot = 0.0F;
      this.leftArm.zRot = 0.0F;
      this.rightLeg.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3 / var9;
      this.leftLeg.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3 / var9;
      this.rightLeg.yRot = 0.0F;
      this.leftLeg.yRot = 0.0F;
      this.rightLeg.zRot = 0.0F;
      this.leftLeg.zRot = 0.0F;
      ModelPart var10000;
      if (this.riding) {
         var10000 = this.rightArm;
         var10000.xRot += -0.62831855F;
         var10000 = this.leftArm;
         var10000.xRot += -0.62831855F;
         this.rightLeg.xRot = -1.4137167F;
         this.rightLeg.yRot = 0.31415927F;
         this.rightLeg.zRot = 0.07853982F;
         this.leftLeg.xRot = -1.4137167F;
         this.leftLeg.yRot = -0.31415927F;
         this.leftLeg.zRot = -0.07853982F;
      }

      this.rightArm.yRot = 0.0F;
      this.rightArm.zRot = 0.0F;
      switch(this.leftArmPose) {
      case EMPTY:
         this.leftArm.yRot = 0.0F;
         break;
      case BLOCK:
         this.leftArm.xRot = this.leftArm.xRot * 0.5F - 0.9424779F;
         this.leftArm.yRot = 0.5235988F;
         break;
      case ITEM:
         this.leftArm.xRot = this.leftArm.xRot * 0.5F - 0.31415927F;
         this.leftArm.yRot = 0.0F;
      }

      switch(this.rightArmPose) {
      case EMPTY:
         this.rightArm.yRot = 0.0F;
         break;
      case BLOCK:
         this.rightArm.xRot = this.rightArm.xRot * 0.5F - 0.9424779F;
         this.rightArm.yRot = -0.5235988F;
         break;
      case ITEM:
         this.rightArm.xRot = this.rightArm.xRot * 0.5F - 0.31415927F;
         this.rightArm.yRot = 0.0F;
         break;
      case THROW_SPEAR:
         this.rightArm.xRot = this.rightArm.xRot * 0.5F - 3.1415927F;
         this.rightArm.yRot = 0.0F;
      }

      if (this.leftArmPose == HumanoidModel.ArmPose.THROW_SPEAR && this.rightArmPose != HumanoidModel.ArmPose.BLOCK && this.rightArmPose != HumanoidModel.ArmPose.THROW_SPEAR && this.rightArmPose != HumanoidModel.ArmPose.BOW_AND_ARROW) {
         this.leftArm.xRot = this.leftArm.xRot * 0.5F - 3.1415927F;
         this.leftArm.yRot = 0.0F;
      }

      float var12;
      float var13;
      float var14;
      if (this.attackTime > 0.0F) {
         HumanoidArm var10 = this.getAttackArm(var1);
         ModelPart var11 = this.getArm(var10);
         var12 = this.attackTime;
         this.body.yRot = Mth.sin(Mth.sqrt(var12) * 6.2831855F) * 0.2F;
         if (var10 == HumanoidArm.LEFT) {
            var10000 = this.body;
            var10000.yRot *= -1.0F;
         }

         this.rightArm.z = Mth.sin(this.body.yRot) * 5.0F;
         this.rightArm.x = -Mth.cos(this.body.yRot) * 5.0F;
         this.leftArm.z = -Mth.sin(this.body.yRot) * 5.0F;
         this.leftArm.x = Mth.cos(this.body.yRot) * 5.0F;
         var10000 = this.rightArm;
         var10000.yRot += this.body.yRot;
         var10000 = this.leftArm;
         var10000.yRot += this.body.yRot;
         var10000 = this.leftArm;
         var10000.xRot += this.body.yRot;
         var12 = 1.0F - this.attackTime;
         var12 *= var12;
         var12 *= var12;
         var12 = 1.0F - var12;
         var13 = Mth.sin(var12 * 3.1415927F);
         var14 = Mth.sin(this.attackTime * 3.1415927F) * -(this.head.xRot - 0.7F) * 0.75F;
         var11.xRot = (float)((double)var11.xRot - ((double)var13 * 1.2D + (double)var14));
         var11.yRot += this.body.yRot * 2.0F;
         var11.zRot += Mth.sin(this.attackTime * 3.1415927F) * -0.4F;
      }

      if (this.crouching) {
         this.body.xRot = 0.5F;
         var10000 = this.rightArm;
         var10000.xRot += 0.4F;
         var10000 = this.leftArm;
         var10000.xRot += 0.4F;
         this.rightLeg.z = 4.0F;
         this.leftLeg.z = 4.0F;
         this.rightLeg.y = 12.2F;
         this.leftLeg.y = 12.2F;
         this.head.y = 4.2F;
         this.body.y = 3.2F;
         this.leftArm.y = 5.2F;
         this.rightArm.y = 5.2F;
      } else {
         this.body.xRot = 0.0F;
         this.rightLeg.z = 0.1F;
         this.leftLeg.z = 0.1F;
         this.rightLeg.y = 12.0F;
         this.leftLeg.y = 12.0F;
         this.head.y = 0.0F;
         this.body.y = 0.0F;
         this.leftArm.y = 2.0F;
         this.rightArm.y = 2.0F;
      }

      var10000 = this.rightArm;
      var10000.zRot += Mth.cos(var4 * 0.09F) * 0.05F + 0.05F;
      var10000 = this.leftArm;
      var10000.zRot -= Mth.cos(var4 * 0.09F) * 0.05F + 0.05F;
      var10000 = this.rightArm;
      var10000.xRot += Mth.sin(var4 * 0.067F) * 0.05F;
      var10000 = this.leftArm;
      var10000.xRot -= Mth.sin(var4 * 0.067F) * 0.05F;
      if (this.rightArmPose == HumanoidModel.ArmPose.BOW_AND_ARROW) {
         this.rightArm.yRot = -0.1F + this.head.yRot;
         this.leftArm.yRot = 0.1F + this.head.yRot + 0.4F;
         this.rightArm.xRot = -1.5707964F + this.head.xRot;
         this.leftArm.xRot = -1.5707964F + this.head.xRot;
      } else if (this.leftArmPose == HumanoidModel.ArmPose.BOW_AND_ARROW && this.rightArmPose != HumanoidModel.ArmPose.THROW_SPEAR && this.rightArmPose != HumanoidModel.ArmPose.BLOCK) {
         this.rightArm.yRot = -0.1F + this.head.yRot - 0.4F;
         this.leftArm.yRot = 0.1F + this.head.yRot;
         this.rightArm.xRot = -1.5707964F + this.head.xRot;
         this.leftArm.xRot = -1.5707964F + this.head.xRot;
      }

      float var15 = (float)CrossbowItem.getChargeDuration(var1.getUseItem());
      float var16;
      if (this.rightArmPose == HumanoidModel.ArmPose.CROSSBOW_CHARGE) {
         this.rightArm.yRot = -0.8F;
         this.rightArm.xRot = -0.97079635F;
         this.leftArm.xRot = -0.97079635F;
         var16 = Mth.clamp(this.itemUseTicks, 0.0F, var15);
         this.leftArm.yRot = Mth.lerp(var16 / var15, 0.4F, 0.85F);
         this.leftArm.xRot = Mth.lerp(var16 / var15, this.leftArm.xRot, -1.5707964F);
      } else if (this.leftArmPose == HumanoidModel.ArmPose.CROSSBOW_CHARGE) {
         this.leftArm.yRot = 0.8F;
         this.rightArm.xRot = -0.97079635F;
         this.leftArm.xRot = -0.97079635F;
         var16 = Mth.clamp(this.itemUseTicks, 0.0F, var15);
         this.rightArm.yRot = Mth.lerp(var16 / var15, -0.4F, -0.85F);
         this.rightArm.xRot = Mth.lerp(var16 / var15, this.rightArm.xRot, -1.5707964F);
      }

      if (this.rightArmPose == HumanoidModel.ArmPose.CROSSBOW_HOLD && this.attackTime <= 0.0F) {
         this.rightArm.yRot = -0.3F + this.head.yRot;
         this.leftArm.yRot = 0.6F + this.head.yRot;
         this.rightArm.xRot = -1.5707964F + this.head.xRot + 0.1F;
         this.leftArm.xRot = -1.5F + this.head.xRot;
      } else if (this.leftArmPose == HumanoidModel.ArmPose.CROSSBOW_HOLD) {
         this.rightArm.yRot = -0.6F + this.head.yRot;
         this.leftArm.yRot = 0.3F + this.head.yRot;
         this.rightArm.xRot = -1.5F + this.head.xRot;
         this.leftArm.xRot = -1.5707964F + this.head.xRot + 0.1F;
      }

      if (this.swimAmount > 0.0F) {
         var16 = var2 % 26.0F;
         var12 = this.attackTime > 0.0F ? 0.0F : this.swimAmount;
         if (var16 < 14.0F) {
            this.leftArm.xRot = this.rotlerpRad(this.leftArm.xRot, 0.0F, this.swimAmount);
            this.rightArm.xRot = Mth.lerp(var12, this.rightArm.xRot, 0.0F);
            this.leftArm.yRot = this.rotlerpRad(this.leftArm.yRot, 3.1415927F, this.swimAmount);
            this.rightArm.yRot = Mth.lerp(var12, this.rightArm.yRot, 3.1415927F);
            this.leftArm.zRot = this.rotlerpRad(this.leftArm.zRot, 3.1415927F + 1.8707964F * this.quadraticArmUpdate(var16) / this.quadraticArmUpdate(14.0F), this.swimAmount);
            this.rightArm.zRot = Mth.lerp(var12, this.rightArm.zRot, 3.1415927F - 1.8707964F * this.quadraticArmUpdate(var16) / this.quadraticArmUpdate(14.0F));
         } else if (var16 >= 14.0F && var16 < 22.0F) {
            var13 = (var16 - 14.0F) / 8.0F;
            this.leftArm.xRot = this.rotlerpRad(this.leftArm.xRot, 1.5707964F * var13, this.swimAmount);
            this.rightArm.xRot = Mth.lerp(var12, this.rightArm.xRot, 1.5707964F * var13);
            this.leftArm.yRot = this.rotlerpRad(this.leftArm.yRot, 3.1415927F, this.swimAmount);
            this.rightArm.yRot = Mth.lerp(var12, this.rightArm.yRot, 3.1415927F);
            this.leftArm.zRot = this.rotlerpRad(this.leftArm.zRot, 5.012389F - 1.8707964F * var13, this.swimAmount);
            this.rightArm.zRot = Mth.lerp(var12, this.rightArm.zRot, 1.2707963F + 1.8707964F * var13);
         } else if (var16 >= 22.0F && var16 < 26.0F) {
            var13 = (var16 - 22.0F) / 4.0F;
            this.leftArm.xRot = this.rotlerpRad(this.leftArm.xRot, 1.5707964F - 1.5707964F * var13, this.swimAmount);
            this.rightArm.xRot = Mth.lerp(var12, this.rightArm.xRot, 1.5707964F - 1.5707964F * var13);
            this.leftArm.yRot = this.rotlerpRad(this.leftArm.yRot, 3.1415927F, this.swimAmount);
            this.rightArm.yRot = Mth.lerp(var12, this.rightArm.yRot, 3.1415927F);
            this.leftArm.zRot = this.rotlerpRad(this.leftArm.zRot, 3.1415927F, this.swimAmount);
            this.rightArm.zRot = Mth.lerp(var12, this.rightArm.zRot, 3.1415927F);
         }

         var13 = 0.3F;
         var14 = 0.33333334F;
         this.leftLeg.xRot = Mth.lerp(this.swimAmount, this.leftLeg.xRot, 0.3F * Mth.cos(var2 * 0.33333334F + 3.1415927F));
         this.rightLeg.xRot = Mth.lerp(this.swimAmount, this.rightLeg.xRot, 0.3F * Mth.cos(var2 * 0.33333334F));
      }

      this.hat.copyFrom(this.head);
   }

   protected float rotlerpRad(float var1, float var2, float var3) {
      float var4 = (var2 - var1) % 6.2831855F;
      if (var4 < -3.1415927F) {
         var4 += 6.2831855F;
      }

      if (var4 >= 3.1415927F) {
         var4 -= 6.2831855F;
      }

      return var1 + var3 * var4;
   }

   private float quadraticArmUpdate(float var1) {
      return -65.0F * var1 + var1 * var1;
   }

   public void copyPropertiesTo(HumanoidModel var1) {
      super.copyPropertiesTo(var1);
      var1.leftArmPose = this.leftArmPose;
      var1.rightArmPose = this.rightArmPose;
      var1.crouching = this.crouching;
   }

   public void setAllVisible(boolean var1) {
      this.head.visible = var1;
      this.hat.visible = var1;
      this.body.visible = var1;
      this.rightArm.visible = var1;
      this.leftArm.visible = var1;
      this.rightLeg.visible = var1;
      this.leftLeg.visible = var1;
   }

   public void translateToHand(HumanoidArm var1, PoseStack var2) {
      this.getArm(var1).translateAndRotate(var2);
   }

   protected ModelPart getArm(HumanoidArm var1) {
      return var1 == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
   }

   public ModelPart getHead() {
      return this.head;
   }

   protected HumanoidArm getAttackArm(LivingEntity var1) {
      HumanoidArm var2 = var1.getMainArm();
      return var1.swingingArm == InteractionHand.MAIN_HAND ? var2 : var2.getOpposite();
   }

   public static enum ArmPose {
      EMPTY,
      ITEM,
      BLOCK,
      BOW_AND_ARROW,
      THROW_SPEAR,
      CROSSBOW_CHARGE,
      CROSSBOW_HOLD;
   }
}
