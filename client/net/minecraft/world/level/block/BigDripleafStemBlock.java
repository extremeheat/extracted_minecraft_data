package net.minecraft.world.level.block;

import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.BlockUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BigDripleafStemBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock, BonemealableBlock {
   private static final BooleanProperty WATERLOGGED;
   private static final Map<Direction, VoxelShape> SHAPES;

   protected BigDripleafStemBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, false)).setValue(FACING, Direction.NORTH));
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return (VoxelShape)SHAPES.get(state.getValue(FACING));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(WATERLOGGED, FACING);
   }

   protected FluidState getFluidState(final BlockState state) {
      return (Boolean)state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      BlockPos belowPos = pos.below();
      BlockState belowState = level.getBlockState(belowPos);
      BlockState aboveState = level.getBlockState(pos.above());
      return (belowState.is(this) || belowState.is(BlockTags.SUPPORTS_BIG_DRIPLEAF)) && (aboveState.is(this) || aboveState.is(Blocks.BIG_DRIPLEAF));
   }

   protected static boolean place(final LevelAccessor level, final BlockPos pos, final FluidState fluidState, final Direction facing) {
      BlockState newState = (BlockState)((BlockState)Blocks.BIG_DRIPLEAF_STEM.defaultBlockState().setValue(WATERLOGGED, fluidState.isSourceOfType(Fluids.WATER))).setValue(FACING, facing);
      return level.setBlockAndUpdate(pos, newState);
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if ((directionToNeighbour == Direction.DOWN || directionToNeighbour == Direction.UP) && !state.canSurvive(level, pos)) {
         ticks.scheduleTick(pos, (Block)this, 1);
      }

      if ((Boolean)state.getValue(WATERLOGGED)) {
         ticks.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (!state.canSurvive(level, pos)) {
         level.destroyBlock(pos, true);
      }

   }

   public boolean isValidBonemealTarget(final LevelReader level, final BlockPos pos, final BlockState state, final BonemealSource source) {
      Optional<BlockPos> headPos = BlockUtil.getTopConnectedBlock(level, pos, state.getBlock(), Direction.UP, Blocks.BIG_DRIPLEAF);
      return headPos.filter((blockPos) -> BigDripleafBlock.canPlaceAt(level, blockPos.above())).isPresent();
   }

   public boolean isBonemealSuccess(final Level level, final RandomSource random, final BlockPos pos, final BlockState state, final BonemealSource source) {
      return true;
   }

   public void performBonemeal(final ServerLevel level, final RandomSource random, final BlockPos pos, final BlockState state, final BonemealSource source) {
      Optional<BlockPos> forwardPos = BlockUtil.getTopConnectedBlock(level, pos, state.getBlock(), Direction.UP, Blocks.BIG_DRIPLEAF);
      if (!forwardPos.isEmpty()) {
         BlockPos headPos = (BlockPos)forwardPos.get();
         BlockPos placeHeadPos = headPos.above();
         Direction facing = (Direction)state.getValue(FACING);
         place(level, headPos, level.getFluidState(headPos), facing);
         BigDripleafBlock.place(level, placeHeadPos, level.getFluidState(placeHeadPos), facing);
      }
   }

   protected ItemStack getCloneItemStack(final LevelReader level, final BlockPos pos, final BlockState state, final boolean includeData) {
      return new ItemStack(Blocks.BIG_DRIPLEAF);
   }

   static {
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      SHAPES = Shapes.rotateHorizontal(Block.column(6.0, 0.0, 16.0).move(0.0, 0.0, 0.25).optimize());
   }
}
