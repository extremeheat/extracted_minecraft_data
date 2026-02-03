package net.minecraft.client.model.animal.fox;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.FoxRenderState;
import net.minecraft.util.Mth;

public abstract class FoxModel extends EntityModel<FoxRenderState> {
   public final ModelPart head;
   protected final ModelPart body;
   protected final ModelPart rightHindLeg;
   protected final ModelPart leftHindLeg;
   protected final ModelPart rightFrontLeg;
   protected final ModelPart leftFrontLeg;
   protected final ModelPart tail;
   private float legMotionPos;

   public FoxModel(final ModelPart root) {
      super(root);
      this.head = root.getChild("head");
      this.body = root.getChild("body");
      this.rightHindLeg = root.getChild("right_hind_leg");
      this.leftHindLeg = root.getChild("left_hind_leg");
      this.rightFrontLeg = root.getChild("right_front_leg");
      this.leftFrontLeg = root.getChild("left_front_leg");
      this.tail = this.body.getChild("tail");
   }

   public void setupAnim(final FoxRenderState state) {
      super.setupAnim(state);
      this.setWalkingPose(state);
      if (state.isCrouching) {
         this.setCrouchingPose(state);
      } else if (state.isSleeping) {
         this.setSleepingPose(state);
      } else if (state.isSitting) {
         this.setSittingPose(state);
      }

      if (state.isPouncing) {
         this.setPouncingPose(state);
      }

      if (!state.isSleeping && !state.isFaceplanted && !state.isCrouching) {
         this.head.xRot = state.xRot * 0.017453292F;
         this.head.yRot = state.yRot * 0.017453292F;
      }

      if (state.isSleeping) {
         this.head.xRot = 0.0F;
         this.head.yRot = -2.0943952F;
         this.head.zRot = Mth.cos((double)(state.ageInTicks * 0.027F)) / 22.0F;
      }

      if (state.isFaceplanted) {
         float legMoveFactor = 0.1F;
         this.legMotionPos += 0.67F;
         this.rightHindLeg.xRot = Mth.cos((double)(this.legMotionPos * 0.4662F)) * 0.1F;
         this.leftHindLeg.xRot = Mth.cos((double)(this.legMotionPos * 0.4662F + 3.1415927F)) * 0.1F;
         this.rightFrontLeg.xRot = Mth.cos((double)(this.legMotionPos * 0.4662F + 3.1415927F)) * 0.1F;
         this.leftFrontLeg.xRot = Mth.cos((double)(this.legMotionPos * 0.4662F)) * 0.1F;
      }

   }

   protected void setSittingPose(final FoxRenderState state) {
      this.head.xRot = 0.0F;
      this.head.yRot = 0.0F;
   }

   protected void setSleepingPose(final FoxRenderState state) {
      this.rightHindLeg.visible = false;
      this.leftHindLeg.visible = false;
      this.rightFrontLeg.visible = false;
      this.leftFrontLeg.visible = false;
   }

   protected void setWalkingPose(final FoxRenderState state) {
      this.head.zRot = state.headRollAngle;
      this.rightHindLeg.visible = true;
      this.leftHindLeg.visible = true;
      this.rightFrontLeg.visible = true;
      this.leftFrontLeg.visible = true;
   }

   protected void setCrouchingPose(final FoxRenderState state) {
      ModelPart var10000 = this.body;
      var10000.xRot += 0.10471976F;
      var10000 = this.head;
      var10000.y += state.crouchAmount * state.ageScale;
      float wiggleAmount = Mth.cos((double)state.ageInTicks) * 0.05F;
      this.body.yRot = wiggleAmount;
      this.rightHindLeg.zRot = wiggleAmount;
      this.leftHindLeg.zRot = wiggleAmount;
      this.rightFrontLeg.zRot = wiggleAmount / 2.0F;
      this.leftFrontLeg.zRot = wiggleAmount / 2.0F;
   }

   protected void setPouncingPose(final FoxRenderState state) {
   }
}
