package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

public class RandomSwimmingGoal extends RandomStrollGoal {
   public RandomSwimmingGoal(PathfinderMob var1, double var2, int var4) {
      super(var1, var2, var4);
   }

   @Nullable
   protected Vec3 getPosition() {
      Vec3 var1 = DefaultRandomPos.getPos(this.mob, 10, 7);

      for(int var2 = 0; var1 != null && !this.mob.level.getBlockState(new BlockPos(var1)).isPathfindable(this.mob.level, new BlockPos(var1), PathComputationType.WATER) && var2++ < 10; var1 = DefaultRandomPos.getPos(this.mob, 10, 7)) {
      }

      return var1;
   }
}
