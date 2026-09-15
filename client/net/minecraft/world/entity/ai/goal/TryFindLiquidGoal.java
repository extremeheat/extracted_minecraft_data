package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.material.Fluid;

public class TryFindLiquidGoal extends Goal {
   private final PathfinderMob mob;
   private final TagKey<Fluid> fluidTag;

   public TryFindLiquidGoal(final PathfinderMob mob, final TagKey<Fluid> fluidTag) {
      super();
      this.mob = mob;
      this.fluidTag = fluidTag;
   }

   public boolean canUse() {
      return this.mob.onGround() && !this.mob.level().getFluidState(this.mob.blockPosition()).is(this.fluidTag);
   }

   public void start() {
      BlockPos fluidPos = null;

      for(BlockPos pos : BlockPos.betweenClosed(Mth.floor(this.mob.getX() - 2.0), Mth.floor(this.mob.getY() - 2.0), Mth.floor(this.mob.getZ() - 2.0), Mth.floor(this.mob.getX() + 2.0), this.mob.getBlockY(), Mth.floor(this.mob.getZ() + 2.0))) {
         if (this.mob.level().getFluidState(pos).is(this.fluidTag)) {
            fluidPos = pos;
            break;
         }
      }

      if (fluidPos != null) {
         this.mob.getMoveControl().setWantedPosition((double)fluidPos.getX(), (double)fluidPos.getY(), (double)fluidPos.getZ(), 1.0);
      }

   }
}
