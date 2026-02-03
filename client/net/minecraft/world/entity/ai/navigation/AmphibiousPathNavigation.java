package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

public class AmphibiousPathNavigation extends PathNavigation {
   public AmphibiousPathNavigation(final Mob mob, final Level level) {
      super(mob, level);
   }

   protected PathFinder createPathFinder(final int maxVisitedNodes) {
      this.nodeEvaluator = new AmphibiousNodeEvaluator(false);
      return new PathFinder(this.nodeEvaluator, maxVisitedNodes);
   }

   protected boolean canUpdatePath() {
      return true;
   }

   protected Vec3 getTempMobPos() {
      return new Vec3(this.mob.getX(), this.mob.getY(0.5), this.mob.getZ());
   }

   protected double getGroundY(final Vec3 target) {
      return target.y;
   }

   protected boolean canMoveDirectly(final Vec3 startPos, final Vec3 stopPos) {
      return this.mob.isInLiquid() ? isClearForMovementBetween(this.mob, startPos, stopPos, false) : false;
   }

   public boolean isStableDestination(final BlockPos pos) {
      return !this.level.getBlockState(pos.below()).isAir();
   }

   public void setCanFloat(final boolean canFloat) {
   }

   public boolean canNavigateGround() {
      return true;
   }
}
