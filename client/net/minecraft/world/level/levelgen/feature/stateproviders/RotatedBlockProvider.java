package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class RotatedBlockProvider extends BlockStateProvider {
   public static final MapCodec<RotatedBlockProvider> CODEC;
   private final Block block;

   public RotatedBlockProvider(final Block block) {
      super();
      this.block = block;
   }

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.ROTATED_BLOCK_PROVIDER;
   }

   public BlockState getState(final LevelAccessor level, final RandomSource random, final BlockPos pos) {
      Direction.Axis randomAxis = Direction.Axis.getRandom(random);
      return (BlockState)this.block.defaultBlockState().trySetValue(RotatedPillarBlock.AXIS, randomAxis);
   }

   static {
      CODEC = BlockState.CODEC.fieldOf("state").xmap(BlockBehaviour.BlockStateBase::getBlock, Block::defaultBlockState).xmap(RotatedBlockProvider::new, (p) -> p.block);
   }
}
