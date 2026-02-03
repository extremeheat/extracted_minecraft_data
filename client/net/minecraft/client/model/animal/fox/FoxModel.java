package net.minecraft.client.model.animal.fox;

import java.util.Set;
import net.minecraft.client.model.BabyModelTransform;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.FoxRenderState;
import net.minecraft.util.Mth;

public class FoxModel extends EntityModel<FoxRenderState> {
   public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(true, 8.0F, 3.35F, Set.of("head"));
   public final ModelPart head;
   private final ModelPart body;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;
   private final ModelPart tail;
   private static final int LEG_SIZE = 6;
   private static final float HEAD_HEIGHT = 16.5F;
   private static final float LEG_POS = 17.5F;
   private float legMotionPos;

   public FoxModel(final ModelPart root) {
      super(root);
      this.head = root.getChild("head");
      this.body = root.getChild("body");
      this.rightHindLeg = root.getChild("right_hind_leg");
      this.leftHindLeg = root.getChild("left_hind_leg");
      this.rightFrontLeg = root.getChild("right_front_leg");
      this.leftFrontLeg = root.getChild("left_front_leg");
      this.tail = this.body.getChild("tail");
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

   public void setupAnim(final FoxRenderState state) {
      super.setupAnim(state);
      float animationSpeed = state.walkAnimationSpeed;
      float animationPos = state.walkAnimationPos;
      this.rightHindLeg.xRot = Mth.cos((double)(animationPos * 0.6662F)) * 1.4F * animationSpeed;
      this.leftHindLeg.xRot = Mth.cos((double)(animationPos * 0.6662F + 3.1415927F)) * 1.4F * animationSpeed;
      this.rightFrontLeg.xRot = Mth.cos((double)(animationPos * 0.6662F + 3.1415927F)) * 1.4F * animationSpeed;
      this.leftFrontLeg.xRot = Mth.cos((double)(animationPos * 0.6662F)) * 1.4F * animationSpeed;
      this.head.zRot = state.headRollAngle;
      this.rightHindLeg.visible = true;
      this.leftHindLeg.visible = true;
      this.rightFrontLeg.visible = true;
      this.leftFrontLeg.visible = true;
      float ageScale = state.ageScale;
      if (state.isCrouching) {
         ModelPart var10000 = this.body;
         var10000.xRot += 0.10471976F;
         float crouch = state.crouchAmount;
         var10000 = this.body;
         var10000.y += crouch * ageScale;
         var10000 = this.head;
         var10000.y += crouch * ageScale;
      } else if (state.isSleeping) {
         this.body.zRot = -1.5707964F;
         ModelPart var10 = this.body;
         var10.y += 5.0F * ageScale;
         this.tail.xRot = -2.6179938F;
         if (state.isBaby) {
            this.tail.xRot = -2.1816616F;
            var10 = this.body;
            var10.z += 2.0F;
         }

         var10 = this.head;
         var10.x += 2.0F * ageScale;
         var10 = this.head;
         var10.y += 2.99F * ageScale;
         this.head.yRot = -2.0943952F;
         this.head.zRot = 0.0F;
         this.rightHindLeg.visible = false;
         this.leftHindLeg.visible = false;
         this.rightFrontLeg.visible = false;
         this.leftFrontLeg.visible = false;
      } else if (state.isSitting) {
         this.body.xRot = 0.5235988F;
         ModelPart var14 = this.body;
         var14.y -= 7.0F * ageScale;
         var14 = this.body;
         var14.z += 3.0F * ageScale;
         this.tail.xRot = 0.7853982F;
         var14 = this.tail;
         var14.z -= 1.0F * ageScale;
         this.head.xRot = 0.0F;
         this.head.yRot = 0.0F;
         if (state.isBaby) {
            --this.head.y;
            var14 = this.head;
            var14.z -= 0.375F;
         } else {
            var14 = this.head;
            var14.y -= 6.5F;
            var14 = this.head;
            var14.z += 2.75F;
         }

         this.rightHindLeg.xRot = -1.3089969F;
         var14 = this.rightHindLeg;
         var14.y += 4.0F * ageScale;
         var14 = this.rightHindLeg;
         var14.z -= 0.25F * ageScale;
         this.leftHindLeg.xRot = -1.3089969F;
         var14 = this.leftHindLeg;
         var14.y += 4.0F * ageScale;
         var14 = this.leftHindLeg;
         var14.z -= 0.25F * ageScale;
         this.rightFrontLeg.xRot = -0.2617994F;
         this.leftFrontLeg.xRot = -0.2617994F;
      }

      if (!state.isSleeping && !state.isFaceplanted && !state.isCrouching) {
         this.head.xRot = state.xRot * 0.017453292F;
         this.head.yRot = state.yRot * 0.017453292F;
      }

      if (state.isSleeping) {
         this.head.xRot = 0.0F;
         this.head.yRot = -2.0943952F;
         this.head.zRot = Mth.cos((double)(state.ageInTicks * 0.027F)) / 22.0F;
      }

      if (state.isCrouching) {
         float wiggleAmount = Mth.cos((double)state.ageInTicks) * 0.01F;
         this.body.yRot = wiggleAmount;
         this.rightHindLeg.zRot = wiggleAmount;
         this.leftHindLeg.zRot = wiggleAmount;
         this.rightFrontLeg.zRot = wiggleAmount / 2.0F;
         this.leftFrontLeg.zRot = wiggleAmount / 2.0F;
      }

      if (state.isFaceplanted) {
         float legMoveFactor = 0.1F;
         this.legMotionPos += 0.67F;
         this.rightHindLeg.xRot = Mth.cos((double)(this.legMotionPos * 0.4662F)) * 0.1F;
         this.leftHindLeg.xRot = Mth.cos((double)(this.legMotionPos * 0.4662F + 3.1415927F)) * 0.1F;
         this.rightFrontLeg.xRot = Mth.cos((double)(this.legMotionPos * 0.4662F + 3.1415927F)) * 0.1F;
         this.leftFrontLeg.xRot = Mth.cos((double)(this.legMotionPos * 0.4662F)) * 0.1F;
      }

   }
}
