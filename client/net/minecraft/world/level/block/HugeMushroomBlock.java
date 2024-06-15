package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class HugeMushroomBlock extends Block {
   public static final MapCodec<HugeMushroomBlock> CODEC = simpleCodec(HugeMushroomBlock::new);
   public static final BooleanProperty NORTH = PipeBlock.NORTH;
   public static final BooleanProperty EAST = PipeBlock.EAST;
   public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
   public static final BooleanProperty WEST = PipeBlock.WEST;
   public static final BooleanProperty UP = PipeBlock.UP;
   public static final BooleanProperty DOWN = PipeBlock.DOWN;
   private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;

   @Override
   public MapCodec<HugeMushroomBlock> codec() {
      return CODEC;
   }

   public HugeMushroomBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(NORTH, Boolean.valueOf(true))
            .setValue(EAST, Boolean.valueOf(true))
            .setValue(SOUTH, Boolean.valueOf(true))
            .setValue(WEST, Boolean.valueOf(true))
            .setValue(UP, Boolean.valueOf(true))
            .setValue(DOWN, Boolean.valueOf(true))
      );
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      return this.defaultBlockState()
         .setValue(DOWN, Boolean.valueOf(!var2.getBlockState(var3.below()).is(this)))
         .setValue(UP, Boolean.valueOf(!var2.getBlockState(var3.above()).is(this)))
         .setValue(NORTH, Boolean.valueOf(!var2.getBlockState(var3.north()).is(this)))
         .setValue(EAST, Boolean.valueOf(!var2.getBlockState(var3.east()).is(this)))
         .setValue(SOUTH, Boolean.valueOf(!var2.getBlockState(var3.south()).is(this)))
         .setValue(WEST, Boolean.valueOf(!var2.getBlockState(var3.west()).is(this)));
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var3.is(this) ? var1.setValue(PROPERTY_BY_DIRECTION.get(var2), Boolean.valueOf(false)) : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   protected BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(PROPERTY_BY_DIRECTION.get(var2.rotate(Direction.NORTH)), var1.getValue(NORTH))
         .setValue(PROPERTY_BY_DIRECTION.get(var2.rotate(Direction.SOUTH)), var1.getValue(SOUTH))
         .setValue(PROPERTY_BY_DIRECTION.get(var2.rotate(Direction.EAST)), var1.getValue(EAST))
         .setValue(PROPERTY_BY_DIRECTION.get(var2.rotate(Direction.WEST)), var1.getValue(WEST))
         .setValue(PROPERTY_BY_DIRECTION.get(var2.rotate(Direction.UP)), var1.getValue(UP))
         .setValue(PROPERTY_BY_DIRECTION.get(var2.rotate(Direction.DOWN)), var1.getValue(DOWN));
   }

   @Override
   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.setValue(PROPERTY_BY_DIRECTION.get(var2.mirror(Direction.NORTH)), var1.getValue(NORTH))
         .setValue(PROPERTY_BY_DIRECTION.get(var2.mirror(Direction.SOUTH)), var1.getValue(SOUTH))
         .setValue(PROPERTY_BY_DIRECTION.get(var2.mirror(Direction.EAST)), var1.getValue(EAST))
         .setValue(PROPERTY_BY_DIRECTION.get(var2.mirror(Direction.WEST)), var1.getValue(WEST))
         .setValue(PROPERTY_BY_DIRECTION.get(var2.mirror(Direction.UP)), var1.getValue(UP))
         .setValue(PROPERTY_BY_DIRECTION.get(var2.mirror(Direction.DOWN)), var1.getValue(DOWN));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
   }
}
