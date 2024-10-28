package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.PathType;

public class FollowMobGoal extends Goal {
   private final Mob mob;
   private final Predicate<Mob> followPredicate;
   @Nullable
   private Mob followingMob;
   private final double speedModifier;
   private final PathNavigation navigation;
   private int timeToRecalcPath;
   private final float stopDistance;
   private float oldWaterCost;
   private final float areaSize;

   public FollowMobGoal(Mob var1, double var2, float var4, float var5) {
      super();
      this.mob = var1;
      this.followPredicate = (var1x) -> {
         return var1x != null && var1.getClass() != var1x.getClass();
      };
      this.speedModifier = var2;
      this.navigation = var1.getNavigation();
      this.stopDistance = var4;
      this.areaSize = var5;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      if (!(var1.getNavigation() instanceof GroundPathNavigation) && !(var1.getNavigation() instanceof FlyingPathNavigation)) {
         throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
      }
   }

   public boolean canUse() {
      List var1 = this.mob.level().getEntitiesOfClass(Mob.class, this.mob.getBoundingBox().inflate((double)this.areaSize), this.followPredicate);
      if (!var1.isEmpty()) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            Mob var3 = (Mob)var2.next();
            if (!var3.isInvisible()) {
               this.followingMob = var3;
               return true;
            }
         }
      }

      return false;
   }

   public boolean canContinueToUse() {
      return this.followingMob != null && !this.navigation.isDone() && this.mob.distanceToSqr(this.followingMob) > (double)(this.stopDistance * this.stopDistance);
   }

   public void start() {
      this.timeToRecalcPath = 0;
      this.oldWaterCost = this.mob.getPathfindingMalus(PathType.WATER);
      this.mob.setPathfindingMalus(PathType.WATER, 0.0F);
   }

   public void stop() {
      this.followingMob = null;
      this.navigation.stop();
      this.mob.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
   }

   public void tick() {
      if (this.followingMob != null && !this.mob.isLeashed()) {
         this.mob.getLookControl().setLookAt(this.followingMob, 10.0F, (float)this.mob.getMaxHeadXRot());
         if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            double var1 = this.mob.getX() - this.followingMob.getX();
            double var3 = this.mob.getY() - this.followingMob.getY();
            double var5 = this.mob.getZ() - this.followingMob.getZ();
            double var7 = var1 * var1 + var3 * var3 + var5 * var5;
            if (!(var7 <= (double)(this.stopDistance * this.stopDistance))) {
               this.navigation.moveTo((Entity)this.followingMob, this.speedModifier);
            } else {
               this.navigation.stop();
               LookControl var9 = this.followingMob.getLookControl();
               if (var7 <= (double)this.stopDistance || var9.getWantedX() == this.mob.getX() && var9.getWantedY() == this.mob.getY() && var9.getWantedZ() == this.mob.getZ()) {
                  double var10 = this.followingMob.getX() - this.mob.getX();
                  double var12 = this.followingMob.getZ() - this.mob.getZ();
                  this.navigation.moveTo(this.mob.getX() - var10, this.mob.getY(), this.mob.getZ() - var12, this.speedModifier);
               }

            }
         }
      }
   }
}
