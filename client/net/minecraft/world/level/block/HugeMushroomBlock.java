package net.minecraft.world.level.block;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class HugeMushroomBlock extends Block {
   public static final BooleanProperty NORTH;
   public static final BooleanProperty EAST;
   public static final BooleanProperty SOUTH;
   public static final BooleanProperty WEST;
   public static final BooleanProperty UP;
   public static final BooleanProperty DOWN;
   private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION;

   public HugeMushroomBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, true)).setValue(EAST, true)).setValue(SOUTH, true)).setValue(WEST, true)).setValue(UP, true)).setValue(DOWN, true));
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(DOWN, this != var2.getBlockState(var3.below()).getBlock())).setValue(UP, this != var2.getBlockState(var3.above()).getBlock())).setValue(NORTH, this != var2.getBlockState(var3.north()).getBlock())).setValue(EAST, this != var2.getBlockState(var3.east()).getBlock())).setValue(SOUTH, this != var2.getBlockState(var3.south()).getBlock())).setValue(WEST, this != var2.getBlockState(var3.west()).getBlock());
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var3.getBlock() == this ? (BlockState)var1.setValue((Property)PROPERTY_BY_DIRECTION.get(var2), false) : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)var1.setValue((Property)PROPERTY_BY_DIRECTION.get(var2.rotate(Direction.NORTH)), var1.getValue(NORTH))).setValue((Property)PROPERTY_BY_DIRECTION.get(var2.rotate(Direction.SOUTH)), var1.getValue(SOUTH))).setValue((Property)PROPERTY_BY_DIRECTION.get(var2.rotate(Direction.EAST)), var1.getValue(EAST))).setValue((Property)PROPERTY_BY_DIRECTION.get(var2.rotate(Direction.WEST)), var1.getValue(WEST))).setValue((Property)PROPERTY_BY_DIRECTION.get(var2.rotate(Direction.UP)), var1.getValue(UP))).setValue((Property)PROPERTY_BY_DIRECTION.get(var2.rotate(Direction.DOWN)), var1.getValue(DOWN));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)var1.setValue((Property)PROPERTY_BY_DIRECTION.get(var2.mirror(Direction.NORTH)), var1.getValue(NORTH))).setValue((Property)PROPERTY_BY_DIRECTION.get(var2.mirror(Direction.SOUTH)), var1.getValue(SOUTH))).setValue((Property)PROPERTY_BY_DIRECTION.get(var2.mirror(Direction.EAST)), var1.getValue(EAST))).setValue((Property)PROPERTY_BY_DIRECTION.get(var2.mirror(Direction.WEST)), var1.getValue(WEST))).setValue((Property)PROPERTY_BY_DIRECTION.get(var2.mirror(Direction.UP)), var1.getValue(UP))).setValue((Property)PROPERTY_BY_DIRECTION.get(var2.mirror(Direction.DOWN)), var1.getValue(DOWN));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
   }

   static {
      NORTH = PipeBlock.NORTH;
      EAST = PipeBlock.EAST;
      SOUTH = PipeBlock.SOUTH;
      WEST = PipeBlock.WEST;
      UP = PipeBlock.UP;
      DOWN = PipeBlock.DOWN;
      PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;
   }
}
