package net.minecraft.world.entity.ai.goal.target;

import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.scores.Team;

public abstract class TargetGoal extends Goal {
   private static final int EMPTY_REACH_CACHE = 0;
   private static final int CAN_REACH_CACHE = 1;
   private static final int CANT_REACH_CACHE = 2;
   protected final Mob mob;
   protected final boolean mustSee;
   private final boolean mustReach;
   private int reachCache;
   private int reachCacheTime;
   private int unseenTicks;
   @Nullable
   protected LivingEntity targetMob;
   protected int unseenMemoryTicks = 60;

   public TargetGoal(Mob var1, boolean var2) {
      this(var1, var2, false);
   }

   public TargetGoal(Mob var1, boolean var2, boolean var3) {
      super();
      this.mob = var1;
      this.mustSee = var2;
      this.mustReach = var3;
   }

   @Override
   public boolean canContinueToUse() {
      LivingEntity var1 = this.mob.getTarget();
      if (var1 == null) {
         var1 = this.targetMob;
      }

      if (var1 == null) {
         return false;
      } else if (!this.mob.canAttack(var1)) {
         return false;
      } else {
         Team var2 = this.mob.getTeam();
         Team var3 = var1.getTeam();
         if (var2 != null && var3 == var2) {
            return false;
         } else {
            double var4 = this.getFollowDistance();
            if (this.mob.distanceToSqr(var1) > var4 * var4) {
               return false;
            } else {
               if (this.mustSee) {
                  if (this.mob.getSensing().hasLineOfSight(var1)) {
                     this.unseenTicks = 0;
                  } else if (++this.unseenTicks > reducedTickDelay(this.unseenMemoryTicks)) {
                     return false;
                  }
               }

               this.mob.setTarget(var1);
               return true;
            }
         }
      }
   }

   protected double getFollowDistance() {
      return this.mob.getAttributeValue(Attributes.FOLLOW_RANGE);
   }

   @Override
   public void start() {
      this.reachCache = 0;
      this.reachCacheTime = 0;
      this.unseenTicks = 0;
   }

   @Override
   public void stop() {
      this.mob.setTarget(null);
      this.targetMob = null;
   }

   protected boolean canAttack(@Nullable LivingEntity var1, TargetingConditions var2) {
      if (var1 == null) {
         return false;
      } else if (!var2.test(this.mob, var1)) {
         return false;
      } else if (!this.mob.isWithinRestriction(var1.blockPosition())) {
         return false;
      } else {
         if (this.mustReach) {
            if (--this.reachCacheTime <= 0) {
               this.reachCache = 0;
            }

            if (this.reachCache == 0) {
               this.reachCache = this.canReach(var1) ? 1 : 2;
            }

            if (this.reachCache == 2) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean canReach(LivingEntity var1) {
      this.reachCacheTime = reducedTickDelay(10 + this.mob.getRandom().nextInt(5));
      Path var2 = this.mob.getNavigation().createPath(var1, 0);
      if (var2 == null) {
         return false;
      } else {
         Node var3 = var2.getEndNode();
         if (var3 == null) {
            return false;
         } else {
            int var4 = var3.x - var1.getBlockX();
            int var5 = var3.z - var1.getBlockZ();
            return (double)(var4 * var4 + var5 * var5) <= 2.25;
         }
      }
   }

   public TargetGoal setUnseenMemoryTicks(int var1) {
      this.unseenMemoryTicks = var1;
      return this;
   }
}
