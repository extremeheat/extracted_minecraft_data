package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Parrot;

public class ParrotModel extends EntityModel<ParrotRenderState> {
   private static final String FEATHER = "feather";
   private final ModelPart body;
   private final ModelPart tail;
   private final ModelPart leftWing;
   private final ModelPart rightWing;
   private final ModelPart head;
   private final ModelPart leftLeg;
   private final ModelPart rightLeg;

   public ParrotModel(ModelPart var1) {
      super(var1);
      this.body = var1.getChild("body");
      this.tail = var1.getChild("tail");
      this.leftWing = var1.getChild("left_wing");
      this.rightWing = var1.getChild("right_wing");
      this.head = var1.getChild("head");
      this.leftLeg = var1.getChild("left_leg");
      this.rightLeg = var1.getChild("right_leg");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(2, 8).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F), PartPose.offsetAndRotation(0.0F, 16.5F, -3.0F, 0.4937F, 0.0F, 0.0F));
      var1.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(22, 1).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 21.07F, 1.16F, 1.015F, 0.0F, 0.0F));
      var1.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(19, 8).addBox(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F), PartPose.offsetAndRotation(1.5F, 16.94F, -2.76F, -0.6981F, -3.1415927F, 0.0F));
      var1.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(19, 8).addBox(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F), PartPose.offsetAndRotation(-1.5F, 16.94F, -2.76F, -0.6981F, -3.1415927F, 0.0F));
      PartDefinition var2 = var1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(2, 2).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(0.0F, 15.69F, -2.76F));
      var2.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(10, 0).addBox(-1.0F, -0.5F, -2.0F, 2.0F, 1.0F, 4.0F), PartPose.offset(0.0F, -2.0F, -1.0F));
      var2.addOrReplaceChild("beak1", CubeListBuilder.create().texOffs(11, 7).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F), PartPose.offset(0.0F, -0.5F, -1.5F));
      var2.addOrReplaceChild("beak2", CubeListBuilder.create().texOffs(16, 7).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F), PartPose.offset(0.0F, -1.75F, -2.45F));
      var2.addOrReplaceChild("feather", CubeListBuilder.create().texOffs(2, 18).addBox(0.0F, -4.0F, -2.0F, 0.0F, 5.0F, 4.0F), PartPose.offsetAndRotation(0.0F, -2.15F, 0.15F, -0.2214F, 0.0F, 0.0F));
      CubeListBuilder var3 = CubeListBuilder.create().texOffs(14, 18).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F);
      var1.addOrReplaceChild("left_leg", var3, PartPose.offsetAndRotation(1.0F, 22.0F, -1.05F, -0.0299F, 0.0F, 0.0F));
      var1.addOrReplaceChild("right_leg", var3, PartPose.offsetAndRotation(-1.0F, 22.0F, -1.05F, -0.0299F, 0.0F, 0.0F));
      return LayerDefinition.create(var0, 32, 32);
   }

   public void setupAnim(ParrotRenderState var1) {
      super.setupAnim(var1);
      this.prepare(var1.pose);
      this.head.xRot = var1.xRot * 0.017453292F;
      this.head.yRot = var1.yRot * 0.017453292F;
      ModelPart var10000;
      switch (var1.pose.ordinal()) {
         case 1:
            var10000 = this.leftLeg;
            var10000.xRot += Mth.cos(var1.walkAnimationPos * 0.6662F) * 1.4F * var1.walkAnimationSpeed;
            var10000 = this.rightLeg;
            var10000.xRot += Mth.cos(var1.walkAnimationPos * 0.6662F + 3.1415927F) * 1.4F * var1.walkAnimationSpeed;
         case 0:
         case 4:
         default:
            float var4 = var1.flapAngle * 0.3F;
            var10000 = this.head;
            var10000.y += var4;
            var10000 = this.tail;
            var10000.xRot += Mth.cos(var1.walkAnimationPos * 0.6662F) * 0.3F * var1.walkAnimationSpeed;
            var10000 = this.tail;
            var10000.y += var4;
            var10000 = this.body;
            var10000.y += var4;
            this.leftWing.zRot = -0.0873F - var1.flapAngle;
            var10000 = this.leftWing;
            var10000.y += var4;
            this.rightWing.zRot = 0.0873F + var1.flapAngle;
            var10000 = this.rightWing;
            var10000.y += var4;
            var10000 = this.leftLeg;
            var10000.y += var4;
            var10000 = this.rightLeg;
            var10000.y += var4;
         case 2:
            break;
         case 3:
            float var2 = Mth.cos(var1.ageInTicks);
            float var3 = Mth.sin(var1.ageInTicks);
            var10000 = this.head;
            var10000.x += var2;
            var10000 = this.head;
            var10000.y += var3;
            this.head.xRot = 0.0F;
            this.head.yRot = 0.0F;
            this.head.zRot = Mth.sin(var1.ageInTicks) * 0.4F;
            var10000 = this.body;
            var10000.x += var2;
            var10000 = this.body;
            var10000.y += var3;
            this.leftWing.zRot = -0.0873F - var1.flapAngle;
            var10000 = this.leftWing;
            var10000.x += var2;
            var10000 = this.leftWing;
            var10000.y += var3;
            this.rightWing.zRot = 0.0873F + var1.flapAngle;
            var10000 = this.rightWing;
            var10000.x += var2;
            var10000 = this.rightWing;
            var10000.y += var3;
            var10000 = this.tail;
            var10000.x += var2;
            var10000 = this.tail;
            var10000.y += var3;
      }

   }

   private void prepare(Pose var1) {
      ModelPart var10000;
      switch (var1.ordinal()) {
         case 0:
            var10000 = this.leftLeg;
            var10000.xRot += 0.6981317F;
            var10000 = this.rightLeg;
            var10000.xRot += 0.6981317F;
         case 1:
         case 4:
         default:
            break;
         case 2:
            float var2 = 1.9F;
            ++this.head.y;
            var10000 = this.tail;
            var10000.xRot += 0.5235988F;
            ++this.tail.y;
            ++this.body.y;
            this.leftWing.zRot = -0.0873F;
            ++this.leftWing.y;
            this.rightWing.zRot = 0.0873F;
            ++this.rightWing.y;
            ++this.leftLeg.y;
            ++this.rightLeg.y;
            ++this.leftLeg.xRot;
            ++this.rightLeg.xRot;
            break;
         case 3:
            this.leftLeg.zRot = -0.34906584F;
            this.rightLeg.zRot = 0.34906584F;
      }

   }

   public static Pose getPose(Parrot var0) {
      if (var0.isPartyParrot()) {
         return ParrotModel.Pose.PARTY;
      } else if (var0.isInSittingPose()) {
         return ParrotModel.Pose.SITTING;
      } else {
         return var0.isFlying() ? ParrotModel.Pose.FLYING : ParrotModel.Pose.STANDING;
      }
   }

   public static enum Pose {
      FLYING,
      STANDING,
      SITTING,
      PARTY,
      ON_SHOULDER;

      private Pose() {
      }

      // $FF: synthetic method
      private static Pose[] $values() {
         return new Pose[]{FLYING, STANDING, SITTING, PARTY, ON_SHOULDER};
      }
   }
}
