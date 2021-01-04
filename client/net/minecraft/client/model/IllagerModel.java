package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.ArmedModel;
import net.minecraft.client.renderer.entity.HeadedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.AbstractIllager;

public class IllagerModel<T extends AbstractIllager> extends EntityModel<T> implements ArmedModel, HeadedModel {
   protected final ModelPart head;
   private final ModelPart hat;
   protected final ModelPart body;
   protected final ModelPart arms;
   protected final ModelPart leftLeg;
   protected final ModelPart rightLeg;
   private final ModelPart nose;
   protected final ModelPart rightArm;
   protected final ModelPart leftArm;
   private float itemUseTicks;

   public IllagerModel(float var1, float var2, int var3, int var4) {
      super();
      this.head = (new ModelPart(this)).setTexSize(var3, var4);
      this.head.setPos(0.0F, 0.0F + var2, 0.0F);
      this.head.texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, var1);
      this.hat = (new ModelPart(this, 32, 0)).setTexSize(var3, var4);
      this.hat.addBox(-4.0F, -10.0F, -4.0F, 8, 12, 8, var1 + 0.45F);
      this.head.addChild(this.hat);
      this.hat.visible = false;
      this.nose = (new ModelPart(this)).setTexSize(var3, var4);
      this.nose.setPos(0.0F, var2 - 2.0F, 0.0F);
      this.nose.texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2, 4, 2, var1);
      this.head.addChild(this.nose);
      this.body = (new ModelPart(this)).setTexSize(var3, var4);
      this.body.setPos(0.0F, 0.0F + var2, 0.0F);
      this.body.texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, var1);
      this.body.texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8, 18, 6, var1 + 0.5F);
      this.arms = (new ModelPart(this)).setTexSize(var3, var4);
      this.arms.setPos(0.0F, 0.0F + var2 + 2.0F, 0.0F);
      this.arms.texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4, 8, 4, var1);
      ModelPart var5 = (new ModelPart(this, 44, 22)).setTexSize(var3, var4);
      var5.mirror = true;
      var5.addBox(4.0F, -2.0F, -2.0F, 4, 8, 4, var1);
      this.arms.addChild(var5);
      this.arms.texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8, 4, 4, var1);
      this.leftLeg = (new ModelPart(this, 0, 22)).setTexSize(var3, var4);
      this.leftLeg.setPos(-2.0F, 12.0F + var2, 0.0F);
      this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
      this.rightLeg = (new ModelPart(this, 0, 22)).setTexSize(var3, var4);
      this.rightLeg.mirror = true;
      this.rightLeg.setPos(2.0F, 12.0F + var2, 0.0F);
      this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
      this.rightArm = (new ModelPart(this, 40, 46)).setTexSize(var3, var4);
      this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, var1);
      this.rightArm.setPos(-5.0F, 2.0F + var2, 0.0F);
      this.leftArm = (new ModelPart(this, 40, 46)).setTexSize(var3, var4);
      this.leftArm.mirror = true;
      this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, var1);
      this.leftArm.setPos(5.0F, 2.0F + var2, 0.0F);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      this.head.render(var7);
      this.body.render(var7);
      this.leftLeg.render(var7);
      this.rightLeg.render(var7);
      if (var1.getArmPose() == AbstractIllager.IllagerArmPose.CROSSED) {
         this.arms.render(var7);
      } else {
         this.rightArm.render(var7);
         this.leftArm.render(var7);
      }

   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
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

      AbstractIllager.IllagerArmPose var8 = var1.getArmPose();
      float var9;
      if (var8 == AbstractIllager.IllagerArmPose.ATTACKING) {
         var9 = Mth.sin(this.attackTime * 3.1415927F);
         float var10 = Mth.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * 3.1415927F);
         this.rightArm.zRot = 0.0F;
         this.leftArm.zRot = 0.0F;
         this.rightArm.yRot = 0.15707964F;
         this.leftArm.yRot = -0.15707964F;
         ModelPart var10000;
         if (var1.getMainArm() == HumanoidArm.RIGHT) {
            this.rightArm.xRot = -1.8849558F + Mth.cos(var4 * 0.09F) * 0.15F;
            this.leftArm.xRot = -0.0F + Mth.cos(var4 * 0.19F) * 0.5F;
            var10000 = this.rightArm;
            var10000.xRot += var9 * 2.2F - var10 * 0.4F;
            var10000 = this.leftArm;
            var10000.xRot += var9 * 1.2F - var10 * 0.4F;
         } else {
            this.rightArm.xRot = -0.0F + Mth.cos(var4 * 0.19F) * 0.5F;
            this.leftArm.xRot = -1.8849558F + Mth.cos(var4 * 0.09F) * 0.15F;
            var10000 = this.rightArm;
            var10000.xRot += var9 * 1.2F - var10 * 0.4F;
            var10000 = this.leftArm;
            var10000.xRot += var9 * 2.2F - var10 * 0.4F;
         }

         var10000 = this.rightArm;
         var10000.zRot += Mth.cos(var4 * 0.09F) * 0.05F + 0.05F;
         var10000 = this.leftArm;
         var10000.zRot -= Mth.cos(var4 * 0.09F) * 0.05F + 0.05F;
         var10000 = this.rightArm;
         var10000.xRot += Mth.sin(var4 * 0.067F) * 0.05F;
         var10000 = this.leftArm;
         var10000.xRot -= Mth.sin(var4 * 0.067F) * 0.05F;
      } else if (var8 == AbstractIllager.IllagerArmPose.SPELLCASTING) {
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
      } else if (var8 == AbstractIllager.IllagerArmPose.BOW_AND_ARROW) {
         this.rightArm.yRot = -0.1F + this.head.yRot;
         this.rightArm.xRot = -1.5707964F + this.head.xRot;
         this.leftArm.xRot = -0.9424779F + this.head.xRot;
         this.leftArm.yRot = this.head.yRot - 0.4F;
         this.leftArm.zRot = 1.5707964F;
      } else if (var8 == AbstractIllager.IllagerArmPose.CROSSBOW_HOLD) {
         this.rightArm.yRot = -0.3F + this.head.yRot;
         this.leftArm.yRot = 0.6F + this.head.yRot;
         this.rightArm.xRot = -1.5707964F + this.head.xRot + 0.1F;
         this.leftArm.xRot = -1.5F + this.head.xRot;
      } else if (var8 == AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE) {
         this.rightArm.yRot = -0.8F;
         this.rightArm.xRot = -0.97079635F;
         this.leftArm.xRot = -0.97079635F;
         var9 = Mth.clamp(this.itemUseTicks, 0.0F, 25.0F);
         this.leftArm.yRot = Mth.lerp(var9 / 25.0F, 0.4F, 0.85F);
         this.leftArm.xRot = Mth.lerp(var9 / 25.0F, this.leftArm.xRot, -1.5707964F);
      } else if (var8 == AbstractIllager.IllagerArmPose.CELEBRATING) {
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

   }

   public void prepareMobModel(T var1, float var2, float var3, float var4) {
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

   public void translateToHand(float var1, HumanoidArm var2) {
      this.getArm(var2).translateTo(0.0625F);
   }

   // $FF: synthetic method
   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim((AbstractIllager)var1, var2, var3, var4, var5, var6, var7);
   }

   // $FF: synthetic method
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.render((AbstractIllager)var1, var2, var3, var4, var5, var6, var7);
   }
}
