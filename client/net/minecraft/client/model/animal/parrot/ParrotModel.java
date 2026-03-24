package net.minecraft.client.model.animal.parrot;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.parrot.Parrot;

public class ParrotModel extends EntityModel<ParrotRenderState> {
   private static final String FEATHER = "feather";
   private final ModelPart body;
   private final ModelPart tail;
   private final ModelPart leftWing;
   private final ModelPart rightWing;
   private final ModelPart head;
   private final ModelPart leftLeg;
   private final ModelPart rightLeg;

   public ParrotModel(final ModelPart root) {
      super(root);
      this.body = root.getChild("body");
      this.tail = root.getChild("tail");
      this.leftWing = root.getChild("left_wing");
      this.rightWing = root.getChild("right_wing");
      this.head = root.getChild("head");
      this.leftLeg = root.getChild("left_leg");
      this.rightLeg = root.getChild("right_leg");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(2, 8).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F), PartPose.offsetAndRotation(0.0F, 16.5F, -3.0F, 0.4937F, 0.0F, 0.0F));
      root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(22, 1).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 21.07F, 1.16F, 1.015F, 0.0F, 0.0F));
      root.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(19, 8).addBox(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F), PartPose.offsetAndRotation(1.5F, 16.94F, -2.76F, -0.6981F, -3.1415927F, 0.0F));
      root.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(19, 8).addBox(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F), PartPose.offsetAndRotation(-1.5F, 16.94F, -2.76F, -0.6981F, -3.1415927F, 0.0F));
      PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(2, 2).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(0.0F, 15.69F, -2.76F));
      head.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(10, 0).addBox(-1.0F, -0.5F, -2.0F, 2.0F, 1.0F, 4.0F), PartPose.offset(0.0F, -2.0F, -1.0F));
      head.addOrReplaceChild("beak1", CubeListBuilder.create().texOffs(11, 7).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F), PartPose.offset(0.0F, -0.5F, -1.5F));
      head.addOrReplaceChild("beak2", CubeListBuilder.create().texOffs(16, 7).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F), PartPose.offset(0.0F, -1.75F, -2.45F));
      head.addOrReplaceChild("feather", CubeListBuilder.create().texOffs(2, 18).addBox(0.0F, -4.0F, -2.0F, 0.0F, 5.0F, 4.0F), PartPose.offsetAndRotation(0.0F, -2.15F, 0.15F, -0.2214F, 0.0F, 0.0F));
      CubeListBuilder leg = CubeListBuilder.create().texOffs(14, 18).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F);
      root.addOrReplaceChild("left_leg", leg, PartPose.offsetAndRotation(1.0F, 22.0F, -1.05F, -0.0299F, 0.0F, 0.0F));
      root.addOrReplaceChild("right_leg", leg, PartPose.offsetAndRotation(-1.0F, 22.0F, -1.05F, -0.0299F, 0.0F, 0.0F));
      return LayerDefinition.create(mesh, 32, 32);
   }

   public void setupAnim(final ParrotRenderState state) {
      super.setupAnim(state);
      this.prepare(state.pose);
      this.head.xRot = state.xRot * 0.017453292F;
      this.head.yRot = state.yRot * 0.017453292F;
      switch (state.pose.ordinal()) {
         case 1:
            ModelPart var14 = this.leftLeg;
            var14.xRot += Mth.cos((double)(state.walkAnimationPos * 0.6662F)) * 1.4F * state.walkAnimationSpeed;
            var14 = this.rightLeg;
            var14.xRot += Mth.cos((double)(state.walkAnimationPos * 0.6662F + 3.1415927F)) * 1.4F * state.walkAnimationSpeed;
         case 0:
         case 4:
         default:
            float bobbingBody = state.flapAngle * 0.3F;
            ModelPart var16 = this.head;
            var16.y += bobbingBody;
            var16 = this.tail;
            var16.xRot += Mth.cos((double)(state.walkAnimationPos * 0.6662F)) * 0.3F * state.walkAnimationSpeed;
            var16 = this.tail;
            var16.y += bobbingBody;
            var16 = this.body;
            var16.y += bobbingBody;
            this.leftWing.zRot = -0.0873F - state.flapAngle;
            var16 = this.leftWing;
            var16.y += bobbingBody;
            this.rightWing.zRot = 0.0873F + state.flapAngle;
            var16 = this.rightWing;
            var16.y += bobbingBody;
            var16 = this.leftLeg;
            var16.y += bobbingBody;
            var16 = this.rightLeg;
            var16.y += bobbingBody;
         case 2:
            break;
         case 3:
            float xPos = Mth.cos((double)state.ageInTicks);
            float yPos = Mth.sin((double)state.ageInTicks);
            ModelPart var10000 = this.head;
            var10000.x += xPos;
            var10000 = this.head;
            var10000.y += yPos;
            this.head.xRot = 0.0F;
            this.head.yRot = 0.0F;
            this.head.zRot = Mth.sin((double)state.ageInTicks) * 0.4F;
            var10000 = this.body;
            var10000.x += xPos;
            var10000 = this.body;
            var10000.y += yPos;
            this.leftWing.zRot = -0.0873F - state.flapAngle;
            var10000 = this.leftWing;
            var10000.x += xPos;
            var10000 = this.leftWing;
            var10000.y += yPos;
            this.rightWing.zRot = 0.0873F + state.flapAngle;
            var10000 = this.rightWing;
            var10000.x += xPos;
            var10000 = this.rightWing;
            var10000.y += yPos;
            var10000 = this.tail;
            var10000.x += xPos;
            var10000 = this.tail;
            var10000.y += yPos;
      }

   }

   private void prepare(final Pose pose) {
      switch (pose.ordinal()) {
         case 0:
            ModelPart var4 = this.leftLeg;
            var4.xRot += 0.6981317F;
            var4 = this.rightLeg;
            var4.xRot += 0.6981317F;
         case 1:
         case 4:
         default:
            break;
         case 2:
            float sittingYOffset = 1.9F;
            ++this.head.y;
            ModelPart var10000 = this.tail;
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

   public static Pose getPose(final Parrot entity) {
      if (entity.isPartyParrot()) {
         return ParrotModel.Pose.PARTY;
      } else if (entity.isInSittingPose()) {
         return ParrotModel.Pose.SITTING;
      } else {
         return entity.isFlying() ? ParrotModel.Pose.FLYING : ParrotModel.Pose.STANDING;
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
