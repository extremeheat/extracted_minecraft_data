package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TrappedChestBlock extends ChestBlock {
   public TrappedChestBlock(Block.Properties var1) {
      super(var1, () -> {
         return BlockEntityType.TRAPPED_CHEST;
      });
   }

   public BlockEntity newBlockEntity(BlockGetter var1) {
      return new TrappedChestBlockEntity();
   }

   protected Stat getOpenChestStat() {
      return Stats.CUSTOM.get(Stats.TRIGGER_TRAPPED_CHEST);
   }

   public boolean isSignalSource(BlockState var1) {
      return true;
   }

   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return Mth.clamp(ChestBlockEntity.getOpenCount(var2, var3), 0, 15);
   }

   public int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return var4 == Direction.UP ? var1.getSignal(var2, var3, var4) : 0;
   }
}
