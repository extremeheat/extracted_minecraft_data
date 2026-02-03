package net.minecraft.client.model.animal.fox;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.FoxRenderState;
import net.minecraft.util.Mth;

public class AdultFoxModel extends FoxModel {
   private static final int LEG_SIZE = 6;
   private static final float HEAD_HEIGHT = 16.5F;
   private static final float LEG_POS = 17.5F;

   public AdultFoxModel(final ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(1, 5).addBox(-3.0F, -2.0F, -5.0F, 8.0F, 6.0F, 6.0F), PartPose.offset(-1.0F, 16.5F, -3.0F));
      head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(8, 1).addBox(-3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F), PartPose.ZERO);
      head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(15, 1).addBox(3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F), PartPose.ZERO);
      head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(6, 18).addBox(-1.0F, 2.01F, -8.0F, 4.0F, 2.0F, 3.0F), PartPose.ZERO);
      PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(24, 15).addBox(-3.0F, 3.999F, -3.5F, 6.0F, 11.0F, 6.0F), PartPose.offsetAndRotation(0.0F, 16.0F, -6.0F, 1.5707964F, 0.0F, 0.0F));
      CubeDeformation fudge = new CubeDeformation(0.001F);
      CubeListBuilder leftLeg = CubeListBuilder.create().texOffs(4, 24).addBox(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, fudge);
      CubeListBuilder rightLeg = CubeListBuilder.create().texOffs(13, 24).addBox(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, fudge);
      root.addOrReplaceChild("right_hind_leg", rightLeg, PartPose.offset(-5.0F, 17.5F, 7.0F));
      root.addOrReplaceChild("left_hind_leg", leftLeg, PartPose.offset(-1.0F, 17.5F, 7.0F));
      root.addOrReplaceChild("right_front_leg", rightLeg, PartPose.offset(-5.0F, 17.5F, 0.0F));
      root.addOrReplaceChild("left_front_leg", leftLeg, PartPose.offset(-1.0F, 17.5F, 0.0F));
      body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(30, 0).addBox(2.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F), PartPose.offsetAndRotation(-4.0F, 15.0F, -1.0F, -0.05235988F, 0.0F, 0.0F));
      return LayerDefinition.create(mesh, 48, 32);
   }

   protected void setSittingPose(final FoxRenderState state) {
      super.setSittingPose(state);
      this.body.xRot = 0.5235988F;
      ModelPart var10000 = this.body;
      var10000.y -= 7.0F;
      var10000 = this.body;
      var10000.z += 3.0F;
      this.tail.xRot = 0.7853982F;
      var10000 = this.head;
      var10000.y -= 6.5F;
      var10000 = this.head;
      var10000.z += 2.75F;
      this.rightFrontLeg.xRot = -0.2617994F;
      this.leftFrontLeg.xRot = -0.2617994F;
      this.rightHindLeg.xRot = -1.3089969F;
      var10000 = this.rightHindLeg;
      var10000.y += 4.0F;
      var10000 = this.rightHindLeg;
      var10000.z -= 0.25F;
      this.leftHindLeg.xRot = -1.3089969F;
      var10000 = this.leftHindLeg;
      var10000.y += 4.0F;
      var10000 = this.leftHindLeg;
      var10000.z -= 0.25F;
      --this.tail.z;
   }

   protected void setSleepingPose(final FoxRenderState state) {
      super.setSleepingPose(state);
      this.body.zRot = -1.5707964F;
      ModelPart var10000 = this.body;
      var10000.y += 5.0F;
      this.tail.xRot = -2.6179938F;
      var10000 = this.head;
      var10000.x += 2.0F;
      var10000 = this.head;
      var10000.y += 2.99F;
      this.head.yRot = -2.0943952F;
      this.head.zRot = 0.0F;
   }

   protected void setWalkingPose(final FoxRenderState state) {
      super.setWalkingPose(state);
      float animationSpeed = state.walkAnimationSpeed;
      float animationPos = state.walkAnimationPos;
      this.rightHindLeg.xRot = Mth.cos((double)(animationPos * 0.6662F)) * 1.4F * animationSpeed;
      this.leftHindLeg.xRot = Mth.cos((double)(animationPos * 0.6662F + 3.1415927F)) * 1.4F * animationSpeed;
      this.rightFrontLeg.xRot = Mth.cos((double)(animationPos * 0.6662F + 3.1415927F)) * 1.4F * animationSpeed;
      this.leftFrontLeg.xRot = Mth.cos((double)(animationPos * 0.6662F)) * 1.4F * animationSpeed;
   }

   protected void setCrouchingPose(final FoxRenderState state) {
      super.setCrouchingPose(state);
      ModelPart var10000 = this.body;
      var10000.y += state.crouchAmount;
   }

   protected void setPouncingPose(final FoxRenderState state) {
      super.setPouncingPose(state);
      float crouch = state.crouchAmount / 2.0F;
      ModelPart var10000 = this.body;
      var10000.y -= crouch;
      var10000 = this.head;
      var10000.y -= crouch;
   }
}
