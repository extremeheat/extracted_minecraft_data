package net.minecraft.world.level.block;

import com.google.common.base.Predicates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EndPortalFrameBlock extends Block {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty HAS_EYE = BlockStateProperties.EYE;
   protected static final VoxelShape BASE_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);
   protected static final VoxelShape EYE_SHAPE = Block.box(4.0, 13.0, 4.0, 12.0, 16.0, 12.0);
   protected static final VoxelShape FULL_SHAPE = Shapes.or(BASE_SHAPE, EYE_SHAPE);
   private static BlockPattern portalShape;

   public EndPortalFrameBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HAS_EYE, Boolean.valueOf(false)));
   }

   @Override
   public boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return var1.getValue(HAS_EYE) ? FULL_SHAPE : BASE_SHAPE;
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getOpposite()).setValue(HAS_EYE, Boolean.valueOf(false));
   }

   @Override
   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   @Override
   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return var1.getValue(HAS_EYE) ? 15 : 0;
   }

   @Override
   public BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(FACING, var2.rotate(var1.getValue(FACING)));
   }

   @Override
   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation(var1.getValue(FACING)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, HAS_EYE);
   }

   public static BlockPattern getOrCreatePortalShape() {
      if (portalShape == null) {
         portalShape = BlockPatternBuilder.start()
            .aisle("?vvv?", ">???<", ">???<", ">???<", "?^^^?")
            .where('?', BlockInWorld.hasState(BlockStatePredicate.ANY))
            .where(
               '^',
               BlockInWorld.hasState(
                  BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME)
                     .where(HAS_EYE, Predicates.equalTo(true))
                     .where(FACING, Predicates.equalTo(Direction.SOUTH))
               )
            )
            .where(
               '>',
               BlockInWorld.hasState(
                  BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME)
                     .where(HAS_EYE, Predicates.equalTo(true))
                     .where(FACING, Predicates.equalTo(Direction.WEST))
               )
            )
            .where(
               'v',
               BlockInWorld.hasState(
                  BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME)
                     .where(HAS_EYE, Predicates.equalTo(true))
                     .where(FACING, Predicates.equalTo(Direction.NORTH))
               )
            )
            .where(
               '<',
               BlockInWorld.hasState(
                  BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME)
                     .where(HAS_EYE, Predicates.equalTo(true))
                     .where(FACING, Predicates.equalTo(Direction.EAST))
               )
            )
            .build();
      }

      return portalShape;
   }

   @Override
   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }
}
