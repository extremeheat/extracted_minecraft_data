package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.AbstractIllager;

public class IllagerModel<T extends AbstractIllager> extends HierarchicalModel<T> implements ArmedModel, HeadedModel {
   private final ModelPart root;
   private final ModelPart head;
   private final ModelPart hat;
   private final ModelPart arms;
   private final ModelPart leftLeg;
   private final ModelPart rightLeg;
   private final ModelPart rightArm;
   private final ModelPart leftArm;

   public IllagerModel(ModelPart var1) {
      super();
      this.root = var1;
      this.head = var1.getChild("head");
      this.hat = this.head.getChild("hat");
      this.hat.visible = false;
      this.arms = var1.getChild("arms");
      this.leftLeg = var1.getChild("left_leg");
      this.rightLeg = var1.getChild("right_leg");
      this.leftArm = var1.getChild("left_arm");
      this.rightArm = var1.getChild("right_arm");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
      var2.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.45F)), PartPose.ZERO);
      var2.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F), PartPose.offset(0.0F, -2.0F, 0.0F));
      var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F).texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition var3 = var1.addOrReplaceChild("arms", CubeListBuilder.create().texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F).texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F), PartPose.offsetAndRotation(0.0F, 3.0F, -1.0F, -0.75F, 0.0F, 0.0F));
      var3.addOrReplaceChild("left_shoulder", CubeListBuilder.create().texOffs(44, 22).mirror().addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F), PartPose.ZERO);
      var1.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(-2.0F, 12.0F, 0.0F));
      var1.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(2.0F, 12.0F, 0.0F));
      var1.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(-5.0F, 2.0F, 0.0F));
      var1.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 46).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(5.0F, 2.0F, 0.0F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      this.head.yRot = var5 * 0.017453292F;
      this.head.xRot = var6 * 0.017453292F;
      if (this.riding) {
         this.rightArm.xRot = -0.62831855F;
         this.rightArm.yRot = 0.0F;
         this.rightArm.zRot = 0.0F;
         this.leftArm.xRot = -0.62831855F;
         this.leftArm.yRot = 0.0F;
         this.leftArm.zRot = 0.0F;
         this.rightLeg.xRot = -1.4137167F;
         this.rightLeg.yRot = 0.31415927F;
         this.rightLeg.zRot = 0.07853982F;
         this.leftLeg.xRot = -1.4137167F;
         this.leftLeg.yRot = -0.31415927F;
         this.leftLeg.zRot = -0.07853982F;
      } else {
         this.rightArm.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 2.0F * var3 * 0.5F;
         this.rightArm.yRot = 0.0F;
         this.rightArm.zRot = 0.0F;
         this.leftArm.xRot = Mth.cos(var2 * 0.6662F) * 2.0F * var3 * 0.5F;
         this.leftArm.yRot = 0.0F;
         this.leftArm.zRot = 0.0F;
         this.rightLeg.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3 * 0.5F;
         this.rightLeg.yRot = 0.0F;
         this.rightLeg.zRot = 0.0F;
         this.leftLeg.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3 * 0.5F;
         this.leftLeg.yRot = 0.0F;
         this.leftLeg.zRot = 0.0F;
      }

      AbstractIllager.IllagerArmPose var7 = var1.getArmPose();
      if (var7 == AbstractIllager.IllagerArmPose.ATTACKING) {
         if (var1.getMainHandItem().isEmpty()) {
            AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, true, this.attackTime, var4);
         } else {
            AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, var1, this.attackTime, var4);
         }
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
         AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
      } else if (var7 == AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE) {
         AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, var1, true);
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

      boolean var8 = var7 == AbstractIllager.IllagerArmPose.CROSSED;
      this.arms.visible = var8;
      this.leftArm.visible = !var8;
      this.rightArm.visible = !var8;
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
