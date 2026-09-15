package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class TrappedChestBlock extends ChestBlock {
   public TrappedChestBlock(final BlockBehaviour.Properties properties) {
      super(() -> BlockEntityTypes.TRAPPED_CHEST, SoundEvents.CHEST_OPEN, SoundEvents.CHEST_CLOSE, properties);
   }

   public BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new TrappedChestBlockEntity(worldPosition, blockState);
   }

   protected Stat<Identifier> getOpenChestStat() {
      return Stats.CUSTOM.get(Stats.TRIGGER_TRAPPED_CHEST);
   }

   protected boolean isSignalSource(final BlockState state) {
      return true;
   }

   protected int ownSignal(final BlockState state, final BlockGetter level, final BlockPos pos) {
      return Mth.clamp(ChestBlockEntity.getOpenCount(level, pos), 0, 15);
   }

   protected int getDirectSignal(final BlockState state, final BlockGetter level, final BlockPos pos, final Direction direction) {
      return direction == Direction.UP ? state.getSignal(level, pos, direction) : 0;
   }
}
