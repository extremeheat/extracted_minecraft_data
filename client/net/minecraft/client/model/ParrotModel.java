package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
   private final ModelPart root;
   private final ModelPart body;
   private final ModelPart tail;
   private final ModelPart leftWing;
   private final ModelPart rightWing;
   private final ModelPart head;
   private final ModelPart leftLeg;
   private final ModelPart rightLeg;

   public ParrotModel(ModelPart var1) {
      super();
      this.root = var1;
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
      var1.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(2, 8).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F),
         PartPose.offsetAndRotation(0.0F, 16.5F, -3.0F, 0.4937F, 0.0F, 0.0F)
      );
      var1.addOrReplaceChild(
         "tail",
         CubeListBuilder.create().texOffs(22, 1).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F),
         PartPose.offsetAndRotation(0.0F, 21.07F, 1.16F, 1.015F, 0.0F, 0.0F)
      );
      var1.addOrReplaceChild(
         "left_wing",
         CubeListBuilder.create().texOffs(19, 8).addBox(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F),
         PartPose.offsetAndRotation(1.5F, 16.94F, -2.76F, -0.6981F, -3.1415927F, 0.0F)
      );
      var1.addOrReplaceChild(
         "right_wing",
         CubeListBuilder.create().texOffs(19, 8).addBox(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F),
         PartPose.offsetAndRotation(-1.5F, 16.94F, -2.76F, -0.6981F, -3.1415927F, 0.0F)
      );
      PartDefinition var2 = var1.addOrReplaceChild(
         "head", CubeListBuilder.create().texOffs(2, 2).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(0.0F, 15.69F, -2.76F)
      );
      var2.addOrReplaceChild(
         "head2", CubeListBuilder.create().texOffs(10, 0).addBox(-1.0F, -0.5F, -2.0F, 2.0F, 1.0F, 4.0F), PartPose.offset(0.0F, -2.0F, -1.0F)
      );
      var2.addOrReplaceChild(
         "beak1", CubeListBuilder.create().texOffs(11, 7).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F), PartPose.offset(0.0F, -0.5F, -1.5F)
      );
      var2.addOrReplaceChild(
         "beak2", CubeListBuilder.create().texOffs(16, 7).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F), PartPose.offset(0.0F, -1.75F, -2.45F)
      );
      var2.addOrReplaceChild(
         "feather",
         CubeListBuilder.create().texOffs(2, 18).addBox(0.0F, -4.0F, -2.0F, 0.0F, 5.0F, 4.0F),
         PartPose.offsetAndRotation(0.0F, -2.15F, 0.15F, -0.2214F, 0.0F, 0.0F)
      );
      CubeListBuilder var3 = CubeListBuilder.create().texOffs(14, 18).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F);
      var1.addOrReplaceChild("left_leg", var3, PartPose.offsetAndRotation(1.0F, 22.0F, -1.05F, -0.0299F, 0.0F, 0.0F));
      var1.addOrReplaceChild("right_leg", var3, PartPose.offsetAndRotation(-1.0F, 22.0F, -1.05F, -0.0299F, 0.0F, 0.0F));
      return LayerDefinition.create(var0, 32, 32);
   }

   @Override
   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(ParrotRenderState var1) {
      this.prepare(var1.pose);
      float var2 = var1.walkAnimationPos;
      float var3 = var1.walkAnimationSpeed;
      this.setupAnim(var1.pose, var1.ageInTicks, var2, var3, var1.flapAngle, var1.yRot, var1.xRot);
   }

   public void renderOnShoulder(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8, float var9) {
      this.prepare(ParrotModel.Pose.ON_SHOULDER);
      this.setupAnim(ParrotModel.Pose.ON_SHOULDER, var9, var5, var6, 0.0F, var7, var8);
      this.root.render(var1, var2, var3, var4);
   }

   private void setupAnim(ParrotModel.Pose var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.head.xRot = var7 * 0.017453292F;
      this.head.yRot = var6 * 0.017453292F;
      switch (var1) {
         case STANDING:
            this.leftLeg.xRot = this.leftLeg.xRot + Mth.cos(var3 * 0.6662F) * 1.4F * var4;
            this.rightLeg.xRot = this.rightLeg.xRot + Mth.cos(var3 * 0.6662F + 3.1415927F) * 1.4F * var4;
         case FLYING:
         case ON_SHOULDER:
         default:
            float var10 = var5 * 0.3F;
            this.head.y += var10;
            this.tail.xRot = this.tail.xRot + Mth.cos(var3 * 0.6662F) * 0.3F * var4;
            this.tail.y += var10;
            this.body.y += var10;
            this.leftWing.zRot = -0.0873F - var5;
            this.leftWing.y += var10;
            this.rightWing.zRot = 0.0873F + var5;
            this.rightWing.y += var10;
            this.leftLeg.y += var10;
            this.rightLeg.y += var10;
         case SITTING:
            break;
         case PARTY:
            float var8 = Mth.cos(var2);
            float var9 = Mth.sin(var2);
            this.head.x += var8;
            this.head.y += var9;
            this.head.xRot = 0.0F;
            this.head.yRot = 0.0F;
            this.head.zRot = Mth.sin(var2) * 0.4F;
            this.body.x += var8;
            this.body.y += var9;
            this.leftWing.zRot = -0.0873F - var5;
            this.leftWing.x += var8;
            this.leftWing.y += var9;
            this.rightWing.zRot = 0.0873F + var5;
            this.rightWing.x += var8;
            this.rightWing.y += var9;
            this.tail.x += var8;
            this.tail.y += var9;
      }
   }

   private void prepare(ParrotModel.Pose var1) {
      this.body.resetPose();
      this.head.resetPose();
      this.tail.resetPose();
      this.rightWing.resetPose();
      this.leftWing.resetPose();
      this.leftLeg.resetPose();
      this.rightLeg.resetPose();
      switch (var1) {
         case FLYING:
            this.leftLeg.xRot += 0.6981317F;
            this.rightLeg.xRot += 0.6981317F;
         case STANDING:
         case ON_SHOULDER:
         default:
            break;
         case SITTING:
            float var2 = 1.9F;
            this.head.y++;
            this.tail.xRot += 0.5235988F;
            this.tail.y++;
            this.body.y++;
            this.leftWing.zRot = -0.0873F;
            this.leftWing.y++;
            this.rightWing.zRot = 0.0873F;
            this.rightWing.y++;
            this.leftLeg.y++;
            this.rightLeg.y++;
            this.leftLeg.xRot++;
            this.rightLeg.xRot++;
            break;
         case PARTY:
            this.leftLeg.zRot = -0.34906584F;
            this.rightLeg.zRot = 0.34906584F;
      }
   }

   public static ParrotModel.Pose getPose(Parrot var0) {
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
   }
}
