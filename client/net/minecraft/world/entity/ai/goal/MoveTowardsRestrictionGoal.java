package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public class MoveTowardsRestrictionGoal extends Goal {
   private final PathfinderMob mob;
   private double wantedX;
   private double wantedY;
   private double wantedZ;
   private final double speedModifier;

   public MoveTowardsRestrictionGoal(PathfinderMob var1, double var2) {
      super();
      this.mob = var1;
      this.speedModifier = var2;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (this.mob.isWithinRestriction()) {
         return false;
      } else {
         Vec3 var1 = DefaultRandomPos.getPosTowards(this.mob, 16, 7, Vec3.atBottomCenterOf(this.mob.getRestrictCenter()), 1.5707963705062866D);
         if (var1 == null) {
            return false;
         } else {
            this.wantedX = var1.x;
            this.wantedY = var1.y;
            this.wantedZ = var1.z;
            return true;
         }
      }
   }

   public boolean canContinueToUse() {
      return !this.mob.getNavigation().isDone();
   }

   public void start() {
      this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
   }
}
