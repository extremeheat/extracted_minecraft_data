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
import net.minecraft.client.renderer.entity.state.HoglinRenderState;
import net.minecraft.util.Mth;

public class HoglinModel extends EntityModel<HoglinRenderState> {
   public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(true, 8.0F, 6.0F, 1.9F, 2.0F, 24.0F, Set.of("head"));
   private static final float DEFAULT_HEAD_X_ROT = 0.87266463F;
   private static final float ATTACK_HEAD_X_ROT_END = -0.34906584F;
   private final ModelPart head;
   private final ModelPart rightEar;
   private final ModelPart leftEar;
   private final ModelPart body;
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart mane;

   public HoglinModel(ModelPart var1) {
      super(var1);
      this.body = var1.getChild("body");
      this.mane = this.body.getChild("mane");
      this.head = var1.getChild("head");
      this.rightEar = this.head.getChild("right_ear");
      this.leftEar = this.head.getChild("left_ear");
      this.rightFrontLeg = var1.getChild("right_front_leg");
      this.leftFrontLeg = var1.getChild("left_front_leg");
      this.rightHindLeg = var1.getChild("right_hind_leg");
      this.leftHindLeg = var1.getChild("left_hind_leg");
   }

   private static MeshDefinition createMesh() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild(
         "body", CubeListBuilder.create().texOffs(1, 1).addBox(-8.0F, -7.0F, -13.0F, 16.0F, 14.0F, 26.0F), PartPose.offset(0.0F, 7.0F, 0.0F)
      );
      var2.addOrReplaceChild(
         "mane",
         CubeListBuilder.create().texOffs(90, 33).addBox(0.0F, 0.0F, -9.0F, 0.0F, 10.0F, 19.0F, new CubeDeformation(0.001F)),
         PartPose.offset(0.0F, -14.0F, -7.0F)
      );
      PartDefinition var3 = var1.addOrReplaceChild(
         "head",
         CubeListBuilder.create().texOffs(61, 1).addBox(-7.0F, -3.0F, -19.0F, 14.0F, 6.0F, 19.0F),
         PartPose.offsetAndRotation(0.0F, 2.0F, -12.0F, 0.87266463F, 0.0F, 0.0F)
      );
      var3.addOrReplaceChild(
         "right_ear",
         CubeListBuilder.create().texOffs(1, 1).addBox(-6.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F),
         PartPose.offsetAndRotation(-6.0F, -2.0F, -3.0F, 0.0F, 0.0F, -0.6981317F)
      );
      var3.addOrReplaceChild(
         "left_ear",
         CubeListBuilder.create().texOffs(1, 6).addBox(0.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F),
         PartPose.offsetAndRotation(6.0F, -2.0F, -3.0F, 0.0F, 0.0F, 0.6981317F)
      );
      var3.addOrReplaceChild(
         "right_horn", CubeListBuilder.create().texOffs(10, 13).addBox(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F), PartPose.offset(-7.0F, 2.0F, -12.0F)
      );
      var3.addOrReplaceChild(
         "left_horn", CubeListBuilder.create().texOffs(1, 13).addBox(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F), PartPose.offset(7.0F, 2.0F, -12.0F)
      );
      byte var4 = 14;
      byte var5 = 11;
      var1.addOrReplaceChild(
         "right_front_leg", CubeListBuilder.create().texOffs(66, 42).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 14.0F, 6.0F), PartPose.offset(-4.0F, 10.0F, -8.5F)
      );
      var1.addOrReplaceChild(
         "left_front_leg", CubeListBuilder.create().texOffs(41, 42).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 14.0F, 6.0F), PartPose.offset(4.0F, 10.0F, -8.5F)
      );
      var1.addOrReplaceChild(
         "right_hind_leg", CubeListBuilder.create().texOffs(21, 45).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F), PartPose.offset(-5.0F, 13.0F, 10.0F)
      );
      var1.addOrReplaceChild(
         "left_hind_leg", CubeListBuilder.create().texOffs(0, 45).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F), PartPose.offset(5.0F, 13.0F, 10.0F)
      );
      return var0;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = createMesh();
      return LayerDefinition.create(var0, 128, 64);
   }

   public static LayerDefinition createBabyLayer() {
      MeshDefinition var0 = createMesh();
      PartDefinition var1 = var0.getRoot().getChild("body");
      var1.addOrReplaceChild(
         "mane",
         CubeListBuilder.create().texOffs(90, 33).addBox(0.0F, 0.0F, -9.0F, 0.0F, 10.0F, 19.0F, new CubeDeformation(0.001F)),
         PartPose.offset(0.0F, -14.0F, -3.0F)
      );
      return LayerDefinition.create(var0, 128, 64).apply(BABY_TRANSFORMER);
   }

   public void setupAnim(HoglinRenderState var1) {
      super.setupAnim(var1);
      float var2 = var1.walkAnimationSpeed;
      float var3 = var1.walkAnimationPos;
      this.rightEar.zRot = -0.6981317F - var2 * Mth.sin(var3);
      this.leftEar.zRot = 0.6981317F + var2 * Mth.sin(var3);
      this.head.yRot = var1.yRot * 0.017453292F;
      float var4 = 1.0F - (float)Mth.abs(10 - 2 * var1.attackAnimationRemainingTicks) / 10.0F;
      this.head.xRot = Mth.lerp(var4, 0.87266463F, -0.34906584F);
      if (var1.isBaby) {
         this.head.y += var4 * 2.5F;
      }

      float var5 = 1.2F;
      this.rightFrontLeg.xRot = Mth.cos(var3) * 1.2F * var2;
      this.leftFrontLeg.xRot = Mth.cos(var3 + 3.1415927F) * 1.2F * var2;
      this.rightHindLeg.xRot = this.leftFrontLeg.xRot;
      this.leftHindLeg.xRot = this.rightFrontLeg.xRot;
   }
}
