package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

public class BreathAirGoal extends Goal {
   private final PathfinderMob mob;

   public BreathAirGoal(PathfinderMob var1) {
      super();
      this.mob = var1;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   public boolean canUse() {
      return this.mob.getAirSupply() < 140;
   }

   public boolean canContinueToUse() {
      return this.canUse();
   }

   public boolean isInterruptable() {
      return false;
   }

   public void start() {
      this.findAirPosition();
   }

   private void findAirPosition() {
      Iterable var1 = BlockPos.betweenClosed(Mth.floor(this.mob.getX() - 1.0D), this.mob.getBlockY(), Mth.floor(this.mob.getZ() - 1.0D), Mth.floor(this.mob.getX() + 1.0D), Mth.floor(this.mob.getY() + 8.0D), Mth.floor(this.mob.getZ() + 1.0D));
      BlockPos var2 = null;
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         BlockPos var4 = (BlockPos)var3.next();
         if (this.givesAir(this.mob.level, var4)) {
            var2 = var4;
            break;
         }
      }

      if (var2 == null) {
         var2 = new BlockPos(this.mob.getX(), this.mob.getY() + 8.0D, this.mob.getZ());
      }

      this.mob.getNavigation().moveTo((double)var2.getX(), (double)(var2.getY() + 1), (double)var2.getZ(), 1.0D);
   }

   public void tick() {
      this.findAirPosition();
      this.mob.moveRelative(0.02F, new Vec3((double)this.mob.xxa, (double)this.mob.yya, (double)this.mob.zza));
      this.mob.move(MoverType.SELF, this.mob.getDeltaMovement());
   }

   private boolean givesAir(LevelReader var1, BlockPos var2) {
      BlockState var3 = var1.getBlockState(var2);
      return (var1.getFluidState(var2).isEmpty() || var3.is(Blocks.BUBBLE_COLUMN)) && var3.isPathfindable(var1, var2, PathComputationType.LAND);
   }
}
