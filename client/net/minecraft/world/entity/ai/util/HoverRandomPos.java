package net.minecraft.world.entity.ai.util;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

public class HoverRandomPos {
   public HoverRandomPos() {
      super();
   }

   @Nullable
   public static Vec3 getPos(PathfinderMob var0, int var1, int var2, double var3, double var5, float var7, int var8, int var9) {
      boolean var10 = GoalUtils.mobRestricted(var0, var1);
      return RandomPos.generateRandomPos(var0, () -> {
         BlockPos var11 = RandomPos.generateRandomDirectionWithinRadians(var0.getRandom(), var1, var2, 0, var3, var5, (double)var7);
         if (var11 == null) {
            return null;
         } else {
            BlockPos var12 = LandRandomPos.generateRandomPosTowardDirection(var0, var1, var10, var11);
            if (var12 == null) {
               return null;
            } else {
               var12 = RandomPos.moveUpToAboveSolid(var12, var0.getRandom().nextInt(var8 - var9 + 1) + var9, var0.level().getMaxBuildHeight(), (var1x) -> {
                  return GoalUtils.isSolid(var0, var1x);
               });
               return !GoalUtils.isWater(var0, var12) && !GoalUtils.hasMalus(var0, var12) ? var12 : null;
            }
         }
      });
   }
}
