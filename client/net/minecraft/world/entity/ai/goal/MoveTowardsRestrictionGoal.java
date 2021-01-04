package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.RandomPos;
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
         BlockPos var1 = this.mob.getRestrictCenter();
         Vec3 var2 = RandomPos.getPosTowards(this.mob, 16, 7, new Vec3((double)var1.getX(), (double)var1.getY(), (double)var1.getZ()));
         if (var2 == null) {
            return false;
         } else {
            this.wantedX = var2.x;
            this.wantedY = var2.y;
            this.wantedZ = var2.z;
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
