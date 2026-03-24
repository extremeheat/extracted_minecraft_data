package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class BaseCoralWallFanBlock extends BaseCoralFanBlock {
   public static final MapCodec<BaseCoralWallFanBlock> CODEC = simpleCodec(BaseCoralWallFanBlock::new);
   public static final EnumProperty<Direction> FACING;
   private static final Map<Direction, VoxelShape> SHAPES;

   public MapCodec<? extends BaseCoralWallFanBlock> codec() {
      return CODEC;
   }

   protected BaseCoralWallFanBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, true));
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return (VoxelShape)SHAPES.get(state.getValue(FACING));
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING, WATERLOGGED);
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         ticks.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return directionToNeighbour.getOpposite() == state.getValue(FACING) && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : state;
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      Direction facing = (Direction)state.getValue(FACING);
      BlockPos relativePos = pos.relative(facing.getOpposite());
      BlockState relativeState = level.getBlockState(relativePos);
      return relativeState.isFaceSturdy(level, relativePos, facing);
   }

   public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
      BlockState state = super.getStateForPlacement(context);
      LevelReader level = context.getLevel();
      BlockPos pos = context.getClickedPos();
      Direction[] directions = context.getNearestLookingDirections();

      for(Direction direction : directions) {
         if (direction.getAxis().isHorizontal()) {
            state = (BlockState)state.setValue(FACING, direction.getOpposite());
            if (state.canSurvive(level, pos)) {
               return state;
            }
         }
      }

      return null;
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      SHAPES = Shapes.rotateHorizontal(Block.boxZ(16.0, 8.0, 5.0, 16.0));
   }
}
