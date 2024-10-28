package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Rabbit;

public class RabbitModel<T extends Rabbit> extends EntityModel<T> {
   private static final float REAR_JUMP_ANGLE = 50.0F;
   private static final float FRONT_JUMP_ANGLE = -40.0F;
   private static final String LEFT_HAUNCH = "left_haunch";
   private static final String RIGHT_HAUNCH = "right_haunch";
   private final ModelPart leftRearFoot;
   private final ModelPart rightRearFoot;
   private final ModelPart leftHaunch;
   private final ModelPart rightHaunch;
   private final ModelPart body;
   private final ModelPart leftFrontLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart head;
   private final ModelPart rightEar;
   private final ModelPart leftEar;
   private final ModelPart tail;
   private final ModelPart nose;
   private float jumpRotation;
   private static final float NEW_SCALE = 0.6F;

   public RabbitModel(ModelPart var1) {
      super();
      this.leftRearFoot = var1.getChild("left_hind_foot");
      this.rightRearFoot = var1.getChild("right_hind_foot");
      this.leftHaunch = var1.getChild("left_haunch");
      this.rightHaunch = var1.getChild("right_haunch");
      this.body = var1.getChild("body");
      this.leftFrontLeg = var1.getChild("left_front_leg");
      this.rightFrontLeg = var1.getChild("right_front_leg");
      this.head = var1.getChild("head");
      this.rightEar = var1.getChild("right_ear");
      this.leftEar = var1.getChild("left_ear");
      this.tail = var1.getChild("tail");
      this.nose = var1.getChild("nose");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("left_hind_foot", CubeListBuilder.create().texOffs(26, 24).addBox(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F), PartPose.offset(3.0F, 17.5F, 3.7F));
      var1.addOrReplaceChild("right_hind_foot", CubeListBuilder.create().texOffs(8, 24).addBox(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F), PartPose.offset(-3.0F, 17.5F, 3.7F));
      var1.addOrReplaceChild("left_haunch", CubeListBuilder.create().texOffs(30, 15).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F), PartPose.offsetAndRotation(3.0F, 17.5F, 3.7F, -0.34906584F, 0.0F, 0.0F));
      var1.addOrReplaceChild("right_haunch", CubeListBuilder.create().texOffs(16, 15).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F), PartPose.offsetAndRotation(-3.0F, 17.5F, 3.7F, -0.34906584F, 0.0F, 0.0F));
      var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -10.0F, 6.0F, 5.0F, 10.0F), PartPose.offsetAndRotation(0.0F, 19.0F, 8.0F, -0.34906584F, 0.0F, 0.0F));
      var1.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(8, 15).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F), PartPose.offsetAndRotation(3.0F, 17.0F, -1.0F, -0.17453292F, 0.0F, 0.0F));
      var1.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 15).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F), PartPose.offsetAndRotation(-3.0F, 17.0F, -1.0F, -0.17453292F, 0.0F, 0.0F));
      var1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 0).addBox(-2.5F, -4.0F, -5.0F, 5.0F, 4.0F, 5.0F), PartPose.offset(0.0F, 16.0F, -1.0F));
      var1.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(52, 0).addBox(-2.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 16.0F, -1.0F, 0.0F, -0.2617994F, 0.0F));
      var1.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(58, 0).addBox(0.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 16.0F, -1.0F, 0.0F, 0.2617994F, 0.0F));
      var1.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(52, 6).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 20.0F, 7.0F, -0.3490659F, 0.0F, 0.0F));
      var1.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(32, 9).addBox(-0.5F, -2.5F, -5.5F, 1.0F, 1.0F, 1.0F), PartPose.offset(0.0F, 16.0F, -1.0F));
      return LayerDefinition.create(var0, 64, 32);
   }

   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, int var5) {
      if (this.young) {
         float var6 = 1.5F;
         var1.pushPose();
         var1.scale(0.56666666F, 0.56666666F, 0.56666666F);
         var1.translate(0.0F, 1.375F, 0.125F);
         ImmutableList.of(this.head, this.leftEar, this.rightEar, this.nose).forEach((var5x) -> {
            var5x.render(var1, var2, var3, var4, var5);
         });
         var1.popPose();
         var1.pushPose();
         var1.scale(0.4F, 0.4F, 0.4F);
         var1.translate(0.0F, 2.25F, 0.0F);
         ImmutableList.of(this.leftRearFoot, this.rightRearFoot, this.leftHaunch, this.rightHaunch, this.body, this.leftFrontLeg, this.rightFrontLeg, this.tail).forEach((var5x) -> {
            var5x.render(var1, var2, var3, var4, var5);
         });
         var1.popPose();
      } else {
         var1.pushPose();
         var1.scale(0.6F, 0.6F, 0.6F);
         var1.translate(0.0F, 1.0F, 0.0F);
         ImmutableList.of(this.leftRearFoot, this.rightRearFoot, this.leftHaunch, this.rightHaunch, this.body, this.leftFrontLeg, this.rightFrontLeg, this.head, this.rightEar, this.leftEar, this.tail, this.nose, new ModelPart[0]).forEach((var5x) -> {
            var5x.render(var1, var2, var3, var4, var5);
         });
         var1.popPose();
      }

   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = var4 - (float)var1.tickCount;
      this.nose.xRot = var6 * 0.017453292F;
      this.head.xRot = var6 * 0.017453292F;
      this.rightEar.xRot = var6 * 0.017453292F;
      this.leftEar.xRot = var6 * 0.017453292F;
      this.nose.yRot = var5 * 0.017453292F;
      this.head.yRot = var5 * 0.017453292F;
      this.rightEar.yRot = this.nose.yRot - 0.2617994F;
      this.leftEar.yRot = this.nose.yRot + 0.2617994F;
      this.jumpRotation = Mth.sin(var1.getJumpCompletion(var7) * 3.1415927F);
      this.leftHaunch.xRot = (this.jumpRotation * 50.0F - 21.0F) * 0.017453292F;
      this.rightHaunch.xRot = (this.jumpRotation * 50.0F - 21.0F) * 0.017453292F;
      this.leftRearFoot.xRot = this.jumpRotation * 50.0F * 0.017453292F;
      this.rightRearFoot.xRot = this.jumpRotation * 50.0F * 0.017453292F;
      this.leftFrontLeg.xRot = (this.jumpRotation * -40.0F - 11.0F) * 0.017453292F;
      this.rightFrontLeg.xRot = (this.jumpRotation * -40.0F - 11.0F) * 0.017453292F;
   }

   public void prepareMobModel(T var1, float var2, float var3, float var4) {
      super.prepareMobModel(var1, var2, var3, var4);
      this.jumpRotation = Mth.sin(var1.getJumpCompletion(var4) * 3.1415927F);
   }
}
