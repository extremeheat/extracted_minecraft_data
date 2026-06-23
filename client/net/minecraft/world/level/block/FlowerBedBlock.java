package net.minecraft.world.level.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

public class FlowerBedBlock extends VegetationBlock implements BonemealableBlock, SegmentableBlock {
   public static final MapCodec<FlowerBedBlock> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(propertiesCodec(), Codec.intRange(0, 16).fieldOf("shape_height").forGetter((b) -> b.shapeHeight)).apply(i, FlowerBedBlock::new));
   public static final EnumProperty<Direction> FACING;
   public static final IntegerProperty AMOUNT;
   private final int shapeHeight;
   private final Function<BlockState, VoxelShape> shapes;

   public MapCodec<FlowerBedBlock> codec() {
      return CODEC;
   }

   protected FlowerBedBlock(final BlockBehaviour.Properties properties, final int shapeHeight) {
      super(properties);
      this.shapeHeight = shapeHeight;
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(AMOUNT, 1));
      this.shapes = this.makeShapes();
   }

   private Function<BlockState, VoxelShape> makeShapes() {
      return this.getShapeForEachState(this.getShapeCalculator(FACING, AMOUNT));
   }

   public BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(final BlockState state, final Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   public boolean canBeReplaced(final BlockState state, final BlockPlaceContext context) {
      return this.canBeReplaced(state, context, AMOUNT) ? true : super.canBeReplaced(state, context);
   }

   public VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return (VoxelShape)this.shapes.apply(state);
   }

   public double getShapeHeight() {
      return (double)this.shapeHeight;
   }

   public IntegerProperty getSegmentAmountProperty() {
      return AMOUNT;
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      return this.getStateForPlacement(context, this, AMOUNT, FACING);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING, AMOUNT);
   }

   public boolean isValidBonemealTarget(final LevelReader level, final BlockPos pos, final BlockState state) {
      return true;
   }

   public boolean isBonemealSuccess(final Level level, final RandomSource random, final BlockPos pos, final BlockState state) {
      return true;
   }

   public void performBonemeal(final ServerLevel level, final RandomSource random, final BlockPos pos, final BlockState state) {
      int currentAmount = (Integer)state.getValue(AMOUNT);
      if (currentAmount < 4) {
         level.setBlock(pos, (BlockState)state.setValue(AMOUNT, currentAmount + 1), 2);
      } else {
         popResource(level, pos, new ItemStack(this));
      }

   }

   static {
      FACING = BlockStateProperties.HORIZONTAL_FACING;
      AMOUNT = BlockStateProperties.FLOWER_AMOUNT;
   }
}
