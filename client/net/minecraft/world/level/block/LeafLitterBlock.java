package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LeafLitterBlock extends VegetationBlock implements SegmentableBlock {
   public static final MapCodec<LeafLitterBlock> CODEC = simpleCodec(LeafLitterBlock::new);
   public static final EnumProperty<Direction> FACING;
   private final Function<BlockState, VoxelShape> shapes;

   public LeafLitterBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(this.getSegmentAmountProperty(), 1));
      this.shapes = this.makeShapes();
   }

   private Function<BlockState, VoxelShape> makeShapes() {
      return this.getShapeForEachState(this.getShapeCalculator(FACING, this.getSegmentAmountProperty()));
   }

   protected MapCodec<LeafLitterBlock> codec() {
      return CODEC;
   }

   public BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(final BlockState state, final Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   public boolean canBeReplaced(final BlockState state, final BlockPlaceContext context) {
      return this.canBeReplaced(state, context, this.getSegmentAmountProperty()) ? true : super.canBeReplaced(state, context);
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      BlockPos belowPos = pos.below();
      return level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP);
   }

   public VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return (VoxelShape)this.shapes.apply(state);
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      return this.getStateForPlacement(context, this, this.getSegmentAmountProperty(), FACING);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING, this.getSegmentAmountProperty());
   }

   static {
      FACING = BlockStateProperties.HORIZONTAL_FACING;
   }
}
