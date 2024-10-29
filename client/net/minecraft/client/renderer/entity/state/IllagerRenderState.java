package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.AbstractIllager;

public class IllagerRenderState extends LivingEntityRenderState {
   public boolean isRiding;
   public boolean isAggressive;
   public HumanoidArm mainArm;
   public AbstractIllager.IllagerArmPose armPose;
   public int maxCrossbowChargeDuration;
   public int ticksUsingItem;
   public float attackAnim;

   public IllagerRenderState() {
      super();
      this.mainArm = HumanoidArm.RIGHT;
      this.armPose = AbstractIllager.IllagerArmPose.NEUTRAL;
   }
}
