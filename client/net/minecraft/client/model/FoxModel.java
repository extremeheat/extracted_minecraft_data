package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Fox;

public class FoxModel<T extends Fox> extends AgeableListModel<T> {
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

   public FoxModel(ModelPart var1) {
      super(true, 8.0F, 3.35F);
      this.head = var1.getChild("head");
      this.body = var1.getChild("body");
      this.rightHindLeg = var1.getChild("right_hind_leg");
      this.leftHindLeg = var1.getChild("left_hind_leg");
      this.rightFrontLeg = var1.getChild("right_front_leg");
      this.leftFrontLeg = var1.getChild("left_front_leg");
      this.tail = this.body.getChild("tail");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(1, 5).addBox(-3.0F, -2.0F, -5.0F, 8.0F, 6.0F, 6.0F), PartPose.offset(-1.0F, 16.5F, -3.0F));
      var2.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(8, 1).addBox(-3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F), PartPose.ZERO);
      var2.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(15, 1).addBox(3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F), PartPose.ZERO);
      var2.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(6, 18).addBox(-1.0F, 2.01F, -8.0F, 4.0F, 2.0F, 3.0F), PartPose.ZERO);
      PartDefinition var3 = var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(24, 15).addBox(-3.0F, 3.999F, -3.5F, 6.0F, 11.0F, 6.0F), PartPose.offsetAndRotation(0.0F, 16.0F, -6.0F, 1.5707964F, 0.0F, 0.0F));
      CubeDeformation var4 = new CubeDeformation(0.001F);
      CubeListBuilder var5 = CubeListBuilder.create().texOffs(4, 24).addBox(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, var4);
      CubeListBuilder var6 = CubeListBuilder.create().texOffs(13, 24).addBox(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, var4);
      var1.addOrReplaceChild("right_hind_leg", var6, PartPose.offset(-5.0F, 17.5F, 7.0F));
      var1.addOrReplaceChild("left_hind_leg", var5, PartPose.offset(-1.0F, 17.5F, 7.0F));
      var1.addOrReplaceChild("right_front_leg", var6, PartPose.offset(-5.0F, 17.5F, 0.0F));
      var1.addOrReplaceChild("left_front_leg", var5, PartPose.offset(-1.0F, 17.5F, 0.0F));
      var3.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(30, 0).addBox(2.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F), PartPose.offsetAndRotation(-4.0F, 15.0F, -1.0F, -0.05235988F, 0.0F, 0.0F));
      return LayerDefinition.create(var0, 48, 32);
   }

   public void prepareMobModel(T var1, float var2, float var3, float var4) {
      this.body.xRot = 1.5707964F;
      this.tail.xRot = -0.05235988F;
      this.rightHindLeg.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
      this.leftHindLeg.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
      this.rightFrontLeg.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
      this.leftFrontLeg.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
      this.head.setPos(-1.0F, 16.5F, -3.0F);
      this.head.yRot = 0.0F;
      this.head.zRot = var1.getHeadRollAngle(var4);
      this.rightHindLeg.visible = true;
      this.leftHindLeg.visible = true;
      this.rightFrontLeg.visible = true;
      this.leftFrontLeg.visible = true;
      this.body.setPos(0.0F, 16.0F, -6.0F);
      this.body.zRot = 0.0F;
      this.rightHindLeg.setPos(-5.0F, 17.5F, 7.0F);
      this.leftHindLeg.setPos(-1.0F, 17.5F, 7.0F);
      if (var1.isCrouching()) {
         this.body.xRot = 1.6755161F;
         float var5 = var1.getCrouchAmount(var4);
         this.body.setPos(0.0F, 16.0F + var1.getCrouchAmount(var4), -6.0F);
         this.head.setPos(-1.0F, 16.5F + var5, -3.0F);
         this.head.yRot = 0.0F;
      } else if (var1.isSleeping()) {
         this.body.zRot = -1.5707964F;
         this.body.setPos(0.0F, 21.0F, -6.0F);
         this.tail.xRot = -2.6179938F;
         if (this.young) {
            this.tail.xRot = -2.1816616F;
            this.body.setPos(0.0F, 21.0F, -2.0F);
         }

         this.head.setPos(1.0F, 19.49F, -3.0F);
         this.head.xRot = 0.0F;
         this.head.yRot = -2.0943952F;
         this.head.zRot = 0.0F;
         this.rightHindLeg.visible = false;
         this.leftHindLeg.visible = false;
         this.rightFrontLeg.visible = false;
         this.leftFrontLeg.visible = false;
      } else if (var1.isSitting()) {
         this.body.xRot = 0.5235988F;
         this.body.setPos(0.0F, 9.0F, -3.0F);
         this.tail.xRot = 0.7853982F;
         this.tail.setPos(-4.0F, 15.0F, -2.0F);
         this.head.setPos(-1.0F, 10.0F, -0.25F);
         this.head.xRot = 0.0F;
         this.head.yRot = 0.0F;
         if (this.young) {
            this.head.setPos(-1.0F, 13.0F, -3.75F);
         }

         this.rightHindLeg.xRot = -1.3089969F;
         this.rightHindLeg.setPos(-5.0F, 21.5F, 6.75F);
         this.leftHindLeg.xRot = -1.3089969F;
         this.leftHindLeg.setPos(-1.0F, 21.5F, 6.75F);
         this.rightFrontLeg.xRot = -0.2617994F;
         this.leftFrontLeg.xRot = -0.2617994F;
      }

   }

   protected Iterable<ModelPart> headParts() {
      return ImmutableList.of(this.head);
   }

   protected Iterable<ModelPart> bodyParts() {
      return ImmutableList.of(this.body, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      if (!var1.isSleeping() && !var1.isFaceplanted() && !var1.isCrouching()) {
         this.head.xRot = var6 * 0.017453292F;
         this.head.yRot = var5 * 0.017453292F;
      }

      if (var1.isSleeping()) {
         this.head.xRot = 0.0F;
         this.head.yRot = -2.0943952F;
         this.head.zRot = Mth.cos(var4 * 0.027F) / 22.0F;
      }

      float var7;
      if (var1.isCrouching()) {
         var7 = Mth.cos(var4) * 0.01F;
         this.body.yRot = var7;
         this.rightHindLeg.zRot = var7;
         this.leftHindLeg.zRot = var7;
         this.rightFrontLeg.zRot = var7 / 2.0F;
         this.leftFrontLeg.zRot = var7 / 2.0F;
      }

      if (var1.isFaceplanted()) {
         var7 = 0.1F;
         this.legMotionPos += 0.67F;
         this.rightHindLeg.xRot = Mth.cos(this.legMotionPos * 0.4662F) * 0.1F;
         this.leftHindLeg.xRot = Mth.cos(this.legMotionPos * 0.4662F + 3.1415927F) * 0.1F;
         this.rightFrontLeg.xRot = Mth.cos(this.legMotionPos * 0.4662F + 3.1415927F) * 0.1F;
         this.leftFrontLeg.xRot = Mth.cos(this.legMotionPos * 0.4662F) * 0.1F;
      }

   }
}
