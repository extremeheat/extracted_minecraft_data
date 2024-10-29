package net.minecraft.client.renderer.entity.state;

import net.minecraft.core.Rotations;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ArmorStandRenderState extends HumanoidRenderState {
   public float yRot;
   public float wiggle;
   public boolean isMarker;
   public boolean isSmall;
   public boolean showArms;
   public boolean showBasePlate = true;
   public Rotations headPose;
   public Rotations bodyPose;
   public Rotations leftArmPose;
   public Rotations rightArmPose;
   public Rotations leftLegPose;
   public Rotations rightLegPose;

   public ArmorStandRenderState() {
      super();
      this.headPose = ArmorStand.DEFAULT_HEAD_POSE;
      this.bodyPose = ArmorStand.DEFAULT_BODY_POSE;
      this.leftArmPose = ArmorStand.DEFAULT_LEFT_ARM_POSE;
      this.rightArmPose = ArmorStand.DEFAULT_RIGHT_ARM_POSE;
      this.leftLegPose = ArmorStand.DEFAULT_LEFT_LEG_POSE;
      this.rightLegPose = ArmorStand.DEFAULT_RIGHT_LEG_POSE;
   }
}
