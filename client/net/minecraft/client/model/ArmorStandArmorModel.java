package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;

public class ArmorStandArmorModel extends HumanoidModel<ArmorStandRenderState> {
   public ArmorStandArmorModel(ModelPart var1) {
      super(var1);
   }

   public static ArmorModelSet<LayerDefinition> createArmorLayerSet(CubeDeformation var0, CubeDeformation var1) {
      return createArmorMeshSet(ArmorStandArmorModel::createBaseMesh, var0, var1).<LayerDefinition>map((var0x) -> LayerDefinition.create(var0x, 64, 32));
   }

   private static MeshDefinition createBaseMesh(CubeDeformation var0) {
      MeshDefinition var1 = HumanoidModel.createMesh(var0, 0.0F);
      PartDefinition var2 = var1.getRoot();
      PartDefinition var3 = var2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, var0), PartPose.offset(0.0F, 1.0F, 0.0F));
      var3.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, var0.extend(0.5F)), PartPose.ZERO);
      var2.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0.extend(-0.1F)), PartPose.offset(-1.9F, 11.0F, 0.0F));
      var2.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var0.extend(-0.1F)), PartPose.offset(1.9F, 11.0F, 0.0F));
      return var1;
   }

   public void setupAnim(ArmorStandRenderState var1) {
      super.setupAnim(var1);
      this.head.xRot = 0.017453292F * var1.headPose.x();
      this.head.yRot = 0.017453292F * var1.headPose.y();
      this.head.zRot = 0.017453292F * var1.headPose.z();
      this.body.xRot = 0.017453292F * var1.bodyPose.x();
      this.body.yRot = 0.017453292F * var1.bodyPose.y();
      this.body.zRot = 0.017453292F * var1.bodyPose.z();
      this.leftArm.xRot = 0.017453292F * var1.leftArmPose.x();
      this.leftArm.yRot = 0.017453292F * var1.leftArmPose.y();
      this.leftArm.zRot = 0.017453292F * var1.leftArmPose.z();
      this.rightArm.xRot = 0.017453292F * var1.rightArmPose.x();
      this.rightArm.yRot = 0.017453292F * var1.rightArmPose.y();
      this.rightArm.zRot = 0.017453292F * var1.rightArmPose.z();
      this.leftLeg.xRot = 0.017453292F * var1.leftLegPose.x();
      this.leftLeg.yRot = 0.017453292F * var1.leftLegPose.y();
      this.leftLeg.zRot = 0.017453292F * var1.leftLegPose.z();
      this.rightLeg.xRot = 0.017453292F * var1.rightLegPose.x();
      this.rightLeg.yRot = 0.017453292F * var1.rightLegPose.y();
      this.rightLeg.zRot = 0.017453292F * var1.rightLegPose.z();
   }
}
