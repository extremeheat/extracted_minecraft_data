package net.minecraft.world.level.redstone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class InstantNeighborUpdater implements NeighborUpdater {
   private final Level level;

   public InstantNeighborUpdater(Level var1) {
      super();
      this.level = var1;
   }

   public void shapeUpdate(Direction var1, BlockState var2, BlockPos var3, BlockPos var4, int var5, int var6) {
      NeighborUpdater.executeShapeUpdate(this.level, var1, var2, var3, var4, var5, var6 - 1);
   }

   public void neighborChanged(BlockPos var1, Block var2, BlockPos var3) {
      BlockState var4 = this.level.getBlockState(var1);
      this.neighborChanged(var4, var1, var2, var3, false);
   }

   public void neighborChanged(BlockState var1, BlockPos var2, Block var3, BlockPos var4, boolean var5) {
      NeighborUpdater.executeUpdate(this.level, var1, var2, var3, var4, var5);
   }
}
