package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

public class FlyingPathNavigation extends PathNavigation {
   public FlyingPathNavigation(final Mob mob, final Level level) {
      super(mob, level);
   }

   protected PathFinder createPathFinder(final int maxVisitedNodes) {
      this.nodeEvaluator = new FlyNodeEvaluator();
      return new PathFinder(this.nodeEvaluator, maxVisitedNodes);
   }

   protected boolean canMoveDirectly(final Vec3 startPos, final Vec3 stopPos) {
      return isClearForMovementBetween(this.mob, startPos, stopPos, true);
   }

   protected boolean canUpdatePath() {
      return this.canFloat() && this.mob.isInLiquid() || !this.mob.isPassenger();
   }

   protected Vec3 getTempMobPos() {
      return this.mob.position();
   }

   public Path createPath(final Entity target, final int reachRange) {
      return this.createPath(target.blockPosition(), reachRange);
   }

   public void tick() {
      ++this.tick;
      if (this.hasDelayedRecomputation) {
         this.recomputePath();
      }

      if (!this.isDone()) {
         if (this.canUpdatePath()) {
            this.followThePath();
         } else if (this.path != null && !this.path.isDone()) {
            Vec3 pos = this.path.getNextEntityPos(this.mob);
            if (this.mob.getBlockX() == Mth.floor(pos.x) && this.mob.getBlockY() == Mth.floor(pos.y) && this.mob.getBlockZ() == Mth.floor(pos.z)) {
               this.path.advance();
            }
         }

         if (!this.isDone()) {
            Vec3 target = this.path.getNextEntityPos(this.mob);
            this.mob.getMoveControl().setWantedPosition(target.x, target.y, target.z, this.speedModifier);
         }
      }
   }

   public boolean isStableDestination(final BlockPos pos) {
      return this.level.getBlockState(pos).entityCanStandOn(this.level, pos, this.mob);
   }

   public boolean canNavigateGround() {
      return false;
   }
}
