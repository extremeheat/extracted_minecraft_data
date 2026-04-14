package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class ConduitBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<ConduitBlock> CODEC = simpleCodec(ConduitBlock::new);
   public static final BooleanProperty WATERLOGGED;
   private static final VoxelShape SHAPE;

   public MapCodec<ConduitBlock> codec() {
      return CODEC;
   }

   public ConduitBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, true));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(WATERLOGGED);
   }

   public BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new ConduitBlockEntity(worldPosition, blockState);
   }

   public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(final Level level, final BlockState blockState, final BlockEntityType<T> type) {
      return createTickerHelper(type, BlockEntityTypes.CONDUIT, level.isClientSide() ? ConduitBlockEntity::clientTick : ConduitBlockEntity::serverTick);
   }

   protected FluidState getFluidState(final BlockState state) {
      return (Boolean)state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         ticks.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPE;
   }

   public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
      FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
      return (BlockState)this.defaultBlockState().setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.isFull());
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return false;
   }

   static {
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      SHAPE = Block.cube(6.0);
   }
}
