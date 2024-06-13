package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;

public class TryFindWaterGoal extends Goal {
   private final PathfinderMob mob;

   public TryFindWaterGoal(PathfinderMob var1) {
      super();
      this.mob = var1;
   }

   @Override
   public boolean canUse() {
      return this.mob.onGround() && !this.mob.level().getFluidState(this.mob.blockPosition()).is(FluidTags.WATER);
   }

   @Override
   public void start() {
      BlockPos var1 = null;

      for (BlockPos var4 : BlockPos.betweenClosed(
         Mth.floor(this.mob.getX() - 2.0),
         Mth.floor(this.mob.getY() - 2.0),
         Mth.floor(this.mob.getZ() - 2.0),
         Mth.floor(this.mob.getX() + 2.0),
         this.mob.getBlockY(),
         Mth.floor(this.mob.getZ() + 2.0)
      )) {
         if (this.mob.level().getFluidState(var4).is(FluidTags.WATER)) {
            var1 = var4;
            break;
         }
      }

      if (var1 != null) {
         this.mob.getMoveControl().setWantedPosition((double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), 1.0);
      }
   }
}
