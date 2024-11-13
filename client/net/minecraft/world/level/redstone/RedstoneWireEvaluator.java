package net.minecraft.world.level.redstone;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;

public abstract class RedstoneWireEvaluator {
   protected final RedStoneWireBlock wireBlock;

   protected RedstoneWireEvaluator(RedStoneWireBlock var1) {
      super();
      this.wireBlock = var1;
   }

   public abstract void updatePowerStrength(Level var1, BlockPos var2, BlockState var3, @Nullable Orientation var4, boolean var5);

   protected int getBlockSignal(Level var1, BlockPos var2) {
      return this.wireBlock.getBlockSignal(var1, var2);
   }

   protected int getWireSignal(BlockPos var1, BlockState var2) {
      return var2.is(this.wireBlock) ? (Integer)var2.getValue(RedStoneWireBlock.POWER) : 0;
   }

   protected int getIncomingWireSignal(Level var1, BlockPos var2) {
      int var3 = 0;

      for(Direction var5 : Direction.Plane.HORIZONTAL) {
         BlockPos var6 = var2.relative(var5);
         BlockState var7 = var1.getBlockState(var6);
         var3 = Math.max(var3, this.getWireSignal(var6, var7));
         BlockPos var8 = var2.above();
         if (var7.isRedstoneConductor(var1, var6) && !var1.getBlockState(var8).isRedstoneConductor(var1, var8)) {
            BlockPos var10 = var6.above();
            var3 = Math.max(var3, this.getWireSignal(var10, var1.getBlockState(var10)));
         } else if (!var7.isRedstoneConductor(var1, var6)) {
            BlockPos var9 = var6.below();
            var3 = Math.max(var3, this.getWireSignal(var9, var1.getBlockState(var9)));
         }
      }

      return Math.max(0, var3 - 1);
   }
}
