package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LerpingModel;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import org.joml.Vector3f;

public class AxolotlModel<T extends Axolotl & LerpingModel> extends AgeableListModel<T> {
   public static final float SWIMMING_LEG_XROT = 1.8849558F;
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
      super(true, 8.0F, 3.35F);
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

   protected Iterable<ModelPart> headParts() {
      return ImmutableList.of();
   }

   protected Iterable<ModelPart> bodyParts() {
      return ImmutableList.of(this.body);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      this.setupInitialAnimationValues(var1, var5, var6);
      if (var1.isPlayingDead()) {
         this.setupPlayDeadAnimation(var5);
         this.saveAnimationValues(var1);
      } else {
         boolean var7 = var3 > 1.0E-5F || var1.getXRot() != var1.xRotO || var1.getYRot() != var1.yRotO;
         if (var1.isInWaterOrBubble()) {
            if (var7) {
               this.setupSwimmingAnimation(var4, var6);
            } else {
               this.setupWaterHoveringAnimation(var4);
            }

            this.saveAnimationValues(var1);
         } else {
            if (var1.onGround()) {
               if (var7) {
                  this.setupGroundCrawlingAnimation(var4, var5);
               } else {
                  this.setupLayStillOnGroundAnimation(var4, var5);
               }
            }

            this.saveAnimationValues(var1);
         }
      }
   }

   private void saveAnimationValues(T var1) {
      Map var2 = var1.getModelRotationValues();
      var2.put("body", this.getRotationVector(this.body));
      var2.put("head", this.getRotationVector(this.head));
      var2.put("right_hind_leg", this.getRotationVector(this.rightHindLeg));
      var2.put("left_hind_leg", this.getRotationVector(this.leftHindLeg));
      var2.put("right_front_leg", this.getRotationVector(this.rightFrontLeg));
      var2.put("left_front_leg", this.getRotationVector(this.leftFrontLeg));
      var2.put("tail", this.getRotationVector(this.tail));
      var2.put("top_gills", this.getRotationVector(this.topGills));
      var2.put("left_gills", this.getRotationVector(this.leftGills));
      var2.put("right_gills", this.getRotationVector(this.rightGills));
   }

   private Vector3f getRotationVector(ModelPart var1) {
      return new Vector3f(var1.xRot, var1.yRot, var1.zRot);
   }

   private void setRotationFromVector(ModelPart var1, Vector3f var2) {
      var1.setRotation(var2.x(), var2.y(), var2.z());
   }

   private void setupInitialAnimationValues(T var1, float var2, float var3) {
      this.body.x = 0.0F;
      this.head.y = 0.0F;
      this.body.y = 20.0F;
      Map var4 = var1.getModelRotationValues();
      if (var4.isEmpty()) {
         this.body.setRotation(var3 * 0.017453292F, var2 * 0.017453292F, 0.0F);
         this.head.setRotation(0.0F, 0.0F, 0.0F);
         this.leftHindLeg.setRotation(0.0F, 0.0F, 0.0F);
         this.rightHindLeg.setRotation(0.0F, 0.0F, 0.0F);
         this.leftFrontLeg.setRotation(0.0F, 0.0F, 0.0F);
         this.rightFrontLeg.setRotation(0.0F, 0.0F, 0.0F);
         this.leftGills.setRotation(0.0F, 0.0F, 0.0F);
         this.rightGills.setRotation(0.0F, 0.0F, 0.0F);
         this.topGills.setRotation(0.0F, 0.0F, 0.0F);
         this.tail.setRotation(0.0F, 0.0F, 0.0F);
      } else {
         this.setRotationFromVector(this.body, (Vector3f)var4.get("body"));
         this.setRotationFromVector(this.head, (Vector3f)var4.get("head"));
         this.setRotationFromVector(this.leftHindLeg, (Vector3f)var4.get("left_hind_leg"));
         this.setRotationFromVector(this.rightHindLeg, (Vector3f)var4.get("right_hind_leg"));
         this.setRotationFromVector(this.leftFrontLeg, (Vector3f)var4.get("left_front_leg"));
         this.setRotationFromVector(this.rightFrontLeg, (Vector3f)var4.get("right_front_leg"));
         this.setRotationFromVector(this.leftGills, (Vector3f)var4.get("left_gills"));
         this.setRotationFromVector(this.rightGills, (Vector3f)var4.get("right_gills"));
         this.setRotationFromVector(this.topGills, (Vector3f)var4.get("top_gills"));
         this.setRotationFromVector(this.tail, (Vector3f)var4.get("tail"));
      }

   }

   private float lerpTo(float var1, float var2) {
      return this.lerpTo(0.05F, var1, var2);
   }

   private float lerpTo(float var1, float var2, float var3) {
      return Mth.rotLerp(var1, var2, var3);
   }

   private void lerpPart(ModelPart var1, float var2, float var3, float var4) {
      var1.setRotation(this.lerpTo(var1.xRot, var2), this.lerpTo(var1.yRot, var3), this.lerpTo(var1.zRot, var4));
   }

   private void setupLayStillOnGroundAnimation(float var1, float var2) {
      float var3 = var1 * 0.09F;
      float var4 = Mth.sin(var3);
      float var5 = Mth.cos(var3);
      float var6 = var4 * var4 - 2.0F * var4;
      float var7 = var5 * var5 - 3.0F * var4;
      this.head.xRot = this.lerpTo(this.head.xRot, -0.09F * var6);
      this.head.yRot = this.lerpTo(this.head.yRot, 0.0F);
      this.head.zRot = this.lerpTo(this.head.zRot, -0.2F);
      this.tail.yRot = this.lerpTo(this.tail.yRot, -0.1F + 0.1F * var6);
      this.topGills.xRot = this.lerpTo(this.topGills.xRot, 0.6F + 0.05F * var7);
      this.leftGills.yRot = this.lerpTo(this.leftGills.yRot, -this.topGills.xRot);
      this.rightGills.yRot = this.lerpTo(this.rightGills.yRot, -this.leftGills.yRot);
      this.lerpPart(this.leftHindLeg, 1.1F, 1.0F, 0.0F);
      this.lerpPart(this.leftFrontLeg, 0.8F, 2.3F, -0.5F);
      this.applyMirrorLegRotations();
      this.body.xRot = this.lerpTo(0.2F, this.body.xRot, 0.0F);
      this.body.yRot = this.lerpTo(this.body.yRot, var2 * 0.017453292F);
      this.body.zRot = this.lerpTo(this.body.zRot, 0.0F);
   }

   private void setupGroundCrawlingAnimation(float var1, float var2) {
      float var3 = var1 * 0.11F;
      float var4 = Mth.cos(var3);
      float var5 = (var4 * var4 - 2.0F * var4) / 5.0F;
      float var6 = 0.7F * var4;
      this.head.xRot = this.lerpTo(this.head.xRot, 0.0F);
      this.head.yRot = this.lerpTo(this.head.yRot, 0.09F * var4);
      this.head.zRot = this.lerpTo(this.head.zRot, 0.0F);
      this.tail.yRot = this.lerpTo(this.tail.yRot, this.head.yRot);
      this.topGills.xRot = this.lerpTo(this.topGills.xRot, 0.6F - 0.08F * (var4 * var4 + 2.0F * Mth.sin(var3)));
      this.leftGills.yRot = this.lerpTo(this.leftGills.yRot, -this.topGills.xRot);
      this.rightGills.yRot = this.lerpTo(this.rightGills.yRot, -this.leftGills.yRot);
      this.lerpPart(this.leftHindLeg, 0.9424779F, 1.5F - var5, -0.1F);
      this.lerpPart(this.leftFrontLeg, 1.0995574F, 1.5707964F - var6, 0.0F);
      this.lerpPart(this.rightHindLeg, this.leftHindLeg.xRot, -1.0F - var5, 0.0F);
      this.lerpPart(this.rightFrontLeg, this.leftFrontLeg.xRot, -1.5707964F - var6, 0.0F);
      this.body.xRot = this.lerpTo(0.2F, this.body.xRot, 0.0F);
      this.body.yRot = this.lerpTo(this.body.yRot, var2 * 0.017453292F);
      this.body.zRot = this.lerpTo(this.body.zRot, 0.0F);
   }

   private void setupWaterHoveringAnimation(float var1) {
      float var2 = var1 * 0.075F;
      float var3 = Mth.cos(var2);
      float var4 = Mth.sin(var2) * 0.15F;
      this.body.xRot = this.lerpTo(this.body.xRot, -0.15F + 0.075F * var3);
      ModelPart var10000 = this.body;
      var10000.y -= var4;
      this.head.xRot = this.lerpTo(this.head.xRot, -this.body.xRot);
      this.topGills.xRot = this.lerpTo(this.topGills.xRot, 0.2F * var3);
      this.leftGills.yRot = this.lerpTo(this.leftGills.yRot, -0.3F * var3 - 0.19F);
      this.rightGills.yRot = this.lerpTo(this.rightGills.yRot, -this.leftGills.yRot);
      this.lerpPart(this.leftHindLeg, 2.3561945F - var3 * 0.11F, 0.47123894F, 1.7278761F);
      this.lerpPart(this.leftFrontLeg, 0.7853982F - var3 * 0.2F, 2.042035F, 0.0F);
      this.applyMirrorLegRotations();
      this.tail.yRot = this.lerpTo(this.tail.yRot, 0.5F * var3);
      this.head.yRot = this.lerpTo(this.head.yRot, 0.0F);
      this.head.zRot = this.lerpTo(this.head.zRot, 0.0F);
   }

   private void setupSwimmingAnimation(float var1, float var2) {
      float var3 = var1 * 0.33F;
      float var4 = Mth.sin(var3);
      float var5 = Mth.cos(var3);
      float var6 = 0.13F * var4;
      this.body.xRot = this.lerpTo(0.1F, this.body.xRot, var2 * 0.017453292F + var6);
      this.head.xRot = -var6 * 1.8F;
      ModelPart var10000 = this.body;
      var10000.y -= 0.45F * var5;
      this.topGills.xRot = this.lerpTo(this.topGills.xRot, -0.5F * var4 - 0.8F);
      this.leftGills.yRot = this.lerpTo(this.leftGills.yRot, 0.3F * var4 + 0.9F);
      this.rightGills.yRot = this.lerpTo(this.rightGills.yRot, -this.leftGills.yRot);
      this.tail.yRot = this.lerpTo(this.tail.yRot, 0.3F * Mth.cos(var3 * 0.9F));
      this.lerpPart(this.leftHindLeg, 1.8849558F, -0.4F * var4, 1.5707964F);
      this.lerpPart(this.leftFrontLeg, 1.8849558F, -0.2F * var5 - 0.1F, 1.5707964F);
      this.applyMirrorLegRotations();
      this.head.yRot = this.lerpTo(this.head.yRot, 0.0F);
      this.head.zRot = this.lerpTo(this.head.zRot, 0.0F);
   }

   private void setupPlayDeadAnimation(float var1) {
      this.lerpPart(this.leftHindLeg, 1.4137167F, 1.0995574F, 0.7853982F);
      this.lerpPart(this.leftFrontLeg, 0.7853982F, 2.042035F, 0.0F);
      this.body.xRot = this.lerpTo(this.body.xRot, -0.15F);
      this.body.zRot = this.lerpTo(this.body.zRot, 0.35F);
      this.applyMirrorLegRotations();
      this.body.yRot = this.lerpTo(this.body.yRot, var1 * 0.017453292F);
      this.head.xRot = this.lerpTo(this.head.xRot, 0.0F);
      this.head.yRot = this.lerpTo(this.head.yRot, 0.0F);
      this.head.zRot = this.lerpTo(this.head.zRot, 0.0F);
      this.tail.yRot = this.lerpTo(this.tail.yRot, 0.0F);
      this.lerpPart(this.topGills, 0.0F, 0.0F, 0.0F);
      this.lerpPart(this.leftGills, 0.0F, 0.0F, 0.0F);
      this.lerpPart(this.rightGills, 0.0F, 0.0F, 0.0F);
   }

   private void applyMirrorLegRotations() {
      this.lerpPart(this.rightHindLeg, this.leftHindLeg.xRot, -this.leftHindLeg.yRot, -this.leftHindLeg.zRot);
      this.lerpPart(this.rightFrontLeg, this.leftFrontLeg.xRot, -this.leftFrontLeg.yRot, -this.leftFrontLeg.zRot);
   }
}
