package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.PathType;
import org.jspecify.annotations.Nullable;

public class FollowOwnerGoal extends Goal {
   private final TamableAnimal tamable;
   private @Nullable LivingEntity owner;
   private final double speedModifier;
   private final PathNavigation navigation;
   private int timeToRecalcPath;
   private final float stopDistance;
   private final float startDistance;
   private float oldWaterCost;

   public FollowOwnerGoal(final TamableAnimal tamable, final double speedModifier, final float startDistance, final float stopDistance) {
      super();
      this.tamable = tamable;
      this.speedModifier = speedModifier;
      this.navigation = tamable.getNavigation();
      this.startDistance = startDistance;
      this.stopDistance = stopDistance;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      if (!(tamable.getNavigation() instanceof GroundPathNavigation) && !(tamable.getNavigation() instanceof FlyingPathNavigation)) {
         throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
      }
   }

   public boolean canUse() {
      LivingEntity owner = this.tamable.getOwner();
      if (owner == null) {
         return false;
      } else if (this.tamable.unableToMoveToOwner()) {
         return false;
      } else if (this.tamable.distanceToSqr(owner) < (double)(this.startDistance * this.startDistance)) {
         return false;
      } else {
         this.owner = owner;
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
      boolean isOwnerFarAway = this.tamable.shouldTryTeleportToOwner();
      if (!isOwnerFarAway) {
         this.tamable.getLookControl().setLookAt(this.owner, 10.0F, (float)this.tamable.getMaxHeadXRot());
      }

      if (--this.timeToRecalcPath <= 0) {
         this.timeToRecalcPath = this.adjustedTickDelay(10);
         if (isOwnerFarAway) {
            this.tamable.tryToTeleportToOwner();
         } else {
            this.navigation.moveTo((Entity)this.owner, this.speedModifier);
         }

      }
   }
}
