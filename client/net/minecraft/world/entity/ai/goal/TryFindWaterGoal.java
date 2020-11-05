package net.minecraft.world.entity.ai.goal;

import java.util.Iterator;
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

   public boolean canUse() {
      return this.mob.isOnGround() && !this.mob.level.getFluidState(this.mob.blockPosition()).is(FluidTags.WATER);
   }

   public void start() {
      BlockPos var1 = null;
      Iterable var2 = BlockPos.betweenClosed(Mth.floor(this.mob.getX() - 2.0D), Mth.floor(this.mob.getY() - 2.0D), Mth.floor(this.mob.getZ() - 2.0D), Mth.floor(this.mob.getX() + 2.0D), Mth.floor(this.mob.getY()), Mth.floor(this.mob.getZ() + 2.0D));
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         BlockPos var4 = (BlockPos)var3.next();
         if (this.mob.level.getFluidState(var4).is(FluidTags.WATER)) {
            var1 = var4;
            break;
         }
      }

      if (var1 != null) {
         this.mob.getMoveControl().setWantedPosition((double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), 1.0D);
      }

   }
}
