package net.minecraft.client.model.monster.zombie;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.util.Mth;

public class DrownedModel extends ZombieModel<ZombieRenderState> {
   public DrownedModel(final ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer(final CubeDeformation g) {
      MeshDefinition mesh = HumanoidModel.createMesh(g, 0.0F);
      PartDefinition root = mesh.getRoot();
      root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, g), PartPose.offset(5.0F, 2.0F, 0.0F));
      root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, g), PartPose.offset(1.9F, 12.0F, 0.0F));
      return LayerDefinition.create(mesh, 64, 64);
   }

   public void setupAnim(final ZombieRenderState state) {
      super.setupAnim(state);
      if (state.leftArmPose == HumanoidModel.ArmPose.THROW_TRIDENT) {
         this.leftArm.xRot = this.leftArm.xRot * 0.5F - 3.1415927F;
         this.leftArm.yRot = 0.0F;
      }

      if (state.rightArmPose == HumanoidModel.ArmPose.THROW_TRIDENT) {
         this.rightArm.xRot = this.rightArm.xRot * 0.5F - 3.1415927F;
         this.rightArm.yRot = 0.0F;
      }

      float swimAmount = state.swimAmount;
      if (swimAmount > 0.0F) {
         this.rightArm.xRot = Mth.rotLerpRad(swimAmount, this.rightArm.xRot, -2.5132742F) + swimAmount * 0.35F * Mth.sin((double)(0.1F * state.ageInTicks));
         this.leftArm.xRot = Mth.rotLerpRad(swimAmount, this.leftArm.xRot, -2.5132742F) - swimAmount * 0.35F * Mth.sin((double)(0.1F * state.ageInTicks));
         this.rightArm.zRot = Mth.rotLerpRad(swimAmount, this.rightArm.zRot, -0.15F);
         this.leftArm.zRot = Mth.rotLerpRad(swimAmount, this.leftArm.zRot, 0.15F);
         ModelPart var10000 = this.leftLeg;
         var10000.xRot -= swimAmount * 0.55F * Mth.sin((double)(0.1F * state.ageInTicks));
         var10000 = this.rightLeg;
         var10000.xRot += swimAmount * 0.55F * Mth.sin((double)(0.1F * state.ageInTicks));
         this.head.xRot = 0.0F;
      }

      if (state.attackTime > 0.0F) {
         AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, state.isAggressive, state);
         --this.rightArm.xRot;
         --this.leftArm.xRot;
      }

   }
}
