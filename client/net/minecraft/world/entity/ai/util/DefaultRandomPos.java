package net.minecraft.world.entity.ai.util;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

public class DefaultRandomPos {
   @Nullable
   public static Vec3 getPos(PathfinderMob var0, int var1, int var2) {
      boolean var3 = GoalUtils.mobRestricted(var0, var1);
      return RandomPos.generateRandomPos(var0, () -> {
         BlockPos var4 = RandomPos.generateRandomDirection(var0.getRandom(), var1, var2);
         return generateRandomPosTowardDirection(var0, var1, var3, var4);
      });
   }

   @Nullable
   public static Vec3 getPosTowards(PathfinderMob var0, int var1, int var2, Vec3 var3, double var4) {
      Vec3 var6 = var3.subtract(var0.getX(), var0.getY(), var0.getZ());
      boolean var7 = GoalUtils.mobRestricted(var0, var1);
      return RandomPos.generateRandomPos(var0, () -> {
         BlockPos var7x = RandomPos.generateRandomDirectionWithinRadians(var0.getRandom(), var1, var2, 0, var6.x, var6.z, var4);
         return var7x == null ? null : generateRandomPosTowardDirection(var0, var1, var7, var7x);
      });
   }

   @Nullable
   public static Vec3 getPosAway(PathfinderMob var0, int var1, int var2, Vec3 var3) {
      Vec3 var4 = var0.position().subtract(var3);
      boolean var5 = GoalUtils.mobRestricted(var0, var1);
      return RandomPos.generateRandomPos(var0, () -> {
         BlockPos var5x = RandomPos.generateRandomDirectionWithinRadians(var0.getRandom(), var1, var2, 0, var4.x, var4.z, 1.5707963705062866D);
         return var5x == null ? null : generateRandomPosTowardDirection(var0, var1, var5, var5x);
      });
   }

   @Nullable
   private static BlockPos generateRandomPosTowardDirection(PathfinderMob var0, int var1, boolean var2, BlockPos var3) {
      BlockPos var4 = RandomPos.generateRandomPosTowardDirection(var0, var1, var0.getRandom(), var3);
      return !GoalUtils.isOutsideLimits(var4, var0) && !GoalUtils.isRestricted(var2, var0, var4) && !GoalUtils.isNotStable(var0.getNavigation(), var4) && !GoalUtils.hasMalus(var0, var4) ? var4 : null;
   }
}
