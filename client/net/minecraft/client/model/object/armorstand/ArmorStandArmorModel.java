package net.minecraft.client.model.object.armorstand;

import net.minecraft.client.model.HumanoidModel;
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
   public ArmorStandArmorModel(final ModelPart root) {
      super(root);
   }

   public static ArmorModelSet<LayerDefinition> createArmorLayerSet(final CubeDeformation innerDeformation, final CubeDeformation outerDeformation) {
      return createArmorMeshSet(ArmorStandArmorModel::createBaseMesh, ADULT_ARMOR_PARTS_PER_SLOT, innerDeformation, outerDeformation).<LayerDefinition>map((mesh) -> LayerDefinition.create(mesh, 64, 32));
   }

   private static MeshDefinition createBaseMesh(final CubeDeformation g) {
      MeshDefinition mesh = HumanoidModel.createMesh(g, 0.0F);
      PartDefinition root = mesh.getRoot();
      PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, g), PartPose.offset(0.0F, 1.0F, 0.0F));
      head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, g.extend(0.5F)), PartPose.ZERO);
      root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, g.extend(-0.1F)), PartPose.offset(-1.9F, 11.0F, 0.0F));
      root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, g.extend(-0.1F)), PartPose.offset(1.9F, 11.0F, 0.0F));
      return mesh;
   }

   public void setupAnim(final ArmorStandRenderState state) {
      super.setupAnim(state);
      this.head.xRot = 0.017453292F * state.headPose.x();
      this.head.yRot = 0.017453292F * state.headPose.y();
      this.head.zRot = 0.017453292F * state.headPose.z();
      this.body.xRot = 0.017453292F * state.bodyPose.x();
      this.body.yRot = 0.017453292F * state.bodyPose.y();
      this.body.zRot = 0.017453292F * state.bodyPose.z();
      this.leftArm.xRot = 0.017453292F * state.leftArmPose.x();
      this.leftArm.yRot = 0.017453292F * state.leftArmPose.y();
      this.leftArm.zRot = 0.017453292F * state.leftArmPose.z();
      this.rightArm.xRot = 0.017453292F * state.rightArmPose.x();
      this.rightArm.yRot = 0.017453292F * state.rightArmPose.y();
      this.rightArm.zRot = 0.017453292F * state.rightArmPose.z();
      this.leftLeg.xRot = 0.017453292F * state.leftLegPose.x();
      this.leftLeg.yRot = 0.017453292F * state.leftLegPose.y();
      this.leftLeg.zRot = 0.017453292F * state.leftLegPose.z();
      this.rightLeg.xRot = 0.017453292F * state.rightLegPose.x();
      this.rightLeg.yRot = 0.017453292F * state.rightLegPose.y();
      this.rightLeg.zRot = 0.017453292F * state.rightLegPose.z();
   }
}
