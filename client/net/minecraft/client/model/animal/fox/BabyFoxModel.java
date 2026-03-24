package net.minecraft.client.model.animal.fox;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.animation.definitions.FoxBabyAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.FoxRenderState;

public class BabyFoxModel extends FoxModel {
   private static final float MAX_WALK_ANIMATION_SPEED = 1.0F;
   private static final float WALK_ANIMATION_SCALE_FACTOR = 2.5F;
   private final KeyframeAnimation babyWalkAnimation;

   public BabyFoxModel(final ModelPart root) {
      super(root);
      this.babyWalkAnimation = FoxBabyAnimation.FOX_BABY_WALK.bake(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition root = meshdefinition.getRoot();
      root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.125F, -5.125F, 6.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(18, 20).addBox(-1.0F, 0.875F, -7.125F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(22, 8).addBox(-3.0F, -4.125F, -4.125F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(22, 11).addBox(1.0F, -4.125F, -4.125F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.125F, 0.125F));
      root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(22, 4).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 22.0F, 4.0F));
      root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(22, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 22.0F, 4.0F));
      root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(22, 4).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 22.0F, 0.0F));
      root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(22, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 22.0F, 0.0F));
      PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 10).addBox(-2.5F, -2.0F, -3.0F, 5.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 2.0F));
      body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 20).addBox(-1.5F, -1.48F, -1.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 3.0F));
      return LayerDefinition.create(meshdefinition, 32, 32);
   }

   protected void setSittingPose(final FoxRenderState state) {
      super.setSittingPose(state);
      this.body.xRot = -0.959931F;
      ModelPart var10000 = this.body;
      var10000.z -= 4.5F * state.ageScale;
      var10000 = this.body;
      var10000.y += 3.0F * state.ageScale;
      var10000 = this.tail;
      var10000.y -= 0.6F;
      var10000 = this.tail;
      var10000.z -= 2.0F * state.ageScale;
      this.tail.xRot = 0.95993114F;
      var10000 = this.head;
      var10000.y -= 0.75F;
      var10000 = this.head;
      var10000.z += 0.0F;
      this.rightFrontLeg.xRot = -0.2617994F;
      this.leftFrontLeg.xRot = -0.2617994F;
      --this.rightFrontLeg.z;
      --this.leftFrontLeg.z;
      var10000 = this.rightFrontLeg;
      var10000.x += 0.01F;
      var10000 = this.leftFrontLeg;
      var10000.x -= 0.01F;
      var10000 = this.rightHindLeg;
      var10000.z -= 3.75F;
      var10000 = this.leftHindLeg;
      var10000.z -= 3.75F;
      var10000 = this.rightHindLeg;
      var10000.x += 0.01F;
      var10000 = this.leftHindLeg;
      var10000.x -= 0.01F;
   }

   protected void setSleepingPose(final FoxRenderState state) {
      super.setSleepingPose(state);
      this.body.zRot = -1.5707964F;
      this.body.xRot = -0.17453292F;
      ++this.body.y;
      --this.body.z;
      --this.body.x;
      this.tail.xRot = -2.6179938F;
      this.tail.xRot = -2.1816616F;
      ModelPart var10000 = this.tail;
      var10000.x -= 0.7F;
      var10000 = this.tail;
      var10000.z += 0.6F;
      var10000 = this.tail;
      var10000.y += 0.9F;
      var10000 = this.head;
      var10000.x -= 2.0F;
      var10000 = this.head;
      var10000.y += 2.8F;
      var10000 = this.head;
      var10000.z -= 4.0F;
      this.head.yRot = -2.0943952F;
      this.head.zRot = 0.0F;
   }

   protected void setWalkingPose(final FoxRenderState state) {
      super.setWalkingPose(state);
      this.babyWalkAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 1.0F, 2.5F);
   }

   protected void setCrouchingPose(final FoxRenderState state) {
      super.setCrouchingPose(state);
      ModelPart var10000 = this.body;
      var10000.y += state.crouchAmount / 6.0F;
   }
}
