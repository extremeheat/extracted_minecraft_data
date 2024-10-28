package net.minecraft.world.entity.ai.util;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

public class AirAndWaterRandomPos {
   public AirAndWaterRandomPos() {
      super();
   }

   @Nullable
   public static Vec3 getPos(PathfinderMob var0, int var1, int var2, int var3, double var4, double var6, double var8) {
      boolean var10 = GoalUtils.mobRestricted(var0, var1);
      return RandomPos.generateRandomPos(var0, () -> {
         return generateRandomPos(var0, var1, var2, var3, var4, var6, var8, var10);
      });
   }

   @Nullable
   public static BlockPos generateRandomPos(PathfinderMob var0, int var1, int var2, int var3, double var4, double var6, double var8, boolean var10) {
      BlockPos var11 = RandomPos.generateRandomDirectionWithinRadians(var0.getRandom(), var1, var2, var3, var4, var6, var8);
      if (var11 == null) {
         return null;
      } else {
         BlockPos var12 = RandomPos.generateRandomPosTowardDirection(var0, var1, var0.getRandom(), var11);
         if (!GoalUtils.isOutsideLimits(var12, var0) && !GoalUtils.isRestricted(var10, var0, var12)) {
            var12 = RandomPos.moveUpOutOfSolid(var12, var0.level().getMaxBuildHeight(), (var1x) -> {
               return GoalUtils.isSolid(var0, var1x);
            });
            return GoalUtils.hasMalus(var0, var12) ? null : var12;
         } else {
            return null;
         }
      }
   }
}
