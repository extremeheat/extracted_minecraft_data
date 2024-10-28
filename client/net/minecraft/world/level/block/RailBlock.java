package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;

public class RailBlock extends BaseRailBlock {
   public static final MapCodec<RailBlock> CODEC = simpleCodec(RailBlock::new);
   public static final EnumProperty<RailShape> SHAPE;

   public MapCodec<RailBlock> codec() {
      return CODEC;
   }

   protected RailBlock(BlockBehaviour.Properties var1) {
      super(false, var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(SHAPE, RailShape.NORTH_SOUTH)).setValue(WATERLOGGED, false));
   }

   protected void updateState(BlockState var1, Level var2, BlockPos var3, Block var4) {
      if (var4.defaultBlockState().isSignalSource() && (new RailState(var2, var3, var1)).countPotentialConnections() == 3) {
         this.updateDir(var2, var3, var1, false);
      }

   }

   public Property<RailShape> getShapeProperty() {
      return SHAPE;
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      RailShape var3 = (RailShape)var1.getValue(SHAPE);
      EnumProperty var10001 = SHAPE;
      RailShape var10002;
      switch (var2) {
         case CLOCKWISE_180:
            switch (var3) {
               case NORTH_SOUTH:
                  var10002 = RailShape.NORTH_SOUTH;
                  return (BlockState)var1.setValue(var10001, var10002);
               case EAST_WEST:
                  var10002 = RailShape.EAST_WEST;
                  return (BlockState)var1.setValue(var10001, var10002);
               case ASCENDING_EAST:
                  var10002 = RailShape.ASCENDING_WEST;
                  return (BlockState)var1.setValue(var10001, var10002);
               case ASCENDING_WEST:
                  var10002 = RailShape.ASCENDING_EAST;
                  return (BlockState)var1.setValue(var10001, var10002);
               case ASCENDING_NORTH:
                  var10002 = RailShape.ASCENDING_SOUTH;
                  return (BlockState)var1.setValue(var10001, var10002);
               case ASCENDING_SOUTH:
                  var10002 = RailShape.ASCENDING_NORTH;
                  return (BlockState)var1.setValue(var10001, var10002);
               case SOUTH_EAST:
                  var10002 = RailShape.NORTH_WEST;
                  return (BlockState)var1.setValue(var10001, var10002);
               case SOUTH_WEST:
                  var10002 = RailShape.NORTH_EAST;
                  return (BlockState)var1.setValue(var10001, var10002);
               case NORTH_WEST:
                  var10002 = RailShape.SOUTH_EAST;
                  return (BlockState)var1.setValue(var10001, var10002);
               case NORTH_EAST:
                  var10002 = RailShape.SOUTH_WEST;
                  return (BlockState)var1.setValue(var10001, var10002);
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }
         case COUNTERCLOCKWISE_90:
            switch (var3) {
               case NORTH_SOUTH:
                  var10002 = RailShape.EAST_WEST;
                  return (BlockState)var1.setValue(var10001, var10002);
               case EAST_WEST:
                  var10002 = RailShape.NORTH_SOUTH;
                  return (BlockState)var1.setValue(var10001, var10002);
               case ASCENDING_EAST:
                  var10002 = RailShape.ASCENDING_NORTH;
                  return (BlockState)var1.setValue(var10001, var10002);
               case ASCENDING_WEST:
                  var10002 = RailShape.ASCENDING_SOUTH;
                  return (BlockState)var1.setValue(var10001, var10002);
               case ASCENDING_NORTH:
                  var10002 = RailShape.ASCENDING_WEST;
                  return (BlockState)var1.setValue(var10001, var10002);
               case ASCENDING_SOUTH:
                  var10002 = RailShape.ASCENDING_EAST;
                  return (BlockState)var1.setValue(var10001, var10002);
               case SOUTH_EAST:
                  var10002 = RailShape.NORTH_EAST;
                  return (BlockState)var1.setValue(var10001, var10002);
               case SOUTH_WEST:
                  var10002 = RailShape.SOUTH_EAST;
                  return (BlockState)var1.setValue(var10001, var10002);
               case NORTH_WEST:
                  var10002 = RailShape.SOUTH_WEST;
                  return (BlockState)var1.setValue(var10001, var10002);
               case NORTH_EAST:
                  var10002 = RailShape.NORTH_WEST;
                  return (BlockState)var1.setValue(var10001, var10002);
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }
         case CLOCKWISE_90:
            switch (var3) {
               case NORTH_SOUTH:
                  var10002 = RailShape.EAST_WEST;
                  return (BlockState)var1.setValue(var10001, var10002);
               case EAST_WEST:
                  var10002 = RailShape.NORTH_SOUTH;
                  return (BlockState)var1.setValue(var10001, var10002);
               case ASCENDING_EAST:
                  var10002 = RailShape.ASCENDING_SOUTH;
                  return (BlockState)var1.setValue(var10001, var10002);
               case ASCENDING_WEST:
                  var10002 = RailShape.ASCENDING_NORTH;
                  return (BlockState)var1.setValue(var10001, var10002);
               case ASCENDING_NORTH:
                  var10002 = RailShape.ASCENDING_EAST;
                  return (BlockState)var1.setValue(var10001, var10002);
               case ASCENDING_SOUTH:
                  var10002 = RailShape.ASCENDING_WEST;
                  return (BlockState)var1.setValue(var10001, var10002);
               case SOUTH_EAST:
                  var10002 = RailShape.SOUTH_WEST;
                  return (BlockState)var1.setValue(var10001, var10002);
               case SOUTH_WEST:
                  var10002 = RailShape.NORTH_WEST;
                  return (BlockState)var1.setValue(var10001, var10002);
               case NORTH_WEST:
                  var10002 = RailShape.NORTH_EAST;
                  return (BlockState)var1.setValue(var10001, var10002);
               case NORTH_EAST:
                  var10002 = RailShape.SOUTH_EAST;
                  return (BlockState)var1.setValue(var10001, var10002);
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }
         default:
            var10002 = var3;
            return (BlockState)var1.setValue(var10001, var10002);
      }
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      RailShape var3 = (RailShape)var1.getValue(SHAPE);
      switch (var2) {
         case LEFT_RIGHT:
            switch (var3) {
               case ASCENDING_NORTH -> {
                  return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
               }
               case ASCENDING_SOUTH -> {
                  return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_NORTH);
               }
               case SOUTH_EAST -> {
                  return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_EAST);
               }
               case SOUTH_WEST -> {
                  return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_WEST);
               }
               case NORTH_WEST -> {
                  return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_WEST);
               }
               case NORTH_EAST -> {
                  return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_EAST);
               }
               default -> {
                  return super.mirror(var1, var2);
               }
            }
         case FRONT_BACK:
            switch (var3) {
               case ASCENDING_EAST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_WEST);
               case ASCENDING_WEST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_EAST);
               case ASCENDING_NORTH:
               case ASCENDING_SOUTH:
               default:
                  break;
               case SOUTH_EAST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_WEST);
               case SOUTH_WEST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_EAST);
               case NORTH_WEST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_EAST);
               case NORTH_EAST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_WEST);
            }
      }

      return super.mirror(var1, var2);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(SHAPE, WATERLOGGED);
   }

   static {
      SHAPE = BlockStateProperties.RAIL_SHAPE;
   }
}
