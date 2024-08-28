package net.minecraft.client.model;

import java.util.Set;
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

   public FoxModel(ModelPart var1) {
      super(var1);
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
      PartDefinition var2 = var1.addOrReplaceChild(
         "head", CubeListBuilder.create().texOffs(1, 5).addBox(-3.0F, -2.0F, -5.0F, 8.0F, 6.0F, 6.0F), PartPose.offset(-1.0F, 16.5F, -3.0F)
      );
      var2.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(8, 1).addBox(-3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F), PartPose.ZERO);
      var2.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(15, 1).addBox(3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F), PartPose.ZERO);
      var2.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(6, 18).addBox(-1.0F, 2.01F, -8.0F, 4.0F, 2.0F, 3.0F), PartPose.ZERO);
      PartDefinition var3 = var1.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(24, 15).addBox(-3.0F, 3.999F, -3.5F, 6.0F, 11.0F, 6.0F),
         PartPose.offsetAndRotation(0.0F, 16.0F, -6.0F, 1.5707964F, 0.0F, 0.0F)
      );
      CubeDeformation var4 = new CubeDeformation(0.001F);
      CubeListBuilder var5 = CubeListBuilder.create().texOffs(4, 24).addBox(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, var4);
      CubeListBuilder var6 = CubeListBuilder.create().texOffs(13, 24).addBox(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, var4);
      var1.addOrReplaceChild("right_hind_leg", var6, PartPose.offset(-5.0F, 17.5F, 7.0F));
      var1.addOrReplaceChild("left_hind_leg", var5, PartPose.offset(-1.0F, 17.5F, 7.0F));
      var1.addOrReplaceChild("right_front_leg", var6, PartPose.offset(-5.0F, 17.5F, 0.0F));
      var1.addOrReplaceChild("left_front_leg", var5, PartPose.offset(-1.0F, 17.5F, 0.0F));
      var3.addOrReplaceChild(
         "tail",
         CubeListBuilder.create().texOffs(30, 0).addBox(2.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F),
         PartPose.offsetAndRotation(-4.0F, 15.0F, -1.0F, -0.05235988F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(var0, 48, 32);
   }

   public void setupAnim(FoxRenderState var1) {
      super.setupAnim(var1);
      float var2 = var1.walkAnimationSpeed;
      float var3 = var1.walkAnimationPos;
      this.rightHindLeg.xRot = Mth.cos(var3 * 0.6662F) * 1.4F * var2;
      this.leftHindLeg.xRot = Mth.cos(var3 * 0.6662F + 3.1415927F) * 1.4F * var2;
      this.rightFrontLeg.xRot = Mth.cos(var3 * 0.6662F + 3.1415927F) * 1.4F * var2;
      this.leftFrontLeg.xRot = Mth.cos(var3 * 0.6662F) * 1.4F * var2;
      this.head.zRot = var1.headRollAngle;
      this.rightHindLeg.visible = true;
      this.leftHindLeg.visible = true;
      this.rightFrontLeg.visible = true;
      this.leftFrontLeg.visible = true;
      float var4 = var1.ageScale;
      if (var1.isCrouching) {
         this.body.xRot += 0.10471976F;
         float var5 = var1.crouchAmount;
         this.body.y += var5 * var4;
         this.head.y += var5 * var4;
      } else if (var1.isSleeping) {
         this.body.zRot = -1.5707964F;
         this.body.y += 5.0F * var4;
         this.tail.xRot = -2.6179938F;
         if (var1.isBaby) {
            this.tail.xRot = -2.1816616F;
            this.body.z += 2.0F;
         }

         this.head.x += 2.0F * var4;
         this.head.y += 2.99F * var4;
         this.head.yRot = -2.0943952F;
         this.head.zRot = 0.0F;
         this.rightHindLeg.visible = false;
         this.leftHindLeg.visible = false;
         this.rightFrontLeg.visible = false;
         this.leftFrontLeg.visible = false;
      } else if (var1.isSitting) {
         this.body.xRot = 0.5235988F;
         this.body.y -= 7.0F * var4;
         this.body.z += 3.0F * var4;
         this.tail.xRot = 0.7853982F;
         this.tail.z -= 1.0F * var4;
         this.head.xRot = 0.0F;
         this.head.yRot = 0.0F;
         if (var1.isBaby) {
            this.head.y--;
            this.head.z -= 0.375F;
         } else {
            this.head.y -= 6.5F;
            this.head.z += 2.75F;
         }

         this.rightHindLeg.xRot = -1.3089969F;
         this.rightHindLeg.y += 4.0F * var4;
         this.rightHindLeg.z -= 0.25F * var4;
         this.leftHindLeg.xRot = -1.3089969F;
         this.leftHindLeg.y += 4.0F * var4;
         this.leftHindLeg.z -= 0.25F * var4;
         this.rightFrontLeg.xRot = -0.2617994F;
         this.leftFrontLeg.xRot = -0.2617994F;
      }

      if (!var1.isSleeping && !var1.isFaceplanted && !var1.isCrouching) {
         this.head.xRot = var1.xRot * 0.017453292F;
         this.head.yRot = var1.yRot * 0.017453292F;
      }

      if (var1.isSleeping) {
         this.head.xRot = 0.0F;
         this.head.yRot = -2.0943952F;
         this.head.zRot = Mth.cos(var1.ageInTicks * 0.027F) / 22.0F;
      }

      if (var1.isCrouching) {
         float var6 = Mth.cos(var1.ageInTicks) * 0.01F;
         this.body.yRot = var6;
         this.rightHindLeg.zRot = var6;
         this.leftHindLeg.zRot = var6;
         this.rightFrontLeg.zRot = var6 / 2.0F;
         this.leftFrontLeg.zRot = var6 / 2.0F;
      }

      if (var1.isFaceplanted) {
         float var7 = 0.1F;
         this.legMotionPos += 0.67F;
         this.rightHindLeg.xRot = Mth.cos(this.legMotionPos * 0.4662F) * 0.1F;
         this.leftHindLeg.xRot = Mth.cos(this.legMotionPos * 0.4662F + 3.1415927F) * 0.1F;
         this.rightFrontLeg.xRot = Mth.cos(this.legMotionPos * 0.4662F + 3.1415927F) * 0.1F;
         this.leftFrontLeg.xRot = Mth.cos(this.legMotionPos * 0.4662F) * 0.1F;
      }
   }
}
