package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TrappedChestBlockEntity extends ChestBlockEntity {
   public TrappedChestBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.TRAPPED_CHEST, var1, var2);
   }

   protected void signalOpenCount(Level var1, BlockPos var2, BlockState var3, int var4, int var5) {
      super.signalOpenCount(var1, var2, var3, var4, var5);
      if (var4 != var5) {
         Block var6 = var3.getBlock();
         var1.updateNeighborsAt(var2, var6);
         var1.updateNeighborsAt(var2.below(), var6);
      }

   }
}
