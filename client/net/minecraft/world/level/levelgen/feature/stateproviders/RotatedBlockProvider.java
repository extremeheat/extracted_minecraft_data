package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class RotatedBlockProvider extends BlockStateProvider {
   public static final MapCodec<RotatedBlockProvider> CODEC = BlockState.CODEC
      .fieldOf("state")
      .xmap(BlockBehaviour.BlockStateBase::getBlock, Block::defaultBlockState)
      .xmap(RotatedBlockProvider::new, var0 -> var0.block);
   private final Block block;

   public RotatedBlockProvider(Block var1) {
      super();
      this.block = var1;
   }

   @Override
   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.ROTATED_BLOCK_PROVIDER;
   }

   @Override
   public BlockState getState(RandomSource var1, BlockPos var2) {
      Direction.Axis var3 = Direction.Axis.getRandom(var1);
      return this.block.defaultBlockState().trySetValue(RotatedPillarBlock.AXIS, var3);
   }
}
