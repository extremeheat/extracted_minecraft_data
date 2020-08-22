package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;

public class ZombieVillagerModel extends HumanoidModel implements VillagerHeadModel {
   private ModelPart hatRim;

   public ZombieVillagerModel(float var1, boolean var2) {
      super(var1, 0.0F, 64, var2 ? 32 : 64);
      if (var2) {
         this.head = new ModelPart(this, 0, 0);
         this.head.addBox(-4.0F, -10.0F, -4.0F, 8.0F, 8.0F, 8.0F, var1);
         this.body = new ModelPart(this, 16, 16);
         this.body.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, var1 + 0.1F);
         this.rightLeg = new ModelPart(this, 0, 16);
         this.rightLeg.setPos(-2.0F, 12.0F, 0.0F);
         this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var1 + 0.1F);
         this.leftLeg = new ModelPart(this, 0, 16);
         this.leftLeg.mirror = true;
         this.leftLeg.setPos(2.0F, 12.0F, 0.0F);
         this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var1 + 0.1F);
      } else {
         this.head = new ModelPart(this, 0, 0);
         this.head.texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, var1);
         this.head.texOffs(24, 0).addBox(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F, var1);
         this.hat = new ModelPart(this, 32, 0);
         this.hat.addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, var1 + 0.5F);
         this.hatRim = new ModelPart(this);
         this.hatRim.texOffs(30, 47).addBox(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F, var1);
         this.hatRim.xRot = -1.5707964F;
         this.hat.addChild(this.hatRim);
         this.body = new ModelPart(this, 16, 20);
         this.body.addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, var1);
         this.body.texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, var1 + 0.05F);
         this.rightArm = new ModelPart(this, 44, 22);
         this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, var1);
         this.rightArm.setPos(-5.0F, 2.0F, 0.0F);
         this.leftArm = new ModelPart(this, 44, 22);
         this.leftArm.mirror = true;
         this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, var1);
         this.leftArm.setPos(5.0F, 2.0F, 0.0F);
         this.rightLeg = new ModelPart(this, 0, 22);
         this.rightLeg.setPos(-2.0F, 12.0F, 0.0F);
         this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var1);
         this.leftLeg = new ModelPart(this, 0, 22);
         this.leftLeg.mirror = true;
         this.leftLeg.setPos(2.0F, 12.0F, 0.0F);
         this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var1);
      }

   }

   public void setupAnim(Zombie var1, float var2, float var3, float var4, float var5, float var6) {
      super.setupAnim((LivingEntity)var1, var2, var3, var4, var5, var6);
      float var7 = Mth.sin(this.attackTime * 3.1415927F);
      float var8 = Mth.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * 3.1415927F);
      this.rightArm.zRot = 0.0F;
      this.leftArm.zRot = 0.0F;
      this.rightArm.yRot = -(0.1F - var7 * 0.6F);
      this.leftArm.yRot = 0.1F - var7 * 0.6F;
      float var9 = -3.1415927F / (var1.isAggressive() ? 1.5F : 2.25F);
      this.rightArm.xRot = var9;
      this.leftArm.xRot = var9;
      ModelPart var10000 = this.rightArm;
      var10000.xRot += var7 * 1.2F - var8 * 0.4F;
      var10000 = this.leftArm;
      var10000.xRot += var7 * 1.2F - var8 * 0.4F;
      var10000 = this.rightArm;
      var10000.zRot += Mth.cos(var4 * 0.09F) * 0.05F + 0.05F;
      var10000 = this.leftArm;
      var10000.zRot -= Mth.cos(var4 * 0.09F) * 0.05F + 0.05F;
      var10000 = this.rightArm;
      var10000.xRot += Mth.sin(var4 * 0.067F) * 0.05F;
      var10000 = this.leftArm;
      var10000.xRot -= Mth.sin(var4 * 0.067F) * 0.05F;
   }

   public void hatVisible(boolean var1) {
      this.head.visible = var1;
      this.hat.visible = var1;
      this.hatRim.visible = var1;
   }
}
