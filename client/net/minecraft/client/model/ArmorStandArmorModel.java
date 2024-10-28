package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ArmorStandArmorModel extends HumanoidModel<ArmorStand> {
   public ArmorStandArmorModel(ModelPart var1) {
      super(var1);
   }

   public static LayerDefinition createBodyLayer(CubeDeformation var0) {
      MeshDefinition var1 = HumanoidModel.createMesh(var0, 0.0F);
      PartDefinition var2 = var1.getRoot();
      var2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, var0), PartPose.offset(0.0F, 1.0F, 0.0F));
      var2.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, var0.extend(0.5F)), PartPose.offset(0.0F, 1.0F, 0.0F));
      var2.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0.extend(-0.1F)), PartPose.offset(-1.9F, 11.0F, 0.0F));
      var2.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0.extend(-0.1F)), PartPose.offset(1.9F, 11.0F, 0.0F));
      return LayerDefinition.create(var1, 64, 32);
   }

   public void setupAnim(ArmorStand var1, float var2, float var3, float var4, float var5, float var6) {
      this.head.xRot = 0.017453292F * var1.getHeadPose().getX();
      this.head.yRot = 0.017453292F * var1.getHeadPose().getY();
      this.head.zRot = 0.017453292F * var1.getHeadPose().getZ();
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
      this.rightLeg.xRot = 0.017453292F * var1.getRightLegPose().getX();
      this.rightLeg.yRot = 0.017453292F * var1.getRightLegPose().getY();
      this.rightLeg.zRot = 0.017453292F * var1.getRightLegPose().getZ();
      this.hat.copyFrom(this.head);
   }
}
