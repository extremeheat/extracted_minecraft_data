package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;

public class SitWhenOrderedToGoal extends Goal {
   private final TamableAnimal mob;

   public SitWhenOrderedToGoal(TamableAnimal var1) {
      super();
      this.mob = var1;
      this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
   }

   public boolean canContinueToUse() {
      return this.mob.isOrderedToSit();
   }

   public boolean canUse() {
      if (!this.mob.isTame()) {
         return false;
      } else if (this.mob.isInWaterOrBubble()) {
         return false;
      } else if (!this.mob.isOnGround()) {
         return false;
      } else {
         LivingEntity var1 = this.mob.getOwner();
         if (var1 == null) {
            return true;
         } else {
            return this.mob.distanceToSqr(var1) < 144.0 && var1.getLastHurtByMob() != null ? false : this.mob.isOrderedToSit();
         }
      }
   }

   public void start() {
      this.mob.getNavigation().stop();
      this.mob.setInSittingPose(true);
   }

   public void stop() {
      this.mob.setInSittingPose(false);
   }
}
