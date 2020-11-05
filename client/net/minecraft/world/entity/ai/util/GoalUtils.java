package net.minecraft.world.entity.ai.util;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class GoalUtils {
   public static boolean hasGroundPathNavigation(Mob var0) {
      return var0.getNavigation() instanceof GroundPathNavigation;
   }

   public static boolean mobRestricted(PathfinderMob var0, int var1) {
      return var0.hasRestriction() && var0.getRestrictCenter().closerThan(var0.position(), (double)(var0.getRestrictRadius() + (float)var1) + 1.0D);
   }

   public static boolean isOutsideLimits(BlockPos var0, PathfinderMob var1) {
      return var0.getY() < var1.level.getMinBuildHeight() || var0.getY() > var1.level.getMaxBuildHeight();
   }

   public static boolean isRestricted(boolean var0, PathfinderMob var1, BlockPos var2) {
      return var0 && !var1.isWithinRestriction(var2);
   }

   public static boolean isNotStable(PathNavigation var0, BlockPos var1) {
      return !var0.isStableDestination(var1);
   }

   public static boolean isWater(PathfinderMob var0, BlockPos var1) {
      return var0.level.getFluidState(var1).is(FluidTags.WATER);
   }

   public static boolean hasMalus(PathfinderMob var0, BlockPos var1) {
      return var0.getPathfindingMalus(WalkNodeEvaluator.getBlockPathTypeStatic(var0.level, var1.mutable())) != 0.0F;
   }

   public static boolean isSolid(PathfinderMob var0, BlockPos var1) {
      return var0.level.getBlockState(var1).getMaterial().isSolid();
   }
}
