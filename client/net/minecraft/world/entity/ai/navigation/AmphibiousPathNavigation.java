package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

public class AmphibiousPathNavigation extends PathNavigation {
   public AmphibiousPathNavigation(Mob var1, Level var2) {
      super(var1, var2);
   }

   @Override
   protected PathFinder createPathFinder(int var1) {
      this.nodeEvaluator = new AmphibiousNodeEvaluator(false);
      this.nodeEvaluator.setCanPassDoors(true);
      return new PathFinder(this.nodeEvaluator, var1);
   }

   @Override
   protected boolean canUpdatePath() {
      return true;
   }

   @Override
   protected Vec3 getTempMobPos() {
      return new Vec3(this.mob.getX(), this.mob.getY(0.5), this.mob.getZ());
   }

   @Override
   protected double getGroundY(Vec3 var1) {
      return var1.y;
   }

   @Override
   protected boolean canMoveDirectly(Vec3 var1, Vec3 var2) {
      return this.isInLiquid() ? isClearForMovementBetween(this.mob, var1, var2) : false;
   }

   @Override
   public boolean isStableDestination(BlockPos var1) {
      return !this.level.getBlockState(var1.below()).isAir();
   }

   @Override
   public void setCanFloat(boolean var1) {
   }
}
