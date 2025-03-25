package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class PipeBlock extends Block {
   public static final BooleanProperty NORTH;
   public static final BooleanProperty EAST;
   public static final BooleanProperty SOUTH;
   public static final BooleanProperty WEST;
   public static final BooleanProperty UP;
   public static final BooleanProperty DOWN;
   public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION;
   private final Function<BlockState, VoxelShape> shapes;

   protected PipeBlock(float var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.shapes = this.makeShapes(var1);
   }

   protected abstract MapCodec<? extends PipeBlock> codec();

   private Function<BlockState, VoxelShape> makeShapes(float var1) {
      VoxelShape var2 = Block.cube((double)var1);
      Map var3 = Shapes.rotateAll(Block.boxZ((double)var1, 0.0, 8.0));
      return this.getShapeForEachState((var2x) -> {
         VoxelShape var3x = var2;

         for(Map.Entry var5 : PROPERTY_BY_DIRECTION.entrySet()) {
            if ((Boolean)var2x.getValue((Property)var5.getValue())) {
               var3x = Shapes.or((VoxelShape)var3.get(var5.getKey()), var3x);
            }
         }

         return var3x;
      });
   }

   protected boolean propagatesSkylightDown(BlockState var1) {
      return false;
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)this.shapes.apply(var1);
   }

   static {
      NORTH = BlockStateProperties.NORTH;
      EAST = BlockStateProperties.EAST;
      SOUTH = BlockStateProperties.SOUTH;
      WEST = BlockStateProperties.WEST;
      UP = BlockStateProperties.UP;
      DOWN = BlockStateProperties.DOWN;
      PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(Maps.newEnumMap(Map.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST, Direction.UP, UP, Direction.DOWN, DOWN)));
   }
}
