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

   public LeafLitterBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(this.getSegmentAmountProperty(), 1));
      this.shapes = this.makeShapes();
   }

   private Function<BlockState, VoxelShape> makeShapes() {
      return this.getShapeForEachState(this.getShapeCalculator(FACING, this.getSegmentAmountProperty()));
   }

   protected MapCodec<LeafLitterBlock> codec() {
      return CODEC;
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   public boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      return this.canBeReplaced(var1, var2, this.getSegmentAmountProperty()) ? true : super.canBeReplaced(var1, var2);
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockPos var4 = var3.below();
      return var2.getBlockState(var4).isFaceSturdy(var2, var4, Direction.UP);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)this.shapes.apply(var1);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.getStateForPlacement(var1, this, this.getSegmentAmountProperty(), FACING);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, this.getSegmentAmountProperty());
   }

   static {
      FACING = BlockStateProperties.HORIZONTAL_FACING;
   }
}
