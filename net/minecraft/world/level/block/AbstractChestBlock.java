package net.minecraft.world.level.block;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractChestBlock extends BaseEntityBlock {
   protected final Supplier blockEntityType;

   protected AbstractChestBlock(Block.Properties var1, Supplier var2) {
      super(var1);
      this.blockEntityType = var2;
   }

   public abstract DoubleBlockCombiner.NeighborCombineResult combine(BlockState var1, Level var2, BlockPos var3, boolean var4);
}
