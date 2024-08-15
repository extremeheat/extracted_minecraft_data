package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;

public class TrappedChestBlockEntity extends ChestBlockEntity {
   public TrappedChestBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.TRAPPED_CHEST, var1, var2);
   }

   @Override
   protected void signalOpenCount(Level var1, BlockPos var2, BlockState var3, int var4, int var5) {
      super.signalOpenCount(var1, var2, var3, var4, var5);
      if (var4 != var5) {
         Orientation var6 = ExperimentalRedstoneUtils.randomOrientation(var1, var3.getValue(TrappedChestBlock.FACING).getOpposite(), Direction.UP);
         Block var7 = var3.getBlock();
         var1.updateNeighborsAt(var2, var7, var6);
         var1.updateNeighborsAt(var2.below(), var7, var6);
      }
   }
}
