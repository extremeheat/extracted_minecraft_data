package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FlowerBedBlock extends BushBlock implements BonemealableBlock, SegmentableBlock {
   public static final MapCodec<FlowerBedBlock> CODEC = simpleCodec(FlowerBedBlock::new);
   public static final EnumProperty<Direction> FACING;
   public static final IntegerProperty AMOUNT;
   private final Function<BlockState, VoxelShape> shapes;

   public MapCodec<FlowerBedBlock> codec() {
      return CODEC;
   }

   protected FlowerBedBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(AMOUNT, 1));
      this.shapes = this.makeShapes();
   }

   private Function<BlockState, VoxelShape> makeShapes() {
      return this.getShapeForEachState(this.getShapeCalculator(FACING, AMOUNT));
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   public boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      return this.canBeReplaced(var1, var2, AMOUNT) ? true : super.canBeReplaced(var1, var2);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)this.shapes.apply(var1);
   }

   public double getShapeHeight() {
      return 3.0;
   }

   public IntegerProperty getSegmentAmountProperty() {
      return AMOUNT;
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.getStateForPlacement(var1, this, AMOUNT, FACING);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, AMOUNT);
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return true;
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      int var5 = (Integer)var4.getValue(AMOUNT);
      if (var5 < 4) {
         var1.setBlock(var3, (BlockState)var4.setValue(AMOUNT, var5 + 1), 2);
      } else {
         popResource(var1, var3, new ItemStack(this));
      }

   }

   static {
      FACING = BlockStateProperties.HORIZONTAL_FACING;
      AMOUNT = BlockStateProperties.FLOWER_AMOUNT;
   }
}
