package net.minecraft.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ArmorStandModel extends ArmorStandArmorModel {
   private final ModelPart bodyStick1;
   private final ModelPart bodyStick2;
   private final ModelPart shoulderStick;
   private final ModelPart basePlate;

   public ArmorStandModel() {
      this(0.0F);
   }

   public ArmorStandModel(float var1) {
      super(var1, 64, 64);
      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-1.0F, -7.0F, -1.0F, 2, 7, 2, var1);
      this.head.setPos(0.0F, 0.0F, 0.0F);
      this.body = new ModelPart(this, 0, 26);
      this.body.addBox(-6.0F, 0.0F, -1.5F, 12, 3, 3, var1);
      this.body.setPos(0.0F, 0.0F, 0.0F);
      this.rightArm = new ModelPart(this, 24, 0);
      this.rightArm.addBox(-2.0F, -2.0F, -1.0F, 2, 12, 2, var1);
      this.rightArm.setPos(-5.0F, 2.0F, 0.0F);
      this.leftArm = new ModelPart(this, 32, 16);
      this.leftArm.mirror = true;
      this.leftArm.addBox(0.0F, -2.0F, -1.0F, 2, 12, 2, var1);
      this.leftArm.setPos(5.0F, 2.0F, 0.0F);
      this.rightLeg = new ModelPart(this, 8, 0);
      this.rightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 11, 2, var1);
      this.rightLeg.setPos(-1.9F, 12.0F, 0.0F);
      this.leftLeg = new ModelPart(this, 40, 16);
      this.leftLeg.mirror = true;
      this.leftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 11, 2, var1);
      this.leftLeg.setPos(1.9F, 12.0F, 0.0F);
      this.bodyStick1 = new ModelPart(this, 16, 0);
      this.bodyStick1.addBox(-3.0F, 3.0F, -1.0F, 2, 7, 2, var1);
      this.bodyStick1.setPos(0.0F, 0.0F, 0.0F);
      this.bodyStick1.visible = true;
      this.bodyStick2 = new ModelPart(this, 48, 16);
      this.bodyStick2.addBox(1.0F, 3.0F, -1.0F, 2, 7, 2, var1);
      this.bodyStick2.setPos(0.0F, 0.0F, 0.0F);
      this.shoulderStick = new ModelPart(this, 0, 48);
      this.shoulderStick.addBox(-4.0F, 10.0F, -1.0F, 8, 2, 2, var1);
      this.shoulderStick.setPos(0.0F, 0.0F, 0.0F);
      this.basePlate = new ModelPart(this, 0, 32);
      this.basePlate.addBox(-6.0F, 11.0F, -6.0F, 12, 1, 12, var1);
      this.basePlate.setPos(0.0F, 12.0F, 0.0F);
      this.hat.visible = false;
   }

   public void setupAnim(ArmorStand var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      this.leftArm.visible = var1.isShowArms();
      this.rightArm.visible = var1.isShowArms();
      this.basePlate.visible = !var1.isNoBasePlate();
      this.leftLeg.setPos(1.9F, 12.0F, 0.0F);
      this.rightLeg.setPos(-1.9F, 12.0F, 0.0F);
      this.bodyStick1.xRot = 0.017453292F * var1.getBodyPose().getX();
      this.bodyStick1.yRot = 0.017453292F * var1.getBodyPose().getY();
      this.bodyStick1.zRot = 0.017453292F * var1.getBodyPose().getZ();
      this.bodyStick2.xRot = 0.017453292F * var1.getBodyPose().getX();
      this.bodyStick2.yRot = 0.017453292F * var1.getBodyPose().getY();
      this.bodyStick2.zRot = 0.017453292F * var1.getBodyPose().getZ();
      this.shoulderStick.xRot = 0.017453292F * var1.getBodyPose().getX();
      this.shoulderStick.yRot = 0.017453292F * var1.getBodyPose().getY();
      this.shoulderStick.zRot = 0.017453292F * var1.getBodyPose().getZ();
      this.basePlate.xRot = 0.0F;
      this.basePlate.yRot = 0.017453292F * -var1.yRot;
      this.basePlate.zRot = 0.0F;
   }

   public void render(ArmorStand var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.render(var1, var2, var3, var4, var5, var6, var7);
      GlStateManager.pushMatrix();
      if (this.young) {
         float var8 = 2.0F;
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.translatef(0.0F, 24.0F * var7, 0.0F);
         this.bodyStick1.render(var7);
         this.bodyStick2.render(var7);
         this.shoulderStick.render(var7);
         this.basePlate.render(var7);
      } else {
         if (var1.isSneaking()) {
            GlStateManager.translatef(0.0F, 0.2F, 0.0F);
         }

         this.bodyStick1.render(var7);
         this.bodyStick2.render(var7);
         this.shoulderStick.render(var7);
         this.basePlate.render(var7);
      }

      GlStateManager.popMatrix();
   }

   public void translateToHand(float var1, HumanoidArm var2) {
      ModelPart var3 = this.getArm(var2);
      boolean var4 = var3.visible;
      var3.visible = true;
      super.translateToHand(var1, var2);
      var3.visible = var4;
   }
}
