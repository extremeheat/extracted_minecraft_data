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

   protected PipeBlock(final float size, final BlockBehaviour.Properties properties) {
      super(properties);
      this.shapes = this.makeShapes(size);
   }

   protected abstract MapCodec<? extends PipeBlock> codec();

   private Function<BlockState, VoxelShape> makeShapes(final float size) {
      VoxelShape core = Block.cube((double)size);
      Map<Direction, VoxelShape> shapes = Shapes.rotateAll(Block.boxZ((double)size, 0.0, 8.0));
      return this.getShapeForEachState((state) -> {
         VoxelShape shape = core;

         for(Map.Entry<Direction, BooleanProperty> entry : PROPERTY_BY_DIRECTION.entrySet()) {
            if ((Boolean)state.getValue((Property)entry.getValue())) {
               shape = Shapes.or((VoxelShape)shapes.get(entry.getKey()), shape);
            }
         }

         return shape;
      });
   }

   protected boolean propagatesSkylightDown(final BlockState state) {
      return false;
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return (VoxelShape)this.shapes.apply(state);
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
