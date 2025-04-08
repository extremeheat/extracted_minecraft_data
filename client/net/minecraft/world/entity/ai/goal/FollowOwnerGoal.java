package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.PathType;

public class FollowOwnerGoal extends Goal {
   private final TamableAnimal tamable;
   @Nullable
   private LivingEntity owner;
   private final double speedModifier;
   private final PathNavigation navigation;
   private int timeToRecalcPath;
   private final float stopDistance;
   private final float startDistance;
   private float oldWaterCost;

   public FollowOwnerGoal(TamableAnimal var1, double var2, float var4, float var5) {
      super();
      this.tamable = var1;
      this.speedModifier = var2;
      this.navigation = var1.getNavigation();
      this.startDistance = var4;
      this.stopDistance = var5;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      if (!(var1.getNavigation() instanceof GroundPathNavigation) && !(var1.getNavigation() instanceof FlyingPathNavigation)) {
         throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
      }
   }

   public boolean canUse() {
      LivingEntity var1 = this.tamable.getOwner();
      if (var1 == null) {
         return false;
      } else if (this.tamable.unableToMoveToOwner()) {
         return false;
      } else if (this.tamable.distanceToSqr(var1) < (double)(this.startDistance * this.startDistance)) {
         return false;
      } else {
         this.owner = var1;
         return true;
      }
   }

   public boolean canContinueToUse() {
      if (this.navigation.isDone()) {
         return false;
      } else if (this.tamable.unableToMoveToOwner()) {
         return false;
      } else {
         return !(this.tamable.distanceToSqr(this.owner) <= (double)(this.stopDistance * this.stopDistance));
      }
   }

   public void start() {
      this.timeToRecalcPath = 0;
      this.oldWaterCost = this.tamable.getPathfindingMalus(PathType.WATER);
      this.tamable.setPathfindingMalus(PathType.WATER, 0.0F);
   }

   public void stop() {
      this.owner = null;
      this.navigation.stop();
      this.tamable.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
   }

   public void tick() {
      boolean var1 = this.tamable.shouldTryTeleportToOwner();
      if (!var1) {
         this.tamable.getLookControl().setLookAt(this.owner, 10.0F, (float)this.tamable.getMaxHeadXRot());
      }

      if (--this.timeToRecalcPath <= 0) {
         this.timeToRecalcPath = this.adjustedTickDelay(10);
         if (var1) {
            this.tamable.tryToTeleportToOwner();
         } else {
            this.navigation.moveTo((Entity)this.owner, this.speedModifier);
         }

      }
   }
}
