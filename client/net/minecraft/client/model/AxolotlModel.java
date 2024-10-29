package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.AxolotlRenderState;
import net.minecraft.util.Mth;

public class AxolotlModel extends EntityModel<AxolotlRenderState> {
   public static final float SWIMMING_LEG_XROT = 1.8849558F;
   public static final MeshTransformer BABY_TRANSFORMER = MeshTransformer.scaling(0.5F);
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

   public AxolotlModel(ModelPart var1) {
      super(var1);
      this.body = var1.getChild("body");
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
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 11).addBox(-4.0F, -2.0F, -9.0F, 8.0F, 4.0F, 10.0F).texOffs(2, 17).addBox(0.0F, -3.0F, -8.0F, 0.0F, 5.0F, 9.0F), PartPose.offset(0.0F, 20.0F, 5.0F));
      CubeDeformation var3 = new CubeDeformation(0.001F);
      PartDefinition var4 = var2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 1).addBox(-4.0F, -3.0F, -5.0F, 8.0F, 5.0F, 5.0F, var3), PartPose.offset(0.0F, 0.0F, -9.0F));
      CubeListBuilder var5 = CubeListBuilder.create().texOffs(3, 37).addBox(-4.0F, -3.0F, 0.0F, 8.0F, 3.0F, 0.0F, var3);
      CubeListBuilder var6 = CubeListBuilder.create().texOffs(0, 40).addBox(-3.0F, -5.0F, 0.0F, 3.0F, 7.0F, 0.0F, var3);
      CubeListBuilder var7 = CubeListBuilder.create().texOffs(11, 40).addBox(0.0F, -5.0F, 0.0F, 3.0F, 7.0F, 0.0F, var3);
      var4.addOrReplaceChild("top_gills", var5, PartPose.offset(0.0F, -3.0F, -1.0F));
      var4.addOrReplaceChild("left_gills", var6, PartPose.offset(-4.0F, 0.0F, -1.0F));
      var4.addOrReplaceChild("right_gills", var7, PartPose.offset(4.0F, 0.0F, -1.0F));
      CubeListBuilder var8 = CubeListBuilder.create().texOffs(2, 13).addBox(-1.0F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, var3);
      CubeListBuilder var9 = CubeListBuilder.create().texOffs(2, 13).addBox(-2.0F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, var3);
      var2.addOrReplaceChild("right_hind_leg", var9, PartPose.offset(-3.5F, 1.0F, -1.0F));
      var2.addOrReplaceChild("left_hind_leg", var8, PartPose.offset(3.5F, 1.0F, -1.0F));
      var2.addOrReplaceChild("right_front_leg", var9, PartPose.offset(-3.5F, 1.0F, -8.0F));
      var2.addOrReplaceChild("left_front_leg", var8, PartPose.offset(3.5F, 1.0F, -8.0F));
      var2.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(2, 19).addBox(0.0F, -3.0F, 0.0F, 0.0F, 5.0F, 12.0F), PartPose.offset(0.0F, 0.0F, 1.0F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public void setupAnim(AxolotlRenderState var1) {
      super.setupAnim(var1);
      float var2 = var1.playingDeadFactor;
      float var3 = var1.inWaterFactor;
      float var4 = var1.onGroundFactor;
      float var5 = var1.movingFactor;
      float var6 = 1.0F - var5;
      float var7 = 1.0F - Math.min(var4, var5);
      ModelPart var10000 = this.body;
      var10000.yRot += var1.yRot * 0.017453292F;
      this.setupSwimmingAnimation(var1.ageInTicks, var1.xRot, Math.min(var5, var3));
      this.setupWaterHoveringAnimation(var1.ageInTicks, Math.min(var6, var3));
      this.setupGroundCrawlingAnimation(var1.ageInTicks, Math.min(var5, var4));
      this.setupLayStillOnGroundAnimation(var1.ageInTicks, Math.min(var6, var4));
      this.setupPlayDeadAnimation(var2);
      this.applyMirrorLegRotations(var7);
   }

   private void setupLayStillOnGroundAnimation(float var1, float var2) {
      if (!(var2 <= 1.0E-5F)) {
         float var3 = var1 * 0.09F;
         float var4 = Mth.sin(var3);
         float var5 = Mth.cos(var3);
         float var6 = var4 * var4 - 2.0F * var4;
         float var7 = var5 * var5 - 3.0F * var4;
         ModelPart var10000 = this.head;
         var10000.xRot += -0.09F * var6 * var2;
         var10000 = this.head;
         var10000.zRot += -0.2F * var2;
         var10000 = this.tail;
         var10000.yRot += (-0.1F + 0.1F * var6) * var2;
         float var8 = (0.6F + 0.05F * var7) * var2;
         var10000 = this.topGills;
         var10000.xRot += var8;
         var10000 = this.leftGills;
         var10000.yRot -= var8;
         var10000 = this.rightGills;
         var10000.yRot += var8;
         var10000 = this.leftHindLeg;
         var10000.xRot += 1.1F * var2;
         var10000 = this.leftHindLeg;
         var10000.yRot += 1.0F * var2;
         var10000 = this.leftFrontLeg;
         var10000.xRot += 0.8F * var2;
         var10000 = this.leftFrontLeg;
         var10000.yRot += 2.3F * var2;
         var10000 = this.leftFrontLeg;
         var10000.zRot -= 0.5F * var2;
      }
   }

   private void setupGroundCrawlingAnimation(float var1, float var2) {
      if (!(var2 <= 1.0E-5F)) {
         float var3 = var1 * 0.11F;
         float var4 = Mth.cos(var3);
         float var5 = (var4 * var4 - 2.0F * var4) / 5.0F;
         float var6 = 0.7F * var4;
         float var7 = 0.09F * var4 * var2;
         ModelPart var10000 = this.head;
         var10000.yRot += var7;
         var10000 = this.tail;
         var10000.yRot += var7;
         float var8 = (0.6F - 0.08F * (var4 * var4 + 2.0F * Mth.sin(var3))) * var2;
         var10000 = this.topGills;
         var10000.xRot += var8;
         var10000 = this.leftGills;
         var10000.yRot -= var8;
         var10000 = this.rightGills;
         var10000.yRot += var8;
         float var9 = 0.9424779F * var2;
         float var10 = 1.0995574F * var2;
         var10000 = this.leftHindLeg;
         var10000.xRot += var9;
         var10000 = this.leftHindLeg;
         var10000.yRot += (1.5F - var5) * var2;
         var10000 = this.leftHindLeg;
         var10000.zRot += -0.1F * var2;
         var10000 = this.leftFrontLeg;
         var10000.xRot += var10;
         var10000 = this.leftFrontLeg;
         var10000.yRot += (1.5707964F - var6) * var2;
         var10000 = this.rightHindLeg;
         var10000.xRot += var9;
         var10000 = this.rightHindLeg;
         var10000.yRot += (-1.0F - var5) * var2;
         var10000 = this.rightFrontLeg;
         var10000.xRot += var10;
         var10000 = this.rightFrontLeg;
         var10000.yRot += (-1.5707964F - var6) * var2;
      }
   }

   private void setupWaterHoveringAnimation(float var1, float var2) {
      if (!(var2 <= 1.0E-5F)) {
         float var3 = var1 * 0.075F;
         float var4 = Mth.cos(var3);
         float var5 = Mth.sin(var3) * 0.15F;
         float var6 = (-0.15F + 0.075F * var4) * var2;
         ModelPart var10000 = this.body;
         var10000.xRot += var6;
         var10000 = this.body;
         var10000.y -= var5 * var2;
         var10000 = this.head;
         var10000.xRot -= var6;
         var10000 = this.topGills;
         var10000.xRot += 0.2F * var4 * var2;
         float var7 = (-0.3F * var4 - 0.19F) * var2;
         var10000 = this.leftGills;
         var10000.yRot += var7;
         var10000 = this.rightGills;
         var10000.yRot -= var7;
         var10000 = this.leftHindLeg;
         var10000.xRot += (2.3561945F - var4 * 0.11F) * var2;
         var10000 = this.leftHindLeg;
         var10000.yRot += 0.47123894F * var2;
         var10000 = this.leftHindLeg;
         var10000.zRot += 1.7278761F * var2;
         var10000 = this.leftFrontLeg;
         var10000.xRot += (0.7853982F - var4 * 0.2F) * var2;
         var10000 = this.leftFrontLeg;
         var10000.yRot += 2.042035F * var2;
         var10000 = this.tail;
         var10000.yRot += 0.5F * var4 * var2;
      }
   }

   private void setupSwimmingAnimation(float var1, float var2, float var3) {
      if (!(var3 <= 1.0E-5F)) {
         float var4 = var1 * 0.33F;
         float var5 = Mth.sin(var4);
         float var6 = Mth.cos(var4);
         float var7 = 0.13F * var5;
         ModelPart var10000 = this.body;
         var10000.xRot += (var2 * 0.017453292F + var7) * var3;
         var10000 = this.head;
         var10000.xRot -= var7 * 1.8F * var3;
         var10000 = this.body;
         var10000.y -= 0.45F * var6 * var3;
         var10000 = this.topGills;
         var10000.xRot += (-0.5F * var5 - 0.8F) * var3;
         float var8 = (0.3F * var5 + 0.9F) * var3;
         var10000 = this.leftGills;
         var10000.yRot += var8;
         var10000 = this.rightGills;
         var10000.yRot -= var8;
         var10000 = this.tail;
         var10000.yRot += 0.3F * Mth.cos(var4 * 0.9F) * var3;
         var10000 = this.leftHindLeg;
         var10000.xRot += 1.8849558F * var3;
         var10000 = this.leftHindLeg;
         var10000.yRot += -0.4F * var5 * var3;
         var10000 = this.leftHindLeg;
         var10000.zRot += 1.5707964F * var3;
         var10000 = this.leftFrontLeg;
         var10000.xRot += 1.8849558F * var3;
         var10000 = this.leftFrontLeg;
         var10000.yRot += (-0.2F * var6 - 0.1F) * var3;
         var10000 = this.leftFrontLeg;
         var10000.zRot += 1.5707964F * var3;
      }
   }

   private void setupPlayDeadAnimation(float var1) {
      if (!(var1 <= 1.0E-5F)) {
         ModelPart var10000 = this.leftHindLeg;
         var10000.xRot += 1.4137167F * var1;
         var10000 = this.leftHindLeg;
         var10000.yRot += 1.0995574F * var1;
         var10000 = this.leftHindLeg;
         var10000.zRot += 0.7853982F * var1;
         var10000 = this.leftFrontLeg;
         var10000.xRot += 0.7853982F * var1;
         var10000 = this.leftFrontLeg;
         var10000.yRot += 2.042035F * var1;
         var10000 = this.body;
         var10000.xRot += -0.15F * var1;
         var10000 = this.body;
         var10000.zRot += 0.35F * var1;
      }
   }

   private void applyMirrorLegRotations(float var1) {
      if (!(var1 <= 1.0E-5F)) {
         ModelPart var10000 = this.rightHindLeg;
         var10000.xRot += this.leftHindLeg.xRot * var1;
         ModelPart var2 = this.rightHindLeg;
         var2.yRot += -this.leftHindLeg.yRot * var1;
         var2 = this.rightHindLeg;
         var2.zRot += -this.leftHindLeg.zRot * var1;
         var10000 = this.rightFrontLeg;
         var10000.xRot += this.leftFrontLeg.xRot * var1;
         var2 = this.rightFrontLeg;
         var2.yRot += -this.leftFrontLeg.yRot * var1;
         var2 = this.rightFrontLeg;
         var2.zRot += -this.leftFrontLeg.zRot * var1;
      }
   }
}
