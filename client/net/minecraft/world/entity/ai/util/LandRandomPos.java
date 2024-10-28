package net.minecraft.world.entity.ai.util;

import java.util.Objects;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

public class LandRandomPos {
   public LandRandomPos() {
      super();
   }

   @Nullable
   public static Vec3 getPos(PathfinderMob var0, int var1, int var2) {
      Objects.requireNonNull(var0);
      return getPos(var0, var1, var2, var0::getWalkTargetValue);
   }

   @Nullable
   public static Vec3 getPos(PathfinderMob var0, int var1, int var2, ToDoubleFunction<BlockPos> var3) {
      boolean var4 = GoalUtils.mobRestricted(var0, var1);
      return RandomPos.generateRandomPos(() -> {
         BlockPos var4x = RandomPos.generateRandomDirection(var0.getRandom(), var1, var2);
         BlockPos var5 = generateRandomPosTowardDirection(var0, var1, var4, var4x);
         return var5 == null ? null : movePosUpOutOfSolid(var0, var5);
      }, var3);
   }

   @Nullable
   public static Vec3 getPosTowards(PathfinderMob var0, int var1, int var2, Vec3 var3) {
      Vec3 var4 = var3.subtract(var0.getX(), var0.getY(), var0.getZ());
      boolean var5 = GoalUtils.mobRestricted(var0, var1);
      return getPosInDirection(var0, var1, var2, var4, var5);
   }

   @Nullable
   public static Vec3 getPosAway(PathfinderMob var0, int var1, int var2, Vec3 var3) {
      Vec3 var4 = var0.position().subtract(var3);
      boolean var5 = GoalUtils.mobRestricted(var0, var1);
      return getPosInDirection(var0, var1, var2, var4, var5);
   }

   @Nullable
   private static Vec3 getPosInDirection(PathfinderMob var0, int var1, int var2, Vec3 var3, boolean var4) {
      return RandomPos.generateRandomPos(var0, () -> {
         BlockPos var5 = RandomPos.generateRandomDirectionWithinRadians(var0.getRandom(), var1, var2, 0, var3.x, var3.z, 1.5707963705062866);
         if (var5 == null) {
            return null;
         } else {
            BlockPos var6 = generateRandomPosTowardDirection(var0, var1, var4, var5);
            return var6 == null ? null : movePosUpOutOfSolid(var0, var6);
         }
      });
   }

   @Nullable
   public static BlockPos movePosUpOutOfSolid(PathfinderMob var0, BlockPos var1) {
      var1 = RandomPos.moveUpOutOfSolid(var1, var0.level().getMaxBuildHeight(), (var1x) -> {
         return GoalUtils.isSolid(var0, var1x);
      });
      return !GoalUtils.isWater(var0, var1) && !GoalUtils.hasMalus(var0, var1) ? var1 : null;
   }

   @Nullable
   public static BlockPos generateRandomPosTowardDirection(PathfinderMob var0, int var1, boolean var2, BlockPos var3) {
      BlockPos var4 = RandomPos.generateRandomPosTowardDirection(var0, var1, var0.getRandom(), var3);
      return !GoalUtils.isOutsideLimits(var4, var0) && !GoalUtils.isRestricted(var2, var0, var4) && !GoalUtils.isNotStable(var0.getNavigation(), var4) ? var4 : null;
   }
}
