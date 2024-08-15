package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;

public class ArmorStandArmorModel extends HumanoidModel<ArmorStandRenderState> {
   public ArmorStandArmorModel(ModelPart var1) {
      super(var1);
   }

   public static LayerDefinition createBodyLayer(CubeDeformation var0) {
      MeshDefinition var1 = HumanoidModel.createMesh(var0, 0.0F);
      PartDefinition var2 = var1.getRoot();
      var2.addOrReplaceChild(
         "head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, var0), PartPose.offset(0.0F, 1.0F, 0.0F)
      );
      var2.addOrReplaceChild(
         "hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, var0.extend(0.5F)), PartPose.offset(0.0F, 1.0F, 0.0F)
      );
      var2.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0.extend(-0.1F)),
         PartPose.offset(-1.9F, 11.0F, 0.0F)
      );
      var2.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0.extend(-0.1F)),
         PartPose.offset(1.9F, 11.0F, 0.0F)
      );
      return LayerDefinition.create(var1, 64, 32);
   }

   public void setupAnim(ArmorStandRenderState var1) {
      this.head.xRot = 0.017453292F * var1.headPose.getX();
      this.head.yRot = 0.017453292F * var1.headPose.getY();
      this.head.zRot = 0.017453292F * var1.headPose.getZ();
      this.body.xRot = 0.017453292F * var1.bodyPose.getX();
      this.body.yRot = 0.017453292F * var1.bodyPose.getY();
      this.body.zRot = 0.017453292F * var1.bodyPose.getZ();
      this.leftArm.xRot = 0.017453292F * var1.leftArmPose.getX();
      this.leftArm.yRot = 0.017453292F * var1.leftArmPose.getY();
      this.leftArm.zRot = 0.017453292F * var1.leftArmPose.getZ();
      this.rightArm.xRot = 0.017453292F * var1.rightArmPose.getX();
      this.rightArm.yRot = 0.017453292F * var1.rightArmPose.getY();
      this.rightArm.zRot = 0.017453292F * var1.rightArmPose.getZ();
      this.leftLeg.xRot = 0.017453292F * var1.leftLegPose.getX();
      this.leftLeg.yRot = 0.017453292F * var1.leftLegPose.getY();
      this.leftLeg.zRot = 0.017453292F * var1.leftLegPose.getZ();
      this.rightLeg.xRot = 0.017453292F * var1.rightLegPose.getX();
      this.rightLeg.yRot = 0.017453292F * var1.rightLegPose.getY();
      this.rightLeg.zRot = 0.017453292F * var1.rightLegPose.getZ();
   }
}
