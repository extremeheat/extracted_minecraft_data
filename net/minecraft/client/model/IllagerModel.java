package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.AbstractIllager;

public class IllagerModel extends ListModel implements ArmedModel, HeadedModel {
   private final ModelPart head;
   private final ModelPart hat;
   private final ModelPart body;
   private final ModelPart arms;
   private final ModelPart leftLeg;
   private final ModelPart rightLeg;
   private final ModelPart rightArm;
   private final ModelPart leftArm;
   private float itemUseTicks;

   public IllagerModel(float var1, float var2, int var3, int var4) {
      this.head = (new ModelPart(this)).setTexSize(var3, var4);
      this.head.setPos(0.0F, 0.0F + var2, 0.0F);
      this.head.texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, var1);
      this.hat = (new ModelPart(this, 32, 0)).setTexSize(var3, var4);
      this.hat.addBox(-4.0F, -10.0F, -4.0F, 8.0F, 12.0F, 8.0F, var1 + 0.45F);
      this.head.addChild(this.hat);
      this.hat.visible = false;
      ModelPart var5 = (new ModelPart(this)).setTexSize(var3, var4);
      var5.setPos(0.0F, var2 - 2.0F, 0.0F);
      var5.texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, var1);
      this.head.addChild(var5);
      this.body = (new ModelPart(this)).setTexSize(var3, var4);
      this.body.setPos(0.0F, 0.0F + var2, 0.0F);
      this.body.texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, var1);
      this.body.texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, var1 + 0.5F);
      this.arms = (new ModelPart(this)).setTexSize(var3, var4);
      this.arms.setPos(0.0F, 0.0F + var2 + 2.0F, 0.0F);
      this.arms.texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, var1);
      ModelPart var6 = (new ModelPart(this, 44, 22)).setTexSize(var3, var4);
      var6.mirror = true;
      var6.addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, var1);
      this.arms.addChild(var6);
      this.arms.texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, var1);
      this.leftLeg = (new ModelPart(this, 0, 22)).setTexSize(var3, var4);
      this.leftLeg.setPos(-2.0F, 12.0F + var2, 0.0F);
      this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var1);
      this.rightLeg = (new ModelPart(this, 0, 22)).setTexSize(var3, var4);
      this.rightLeg.mirror = true;
      this.rightLeg.setPos(2.0F, 12.0F + var2, 0.0F);
      this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var1);
      this.rightArm = (new ModelPart(this, 40, 46)).setTexSize(var3, var4);
      this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, var1);
      this.rightArm.setPos(-5.0F, 2.0F + var2, 0.0F);
      this.leftArm = (new ModelPart(this, 40, 46)).setTexSize(var3, var4);
      this.leftArm.mirror = true;
      this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, var1);
      this.leftArm.setPos(5.0F, 2.0F + var2, 0.0F);
   }

   public Iterable parts() {
      return ImmutableList.of(this.head, this.body, this.leftLeg, this.rightLeg, this.arms, this.rightArm, this.leftArm);
   }

   public void setupAnim(AbstractIllager var1, float var2, float var3, float var4, float var5, float var6) {
      this.head.yRot = var5 * 0.017453292F;
      this.head.xRot = var6 * 0.017453292F;
      this.arms.y = 3.0F;
      this.arms.z = -1.0F;
      this.arms.xRot = -0.75F;
      if (this.riding) {
         this.rightArm.xRot = -0.62831855F;
         this.rightArm.yRot = 0.0F;
         this.rightArm.zRot = 0.0F;
         this.leftArm.xRot = -0.62831855F;
         this.leftArm.yRot = 0.0F;
         this.leftArm.zRot = 0.0F;
         this.leftLeg.xRot = -1.4137167F;
         this.leftLeg.yRot = 0.31415927F;
         this.leftLeg.zRot = 0.07853982F;
         this.rightLeg.xRot = -1.4137167F;
         this.rightLeg.yRot = -0.31415927F;
         this.rightLeg.zRot = -0.07853982F;
      } else {
         this.rightArm.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 2.0F * var3 * 0.5F;
         this.rightArm.yRot = 0.0F;
         this.rightArm.zRot = 0.0F;
         this.leftArm.xRot = Mth.cos(var2 * 0.6662F) * 2.0F * var3 * 0.5F;
         this.leftArm.yRot = 0.0F;
         this.leftArm.zRot = 0.0F;
         this.leftLeg.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3 * 0.5F;
         this.leftLeg.yRot = 0.0F;
         this.leftLeg.zRot = 0.0F;
         this.rightLeg.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3 * 0.5F;
         this.rightLeg.yRot = 0.0F;
         this.rightLeg.zRot = 0.0F;
      }

      AbstractIllager.IllagerArmPose var7 = var1.getArmPose();
      float var8;
      if (var7 == AbstractIllager.IllagerArmPose.ATTACKING) {
         var8 = Mth.sin(this.attackTime * 3.1415927F);
         float var9 = Mth.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * 3.1415927F);
         this.rightArm.zRot = 0.0F;
         this.leftArm.zRot = 0.0F;
         this.rightArm.yRot = 0.15707964F;
         this.leftArm.yRot = -0.15707964F;
         ModelPart var10000;
         if (var1.getMainArm() == HumanoidArm.RIGHT) {
            this.rightArm.xRot = -1.8849558F + Mth.cos(var4 * 0.09F) * 0.15F;
            this.leftArm.xRot = -0.0F + Mth.cos(var4 * 0.19F) * 0.5F;
            var10000 = this.rightArm;
            var10000.xRot += var8 * 2.2F - var9 * 0.4F;
            var10000 = this.leftArm;
            var10000.xRot += var8 * 1.2F - var9 * 0.4F;
         } else {
            this.rightArm.xRot = -0.0F + Mth.cos(var4 * 0.19F) * 0.5F;
            this.leftArm.xRot = -1.8849558F + Mth.cos(var4 * 0.09F) * 0.15F;
            var10000 = this.rightArm;
            var10000.xRot += var8 * 1.2F - var9 * 0.4F;
            var10000 = this.leftArm;
            var10000.xRot += var8 * 2.2F - var9 * 0.4F;
         }

         var10000 = this.rightArm;
         var10000.zRot += Mth.cos(var4 * 0.09F) * 0.05F + 0.05F;
         var10000 = this.leftArm;
         var10000.zRot -= Mth.cos(var4 * 0.09F) * 0.05F + 0.05F;
         var10000 = this.rightArm;
         var10000.xRot += Mth.sin(var4 * 0.067F) * 0.05F;
         var10000 = this.leftArm;
         var10000.xRot -= Mth.sin(var4 * 0.067F) * 0.05F;
      } else if (var7 == AbstractIllager.IllagerArmPose.SPELLCASTING) {
         this.rightArm.z = 0.0F;
         this.rightArm.x = -5.0F;
         this.leftArm.z = 0.0F;
         this.leftArm.x = 5.0F;
         this.rightArm.xRot = Mth.cos(var4 * 0.6662F) * 0.25F;
         this.leftArm.xRot = Mth.cos(var4 * 0.6662F) * 0.25F;
         this.rightArm.zRot = 2.3561945F;
         this.leftArm.zRot = -2.3561945F;
         this.rightArm.yRot = 0.0F;
         this.leftArm.yRot = 0.0F;
      } else if (var7 == AbstractIllager.IllagerArmPose.BOW_AND_ARROW) {
         this.rightArm.yRot = -0.1F + this.head.yRot;
         this.rightArm.xRot = -1.5707964F + this.head.xRot;
         this.leftArm.xRot = -0.9424779F + this.head.xRot;
         this.leftArm.yRot = this.head.yRot - 0.4F;
         this.leftArm.zRot = 1.5707964F;
      } else if (var7 == AbstractIllager.IllagerArmPose.CROSSBOW_HOLD) {
         this.rightArm.yRot = -0.3F + this.head.yRot;
         this.leftArm.yRot = 0.6F + this.head.yRot;
         this.rightArm.xRot = -1.5707964F + this.head.xRot + 0.1F;
         this.leftArm.xRot = -1.5F + this.head.xRot;
      } else if (var7 == AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE) {
         this.rightArm.yRot = -0.8F;
         this.rightArm.xRot = -0.97079635F;
         this.leftArm.xRot = -0.97079635F;
         var8 = Mth.clamp(this.itemUseTicks, 0.0F, 25.0F);
         this.leftArm.yRot = Mth.lerp(var8 / 25.0F, 0.4F, 0.85F);
         this.leftArm.xRot = Mth.lerp(var8 / 25.0F, this.leftArm.xRot, -1.5707964F);
      } else if (var7 == AbstractIllager.IllagerArmPose.CELEBRATING) {
         this.rightArm.z = 0.0F;
         this.rightArm.x = -5.0F;
         this.rightArm.xRot = Mth.cos(var4 * 0.6662F) * 0.05F;
         this.rightArm.zRot = 2.670354F;
         this.rightArm.yRot = 0.0F;
         this.leftArm.z = 0.0F;
         this.leftArm.x = 5.0F;
         this.leftArm.xRot = Mth.cos(var4 * 0.6662F) * 0.05F;
         this.leftArm.zRot = -2.3561945F;
         this.leftArm.yRot = 0.0F;
      }

      boolean var10 = var7 == AbstractIllager.IllagerArmPose.CROSSED;
      this.arms.visible = var10;
      this.leftArm.visible = !var10;
      this.rightArm.visible = !var10;
   }

   public void prepareMobModel(AbstractIllager var1, float var2, float var3, float var4) {
      this.itemUseTicks = (float)var1.getTicksUsingItem();
      super.prepareMobModel(var1, var2, var3, var4);
   }

   private ModelPart getArm(HumanoidArm var1) {
      return var1 == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
   }

   public ModelPart getHat() {
      return this.hat;
   }

   public ModelPart getHead() {
      return this.head;
   }

   public void translateToHand(HumanoidArm var1, PoseStack var2) {
      this.getArm(var1).translateAndRotate(var2);
   }
}
