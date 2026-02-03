package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class SnowyDirtBlock extends Block {
   public static final MapCodec<SnowyDirtBlock> CODEC = simpleCodec(SnowyDirtBlock::new);
   public static final BooleanProperty SNOWY;

   protected MapCodec<? extends SnowyDirtBlock> codec() {
      return CODEC;
   }

   protected SnowyDirtBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(SNOWY, false));
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      return directionToNeighbour == Direction.UP ? (BlockState)state.setValue(SNOWY, isSnowySetting(neighbourState)) : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      BlockState aboveState = context.getLevel().getBlockState(context.getClickedPos().above());
      return (BlockState)this.defaultBlockState().setValue(SNOWY, isSnowySetting(aboveState));
   }

   protected static boolean isSnowySetting(final BlockState aboveState) {
      return aboveState.is(BlockTags.SNOW);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(SNOWY);
   }

   static {
      SNOWY = BlockStateProperties.SNOWY;
   }
}
