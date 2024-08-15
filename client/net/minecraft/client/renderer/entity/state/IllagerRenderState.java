package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.AbstractIllager;

public class IllagerRenderState extends LivingEntityRenderState {
   public boolean isRiding;
   public boolean isAggressive;
   public HumanoidArm mainArm = HumanoidArm.RIGHT;
   public AbstractIllager.IllagerArmPose armPose = AbstractIllager.IllagerArmPose.NEUTRAL;
   public int maxCrossbowChargeDuration;
   public int ticksUsingItem;
   public float attackAnim;

   public IllagerRenderState() {
      super();
   }
}
