package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

public class EndermanModel<T extends LivingEntity> extends HumanoidModel<T> {
   public boolean carrying;
   public boolean creepy;

   public EndermanModel(float var1) {
      super(0.0F, -14.0F, 64, 32);
      float var2 = -14.0F;
      this.hat = new ModelPart(this, 0, 16);
      this.hat.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, var1 - 0.5F);
      this.hat.setPos(0.0F, -14.0F, 0.0F);
      this.body = new ModelPart(this, 32, 16);
      this.body.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, var1);
      this.body.setPos(0.0F, -14.0F, 0.0F);
      this.rightArm = new ModelPart(this, 56, 0);
      this.rightArm.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F, var1);
      this.rightArm.setPos(-3.0F, -12.0F, 0.0F);
      this.leftArm = new ModelPart(this, 56, 0);
      this.leftArm.mirror = true;
      this.leftArm.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F, var1);
      this.leftArm.setPos(5.0F, -12.0F, 0.0F);
      this.rightLeg = new ModelPart(this, 56, 0);
      this.rightLeg.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F, var1);
      this.rightLeg.setPos(-2.0F, -2.0F, 0.0F);
      this.leftLeg = new ModelPart(this, 56, 0);
      this.leftLeg.mirror = true;
      this.leftLeg.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F, var1);
      this.leftLeg.setPos(2.0F, -2.0F, 0.0F);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      super.setupAnim(var1, var2, var3, var4, var5, var6);
      this.head.visible = true;
      float var7 = -14.0F;
      this.body.xRot = 0.0F;
      this.body.y = -14.0F;
      this.body.z = -0.0F;
      ModelPart var10000 = this.rightLeg;
      var10000.xRot -= 0.0F;
      var10000 = this.leftLeg;
      var10000.xRot -= 0.0F;
      var10000 = this.rightArm;
      var10000.xRot = (float)((double)var10000.xRot * 0.5D);
      var10000 = this.leftArm;
      var10000.xRot = (float)((double)var10000.xRot * 0.5D);
      var10000 = this.rightLeg;
      var10000.xRot = (float)((double)var10000.xRot * 0.5D);
      var10000 = this.leftLeg;
      var10000.xRot = (float)((double)var10000.xRot * 0.5D);
      float var8 = 0.4F;
      if (this.rightArm.xRot > 0.4F) {
         this.rightArm.xRot = 0.4F;
      }

      if (this.leftArm.xRot > 0.4F) {
         this.leftArm.xRot = 0.4F;
      }

      if (this.rightArm.xRot < -0.4F) {
         this.rightArm.xRot = -0.4F;
      }

      if (this.leftArm.xRot < -0.4F) {
         this.leftArm.xRot = -0.4F;
      }

      if (this.rightLeg.xRot > 0.4F) {
         this.rightLeg.xRot = 0.4F;
      }

      if (this.leftLeg.xRot > 0.4F) {
         this.leftLeg.xRot = 0.4F;
      }

      if (this.rightLeg.xRot < -0.4F) {
         this.rightLeg.xRot = -0.4F;
      }

      if (this.leftLeg.xRot < -0.4F) {
         this.leftLeg.xRot = -0.4F;
      }

      if (this.carrying) {
         this.rightArm.xRot = -0.5F;
         this.leftArm.xRot = -0.5F;
         this.rightArm.zRot = 0.05F;
         this.leftArm.zRot = -0.05F;
      }

      this.rightArm.z = 0.0F;
      this.leftArm.z = 0.0F;
      this.rightLeg.z = 0.0F;
      this.leftLeg.z = 0.0F;
      this.rightLeg.y = -5.0F;
      this.leftLeg.y = -5.0F;
      this.head.z = -0.0F;
      this.head.y = -13.0F;
      this.hat.x = this.head.x;
      this.hat.y = this.head.y;
      this.hat.z = this.head.z;
      this.hat.xRot = this.head.xRot;
      this.hat.yRot = this.head.yRot;
      this.hat.zRot = this.head.zRot;
      float var9;
      if (this.creepy) {
         var9 = 1.0F;
         var10000 = this.head;
         var10000.y -= 5.0F;
      }

      var9 = -14.0F;
      this.rightArm.setPos(-5.0F, -12.0F, 0.0F);
      this.leftArm.setPos(5.0F, -12.0F, 0.0F);
   }
}
