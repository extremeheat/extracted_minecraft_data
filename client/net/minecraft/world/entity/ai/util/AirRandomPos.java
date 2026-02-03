package net.minecraft.world.entity.ai.util;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class AirRandomPos {
   public AirRandomPos() {
      super();
   }

   public static @Nullable Vec3 getPosTowards(final PathfinderMob mob, final int horizontalDist, final int verticalDist, final int flyingHeight, final Vec3 towardsPos, final double maxXzRadiansFromDir) {
      Vec3 dir = towardsPos.subtract(mob.getX(), mob.getY(), mob.getZ());
      boolean restrict = GoalUtils.mobRestricted(mob, (double)horizontalDist);
      return RandomPos.generateRandomPos(mob, (Supplier)(() -> {
         BlockPos pos = AirAndWaterRandomPos.generateRandomPos(mob, horizontalDist, verticalDist, flyingHeight, dir.x, dir.z, maxXzRadiansFromDir, restrict);
         return pos != null && !GoalUtils.isWater(mob, pos) ? pos : null;
      }));
   }
}
