package net.minecraft.world.entity.ai.util;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class HoverRandomPos {
   public HoverRandomPos() {
      super();
   }

   public static @Nullable Vec3 getPos(final PathfinderMob mob, final int horizontalDist, final int verticalDist, final double xDir, final double zDir, final float maxXzRadiansDifference, final int hoverMaxHeight, final int hoverMinHeight) {
      boolean restrict = GoalUtils.mobRestricted(mob, (double)horizontalDist);
      return RandomPos.generateRandomPos(mob, (Supplier)(() -> {
         BlockPos direction = RandomPos.generateRandomDirectionWithinRadians(mob.getRandom(), 0.0, (double)horizontalDist, verticalDist, 0, xDir, zDir, (double)maxXzRadiansDifference);
         if (direction == null) {
            return null;
         } else {
            BlockPos pos = LandRandomPos.generateRandomPosTowardDirection(mob, (double)horizontalDist, restrict, direction);
            if (pos == null) {
               return null;
            } else {
               pos = RandomPos.moveUpToAboveSolid(pos, mob.getRandom().nextInt(hoverMaxHeight - hoverMinHeight + 1) + hoverMinHeight, mob.level().getMaxY(), (blockPos) -> GoalUtils.isSolid(mob, blockPos));
               return !GoalUtils.isWater(mob, pos) && !GoalUtils.hasMalus(mob, pos) ? pos : null;
            }
         }
      }));
   }
}
