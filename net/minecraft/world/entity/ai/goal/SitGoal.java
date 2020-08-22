package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;

public class SitGoal extends Goal {
   private final TamableAnimal mob;
   private boolean wantToSit;

   public SitGoal(TamableAnimal var1) {
      this.mob = var1;
      this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
   }

   public boolean canContinueToUse() {
      return this.wantToSit;
   }

   public boolean canUse() {
      if (!this.mob.isTame()) {
         return false;
      } else if (this.mob.isInWaterOrBubble()) {
         return false;
      } else if (!this.mob.onGround) {
         return false;
      } else {
         LivingEntity var1 = this.mob.getOwner();
         if (var1 == null) {
            return true;
         } else {
            return this.mob.distanceToSqr(var1) < 144.0D && var1.getLastHurtByMob() != null ? false : this.wantToSit;
         }
      }
   }

   public void start() {
      this.mob.getNavigation().stop();
      this.mob.setSitting(true);
   }

   public void stop() {
      this.mob.setSitting(false);
   }

   public void wantToSit(boolean var1) {
      this.wantToSit = var1;
   }
}
