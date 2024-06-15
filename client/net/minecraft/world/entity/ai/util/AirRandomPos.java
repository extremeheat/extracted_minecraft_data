package net.minecraft.world.entity.ai.util;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

public class AirRandomPos {
   public AirRandomPos() {
      super();
   }

   @Nullable
   public static Vec3 getPosTowards(PathfinderMob var0, int var1, int var2, int var3, Vec3 var4, double var5) {
      Vec3 var7 = var4.subtract(var0.getX(), var0.getY(), var0.getZ());
      boolean var8 = GoalUtils.mobRestricted(var0, var1);
      return RandomPos.generateRandomPos(var0, () -> {
         BlockPos var8x = AirAndWaterRandomPos.generateRandomPos(var0, var1, var2, var3, var7.x, var7.z, var5, var8);
         return var8x != null && !GoalUtils.isWater(var0, var8x) ? var8x : null;
      });
   }
}
