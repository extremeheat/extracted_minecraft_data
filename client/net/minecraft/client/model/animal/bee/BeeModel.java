package net.minecraft.client.model.animal.bee;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.BeeRenderState;
import net.minecraft.util.Mth;

public abstract class BeeModel extends EntityModel<BeeRenderState> {
   protected static final String BONE = "bone";
   protected static final String STINGER = "stinger";
   protected static final String FRONT_LEGS = "front_legs";
   protected static final String MIDDLE_LEGS = "middle_legs";
   protected static final String BACK_LEGS = "back_legs";
   protected final ModelPart bone;
   private final ModelPart rightWing;
   private final ModelPart leftWing;
   private final ModelPart frontLeg;
   private final ModelPart midLeg;
   private final ModelPart backLeg;
   private final ModelPart stinger;

   public BeeModel(final ModelPart root) {
      super(root);
      this.bone = root.getChild("bone");
      ModelPart body = this.bone.getChild("body");
      this.stinger = body.getChild("stinger");
      this.rightWing = this.bone.getChild("right_wing");
      this.leftWing = this.bone.getChild("left_wing");
      this.frontLeg = this.bone.getChild("front_legs");
      this.midLeg = this.bone.getChild("middle_legs");
      this.backLeg = this.bone.getChild("back_legs");
   }

   public void setupAnim(final BeeRenderState state) {
      super.setupAnim(state);
      this.stinger.visible = state.hasStinger;
      if (!state.isOnGround) {
         float speed = state.ageInTicks * 120.32113F * 0.017453292F;
         this.rightWing.yRot = 0.0F;
         this.rightWing.zRot = Mth.cos((double)speed) * 3.1415927F * 0.15F;
         this.leftWing.xRot = this.rightWing.xRot;
         this.leftWing.yRot = this.rightWing.yRot;
         this.leftWing.zRot = -this.rightWing.zRot;
         this.frontLeg.xRot = 0.7853982F;
         this.midLeg.xRot = 0.7853982F;
         this.backLeg.xRot = 0.7853982F;
      }

      if (!state.isAngry && !state.isOnGround) {
         float speed = Mth.cos((double)(state.ageInTicks * 0.18F));
         this.bobUpAndDown(speed, state.ageInTicks);
      }

      float rollAmount = state.rollAmount;
      if (rollAmount > 0.0F) {
         this.bone.xRot = Mth.rotLerpRad(rollAmount, this.bone.xRot, 3.0915928F);
      }

   }

   protected void bobUpAndDown(final float speed, final float ageInTicks) {
      this.bone.xRot = 0.1F + speed * 3.1415927F * 0.025F;
      ModelPart var10000 = this.bone;
      var10000.y -= Mth.cos((double)(ageInTicks * 0.18F)) * 0.9F;
      this.frontLeg.xRot = -speed * 3.1415927F * 0.1F + 0.3926991F;
      this.backLeg.xRot = -speed * 3.1415927F * 0.05F + 0.7853982F;
   }
}
