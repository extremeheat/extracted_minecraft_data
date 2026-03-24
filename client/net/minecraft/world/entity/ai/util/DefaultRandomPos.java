package net.minecraft.world.entity.ai.util;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class DefaultRandomPos {
   public DefaultRandomPos() {
      super();
   }

   public static @Nullable Vec3 getPos(final PathfinderMob mob, final int horizontalDist, final int verticalDist) {
      boolean restrict = GoalUtils.mobRestricted(mob, (double)horizontalDist);
      return RandomPos.generateRandomPos(mob, (Supplier)(() -> {
         BlockPos direction = RandomPos.generateRandomDirection(mob.getRandom(), horizontalDist, verticalDist);
         return generateRandomPosTowardDirection(mob, horizontalDist, restrict, direction);
      }));
   }

   public static @Nullable Vec3 getPosTowards(final PathfinderMob mob, final int horizontalDist, final int verticalDist, final Vec3 towardsPos, final double maxXzRadiansFromDir) {
      Vec3 dir = towardsPos.subtract(mob.getX(), mob.getY(), mob.getZ());
      boolean restrict = GoalUtils.mobRestricted(mob, (double)horizontalDist);
      return RandomPos.generateRandomPos(mob, (Supplier)(() -> {
         BlockPos direction = RandomPos.generateRandomDirectionWithinRadians(mob.getRandom(), 0.0, (double)horizontalDist, verticalDist, 0, dir.x, dir.z, maxXzRadiansFromDir);
         return direction == null ? null : generateRandomPosTowardDirection(mob, horizontalDist, restrict, direction);
      }));
   }

   public static @Nullable Vec3 getPosAway(final PathfinderMob mob, final int horizontalDist, final int verticalDist, final Vec3 avoidPos) {
      Vec3 dirAway = mob.position().subtract(avoidPos);
      boolean restrict = GoalUtils.mobRestricted(mob, (double)horizontalDist);
      return RandomPos.generateRandomPos(mob, (Supplier)(() -> {
         BlockPos direction = RandomPos.generateRandomDirectionWithinRadians(mob.getRandom(), 0.0, (double)horizontalDist, verticalDist, 0, dirAway.x, dirAway.z, 1.5707963705062866);
         return direction == null ? null : generateRandomPosTowardDirection(mob, horizontalDist, restrict, direction);
      }));
   }

   private static @Nullable BlockPos generateRandomPosTowardDirection(final PathfinderMob mob, final int horizontalDist, final boolean restrict, final BlockPos direction) {
      BlockPos pos = RandomPos.generateRandomPosTowardDirection(mob, (double)horizontalDist, mob.getRandom(), direction);
      return !GoalUtils.isOutsideLimits(pos, mob) && !GoalUtils.isRestricted(restrict, mob, pos) && !GoalUtils.isNotStable(mob.getNavigation(), pos) && !GoalUtils.hasMalus(mob, pos) ? pos : null;
   }
}
