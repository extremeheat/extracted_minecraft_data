package net.minecraft.client.model.animal.axolotl;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.AxolotlRenderState;
import net.minecraft.util.Mth;

public class AdultAxolotlModel extends EntityModel<AxolotlRenderState> {
   private static final float SWIMMING_LEG_XROT = 1.8849558F;
   private final ModelPart tail;
   private final ModelPart leftHindLeg;
   private final ModelPart rightHindLeg;
   private final ModelPart leftFrontLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart body;
   private final ModelPart head;
   private final ModelPart topGills;
   private final ModelPart leftGills;
   private final ModelPart rightGills;

   public AdultAxolotlModel(final ModelPart root) {
      super(root);
      this.body = root.getChild("body");
      this.head = this.body.getChild("head");
      this.rightHindLeg = this.body.getChild("right_hind_leg");
      this.leftHindLeg = this.body.getChild("left_hind_leg");
      this.rightFrontLeg = this.body.getChild("right_front_leg");
      this.leftFrontLeg = this.body.getChild("left_front_leg");
      this.tail = this.body.getChild("tail");
      this.topGills = this.head.getChild("top_gills");
      this.leftGills = this.head.getChild("left_gills");
      this.rightGills = this.head.getChild("right_gills");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 11).addBox(-4.0F, -2.0F, -9.0F, 8.0F, 4.0F, 10.0F).texOffs(2, 17).addBox(0.0F, -3.0F, -8.0F, 0.0F, 5.0F, 9.0F), PartPose.offset(0.0F, 19.5F, 5.0F));
      CubeDeformation fudge = new CubeDeformation(0.001F);
      PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 1).addBox(-4.0F, -3.0F, -5.0F, 8.0F, 5.0F, 5.0F, fudge), PartPose.offset(0.0F, 0.0F, -9.0F));
      CubeListBuilder topGills = CubeListBuilder.create().texOffs(3, 37).addBox(-4.0F, -3.0F, 0.0F, 8.0F, 3.0F, 0.0F, fudge);
      CubeListBuilder leftGills = CubeListBuilder.create().texOffs(0, 40).addBox(-3.0F, -5.0F, 0.0F, 3.0F, 7.0F, 0.0F, fudge);
      CubeListBuilder rightGills = CubeListBuilder.create().texOffs(11, 40).addBox(0.0F, -5.0F, 0.0F, 3.0F, 7.0F, 0.0F, fudge);
      head.addOrReplaceChild("top_gills", topGills, PartPose.offset(0.0F, -3.0F, -1.0F));
      head.addOrReplaceChild("left_gills", leftGills, PartPose.offset(-4.0F, 0.0F, -1.0F));
      head.addOrReplaceChild("right_gills", rightGills, PartPose.offset(4.0F, 0.0F, -1.0F));
      CubeListBuilder leftLeg = CubeListBuilder.create().texOffs(2, 13).addBox(-1.0F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, fudge);
      CubeListBuilder rightLeg = CubeListBuilder.create().texOffs(2, 13).addBox(-2.0F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, fudge);
      body.addOrReplaceChild("right_hind_leg", rightLeg, PartPose.offset(-3.5F, 1.0F, -1.0F));
      body.addOrReplaceChild("left_hind_leg", leftLeg, PartPose.offset(3.5F, 1.0F, -1.0F));
      body.addOrReplaceChild("right_front_leg", rightLeg, PartPose.offset(-3.5F, 1.0F, -8.0F));
      body.addOrReplaceChild("left_front_leg", leftLeg, PartPose.offset(3.5F, 1.0F, -8.0F));
      body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(2, 19).addBox(0.0F, -3.0F, 0.0F, 0.0F, 5.0F, 12.0F), PartPose.offset(0.0F, 0.0F, 1.0F));
      return LayerDefinition.create(mesh, 64, 64);
   }

   public void setupAnim(final AxolotlRenderState state) {
      super.setupAnim(state);
      float playingDeadFactor = state.playingDeadFactor;
      float inWaterFactor = state.inWaterFactor;
      float onGroundFactor = state.onGroundFactor;
      float movingFactor = state.movingFactor;
      float notMovingFactor = 1.0F - movingFactor;
      float mirroredLegsFactor = 1.0F - Math.min(onGroundFactor, movingFactor);
      ModelPart var10000 = this.body;
      var10000.yRot += state.yRot * 0.017453292F;
      this.setupSwimmingAnimation(state.ageInTicks, state.xRot, Math.min(movingFactor, inWaterFactor));
      this.setupWaterHoveringAnimation(state.ageInTicks, Math.min(notMovingFactor, inWaterFactor));
      this.setupGroundCrawlingAnimation(state.ageInTicks, Math.min(movingFactor, onGroundFactor));
      this.setupLayStillOnGroundAnimation(state.ageInTicks, Math.min(notMovingFactor, onGroundFactor));
      this.setupPlayDeadAnimation(playingDeadFactor);
      this.applyMirrorLegRotations(mirroredLegsFactor);
   }

   private void setupLayStillOnGroundAnimation(final float ageInTicks, final float factor) {
      if (!(factor <= 1.0E-5F)) {
         float animMoveSpeed = ageInTicks * 0.09F;
         float sineSway = Mth.sin((double)animMoveSpeed);
         float cosineSway = Mth.cos((double)animMoveSpeed);
         float movement = sineSway * sineSway - 2.0F * sineSway;
         float movement2 = cosineSway * cosineSway - 3.0F * sineSway;
         ModelPart var10000 = this.head;
         var10000.xRot += -0.09F * movement * factor;
         var10000 = this.head;
         var10000.zRot += -0.2F * factor;
         var10000 = this.tail;
         var10000.yRot += (-0.1F + 0.1F * movement) * factor;
         float gillAngle = (0.6F + 0.05F * movement2) * factor;
         var10000 = this.topGills;
         var10000.xRot += gillAngle;
         var10000 = this.leftGills;
         var10000.yRot -= gillAngle;
         var10000 = this.rightGills;
         var10000.yRot += gillAngle;
         var10000 = this.leftHindLeg;
         var10000.xRot += 1.1F * factor;
         var10000 = this.leftHindLeg;
         var10000.yRot += 1.0F * factor;
         var10000 = this.leftFrontLeg;
         var10000.xRot += 0.8F * factor;
         var10000 = this.leftFrontLeg;
         var10000.yRot += 2.3F * factor;
         var10000 = this.leftFrontLeg;
         var10000.zRot -= 0.5F * factor;
      }
   }

   private void setupGroundCrawlingAnimation(final float ageInTicks, final float factor) {
      if (!(factor <= 1.0E-5F)) {
         float animMoveSpeed = ageInTicks * 0.11F;
         float cosineSway = Mth.cos((double)animMoveSpeed);
         float hindLegYRotSway = (cosineSway * cosineSway - 2.0F * cosineSway) / 5.0F;
         float frontLegYRotSway = 0.7F * cosineSway;
         float headAndTailYRot = 0.09F * cosineSway * factor;
         ModelPart var10000 = this.head;
         var10000.yRot += headAndTailYRot;
         var10000 = this.tail;
         var10000.yRot += headAndTailYRot;
         float gillAngle = (0.6F - 0.08F * (cosineSway * cosineSway + 2.0F * Mth.sin((double)animMoveSpeed))) * factor;
         var10000 = this.topGills;
         var10000.xRot += gillAngle;
         var10000 = this.leftGills;
         var10000.yRot -= gillAngle;
         var10000 = this.rightGills;
         var10000.yRot += gillAngle;
         float hindLegXRot = 0.9424779F * factor;
         float frontLegXRot = 1.0995574F * factor;
         var10000 = this.leftHindLeg;
         var10000.xRot += hindLegXRot;
         var10000 = this.leftHindLeg;
         var10000.yRot += (1.5F - hindLegYRotSway) * factor;
         var10000 = this.leftHindLeg;
         var10000.zRot += -0.1F * factor;
         var10000 = this.leftFrontLeg;
         var10000.xRot += frontLegXRot;
         var10000 = this.leftFrontLeg;
         var10000.yRot += (1.5707964F - frontLegYRotSway) * factor;
         var10000 = this.rightHindLeg;
         var10000.xRot += hindLegXRot;
         var10000 = this.rightHindLeg;
         var10000.yRot += (-1.0F - hindLegYRotSway) * factor;
         var10000 = this.rightFrontLeg;
         var10000.xRot += frontLegXRot;
         var10000 = this.rightFrontLeg;
         var10000.yRot += (-1.5707964F - frontLegYRotSway) * factor;
      }
   }

   private void setupWaterHoveringAnimation(final float ageInTicks, final float factor) {
      if (!(factor <= 1.0E-5F)) {
         float animMoveSpeed = ageInTicks * 0.075F;
         float cosineSway = Mth.cos((double)animMoveSpeed);
         float sineSway = Mth.sin((double)animMoveSpeed) * 0.15F;
         float bodyXRot = (-0.15F + 0.075F * cosineSway) * factor;
         ModelPart var10000 = this.body;
         var10000.xRot += bodyXRot;
         var10000 = this.body;
         var10000.y -= sineSway * factor;
         var10000 = this.head;
         var10000.xRot -= bodyXRot;
         var10000 = this.topGills;
         var10000.xRot += 0.2F * cosineSway * factor;
         float gillYRot = (-0.3F * cosineSway - 0.19F) * factor;
         var10000 = this.leftGills;
         var10000.yRot += gillYRot;
         var10000 = this.rightGills;
         var10000.yRot -= gillYRot;
         var10000 = this.leftHindLeg;
         var10000.xRot += (2.3561945F - cosineSway * 0.11F) * factor;
         var10000 = this.leftHindLeg;
         var10000.yRot += 0.47123894F * factor;
         var10000 = this.leftHindLeg;
         var10000.zRot += 1.7278761F * factor;
         var10000 = this.leftFrontLeg;
         var10000.xRot += (0.7853982F - cosineSway * 0.2F) * factor;
         var10000 = this.leftFrontLeg;
         var10000.yRot += 2.042035F * factor;
         var10000 = this.tail;
         var10000.yRot += 0.5F * cosineSway * factor;
      }
   }

   private void setupSwimmingAnimation(final float ageInTicks, final float xRot, final float factor) {
      if (!(factor <= 1.0E-5F)) {
         float animMoveSpeed = ageInTicks * 0.33F;
         float sineSway = Mth.sin((double)animMoveSpeed);
         float cosineSway = Mth.cos((double)animMoveSpeed);
         float bodySway = 0.13F * sineSway;
         ModelPart var10000 = this.body;
         var10000.xRot += (xRot * 0.017453292F + bodySway) * factor;
         var10000 = this.head;
         var10000.xRot -= bodySway * 1.8F * factor;
         var10000 = this.body;
         var10000.y -= 0.45F * cosineSway * factor;
         var10000 = this.topGills;
         var10000.xRot += (-0.5F * sineSway - 0.8F) * factor;
         float gillYRot = (0.3F * sineSway + 0.9F) * factor;
         var10000 = this.leftGills;
         var10000.yRot += gillYRot;
         var10000 = this.rightGills;
         var10000.yRot -= gillYRot;
         var10000 = this.tail;
         var10000.yRot += 0.3F * Mth.cos((double)(animMoveSpeed * 0.9F)) * factor;
         var10000 = this.leftHindLeg;
         var10000.xRot += 1.8849558F * factor;
         var10000 = this.leftHindLeg;
         var10000.yRot += -0.4F * sineSway * factor;
         var10000 = this.leftHindLeg;
         var10000.zRot += 1.5707964F * factor;
         var10000 = this.leftFrontLeg;
         var10000.xRot += 1.8849558F * factor;
         var10000 = this.leftFrontLeg;
         var10000.yRot += (-0.2F * cosineSway - 0.1F) * factor;
         var10000 = this.leftFrontLeg;
         var10000.zRot += 1.5707964F * factor;
      }
   }

   private void setupPlayDeadAnimation(final float factor) {
      if (!(factor <= 1.0E-5F)) {
         ModelPart var10000 = this.leftHindLeg;
         var10000.xRot += 1.4137167F * factor;
         var10000 = this.leftHindLeg;
         var10000.yRot += 1.0995574F * factor;
         var10000 = this.leftHindLeg;
         var10000.zRot += 0.7853982F * factor;
         var10000 = this.leftFrontLeg;
         var10000.xRot += 0.7853982F * factor;
         var10000 = this.leftFrontLeg;
         var10000.yRot += 2.042035F * factor;
         var10000 = this.body;
         var10000.xRot += -0.15F * factor;
         var10000 = this.body;
         var10000.zRot += 0.35F * factor;
      }
   }

   private void applyMirrorLegRotations(final float factor) {
      if (!(factor <= 1.0E-5F)) {
         ModelPart var10000 = this.rightHindLeg;
         var10000.xRot += this.leftHindLeg.xRot * factor;
         ModelPart var2 = this.rightHindLeg;
         var2.yRot += -this.leftHindLeg.yRot * factor;
         var2 = this.rightHindLeg;
         var2.zRot += -this.leftHindLeg.zRot * factor;
         var10000 = this.rightFrontLeg;
         var10000.xRot += this.leftFrontLeg.xRot * factor;
         var2 = this.rightFrontLeg;
         var2.yRot += -this.leftFrontLeg.yRot * factor;
         var2 = this.rightFrontLeg;
         var2.zRot += -this.leftFrontLeg.zRot * factor;
      }
   }
}
