package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class CrossCollisionBlock extends Block implements SimpleWaterloggedBlock {
   public static final BooleanProperty NORTH;
   public static final BooleanProperty EAST;
   public static final BooleanProperty SOUTH;
   public static final BooleanProperty WEST;
   public static final BooleanProperty WATERLOGGED;
   public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION;
   private final Function<BlockState, VoxelShape> collisionShapes;
   private final Function<BlockState, VoxelShape> shapes;

   protected CrossCollisionBlock(float var1, float var2, float var3, float var4, float var5, BlockBehaviour.Properties var6) {
      super(var6);
      this.collisionShapes = this.makeShapes(var1, var5, var3, 0.0F, var5);
      this.shapes = this.makeShapes(var1, var2, var3, 0.0F, var4);
   }

   protected abstract MapCodec<? extends CrossCollisionBlock> codec();

   protected Function<BlockState, VoxelShape> makeShapes(float var1, float var2, float var3, float var4, float var5) {
      VoxelShape var6 = Block.column((double)var1, 0.0, (double)var2);
      Map var7 = Shapes.rotateHorizontal(Block.boxZ((double)var3, (double)var4, (double)var5, 0.0, 8.0));
      return this.getShapeForEachState((var2x) -> {
         VoxelShape var3 = var6;

         for(Map.Entry var5 : PROPERTY_BY_DIRECTION.entrySet()) {
            if ((Boolean)var2x.getValue((Property)var5.getValue())) {
               var3 = Shapes.or(var3, (VoxelShape)var7.get(var5.getKey()));
            }
         }

         return var3;
      }, new Property[]{WATERLOGGED});
   }

   protected boolean propagatesSkylightDown(BlockState var1) {
      return !(Boolean)var1.getValue(WATERLOGGED);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)this.shapes.apply(var1);
   }

   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)this.collisionShapes.apply(var1);
   }

   protected FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      switch (var2) {
         case CLOCKWISE_180 -> {
            return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, (Boolean)var1.getValue(SOUTH))).setValue(EAST, (Boolean)var1.getValue(WEST))).setValue(SOUTH, (Boolean)var1.getValue(NORTH))).setValue(WEST, (Boolean)var1.getValue(EAST));
         }
         case COUNTERCLOCKWISE_90 -> {
            return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, (Boolean)var1.getValue(EAST))).setValue(EAST, (Boolean)var1.getValue(SOUTH))).setValue(SOUTH, (Boolean)var1.getValue(WEST))).setValue(WEST, (Boolean)var1.getValue(NORTH));
         }
         case CLOCKWISE_90 -> {
            return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, (Boolean)var1.getValue(WEST))).setValue(EAST, (Boolean)var1.getValue(NORTH))).setValue(SOUTH, (Boolean)var1.getValue(EAST))).setValue(WEST, (Boolean)var1.getValue(SOUTH));
         }
         default -> {
            return var1;
         }
      }
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      switch (var2) {
         case LEFT_RIGHT -> {
            return (BlockState)((BlockState)var1.setValue(NORTH, (Boolean)var1.getValue(SOUTH))).setValue(SOUTH, (Boolean)var1.getValue(NORTH));
         }
         case FRONT_BACK -> {
            return (BlockState)((BlockState)var1.setValue(EAST, (Boolean)var1.getValue(WEST))).setValue(WEST, (Boolean)var1.getValue(EAST));
         }
         default -> {
            return super.mirror(var1, var2);
         }
      }
   }

   static {
      NORTH = PipeBlock.NORTH;
      EAST = PipeBlock.EAST;
      SOUTH = PipeBlock.SOUTH;
      WEST = PipeBlock.WEST;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      PROPERTY_BY_DIRECTION = (Map)PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((var0) -> ((Direction)var0.getKey()).getAxis().isHorizontal()).collect(Util.toMap());
   }
}
