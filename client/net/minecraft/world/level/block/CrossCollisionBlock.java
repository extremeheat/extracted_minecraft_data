package net.minecraft.world.level.block;

import com.google.common.collect.UnmodifiableIterator;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class CrossCollisionBlock extends Block implements SimpleWaterloggedBlock {
   public static final BooleanProperty NORTH = PipeBlock.NORTH;
   public static final BooleanProperty EAST = PipeBlock.EAST;
   public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
   public static final BooleanProperty WEST = PipeBlock.WEST;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION
      .entrySet()
      .stream()
      .filter(var0 -> var0.getKey().getAxis().isHorizontal())
      .collect(Util.toMap());
   protected final VoxelShape[] collisionShapeByIndex;
   protected final VoxelShape[] shapeByIndex;
   private final Object2IntMap<BlockState> stateToIndex = new Object2IntOpenHashMap();

   protected CrossCollisionBlock(float var1, float var2, float var3, float var4, float var5, BlockBehaviour.Properties var6) {
      super(var6);
      this.collisionShapeByIndex = this.makeShapes(var1, var2, var5, 0.0F, var5);
      this.shapeByIndex = this.makeShapes(var1, var2, var3, 0.0F, var4);
      UnmodifiableIterator var7 = this.stateDefinition.getPossibleStates().iterator();

      while (var7.hasNext()) {
         BlockState var8 = (BlockState)var7.next();
         this.getAABBIndex(var8);
      }
   }

   @Override
   protected abstract MapCodec<? extends CrossCollisionBlock> codec();

   protected VoxelShape[] makeShapes(float var1, float var2, float var3, float var4, float var5) {
      float var6 = 8.0F - var1;
      float var7 = 8.0F + var1;
      float var8 = 8.0F - var2;
      float var9 = 8.0F + var2;
      VoxelShape var10 = Block.box((double)var6, 0.0, (double)var6, (double)var7, (double)var3, (double)var7);
      VoxelShape var11 = Block.box((double)var8, (double)var4, 0.0, (double)var9, (double)var5, (double)var9);
      VoxelShape var12 = Block.box((double)var8, (double)var4, (double)var8, (double)var9, (double)var5, 16.0);
      VoxelShape var13 = Block.box(0.0, (double)var4, (double)var8, (double)var9, (double)var5, (double)var9);
      VoxelShape var14 = Block.box((double)var8, (double)var4, (double)var8, 16.0, (double)var5, (double)var9);
      VoxelShape var15 = Shapes.or(var11, var14);
      VoxelShape var16 = Shapes.or(var12, var13);
      VoxelShape[] var17 = new VoxelShape[]{
         Shapes.empty(),
         var12,
         var13,
         var16,
         var11,
         Shapes.or(var12, var11),
         Shapes.or(var13, var11),
         Shapes.or(var16, var11),
         var14,
         Shapes.or(var12, var14),
         Shapes.or(var13, var14),
         Shapes.or(var16, var14),
         var15,
         Shapes.or(var12, var15),
         Shapes.or(var13, var15),
         Shapes.or(var16, var15)
      };

      for (int var18 = 0; var18 < 16; var18++) {
         var17[var18] = Shapes.or(var10, var17[var18]);
      }

      return var17;
   }

   @Override
   protected boolean propagatesSkylightDown(BlockState var1) {
      return !var1.getValue(WATERLOGGED);
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.shapeByIndex[this.getAABBIndex(var1)];
   }

   @Override
   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.collisionShapeByIndex[this.getAABBIndex(var1)];
   }

   private static int indexFor(Direction var0) {
      return 1 << var0.get2DDataValue();
   }

   protected int getAABBIndex(BlockState var1) {
      return this.stateToIndex.computeIntIfAbsent(var1, var0 -> {
         int var1x = 0;
         if (var0.getValue(NORTH)) {
            var1x |= indexFor(Direction.NORTH);
         }

         if (var0.getValue(EAST)) {
            var1x |= indexFor(Direction.EAST);
         }

         if (var0.getValue(SOUTH)) {
            var1x |= indexFor(Direction.SOUTH);
         }

         if (var0.getValue(WEST)) {
            var1x |= indexFor(Direction.WEST);
         }

         return var1x;
      });
   }

   @Override
   protected FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Override
   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   @Override
   protected BlockState rotate(BlockState var1, Rotation var2) {
      switch (var2) {
         case CLOCKWISE_180:
            return var1.setValue(NORTH, var1.getValue(SOUTH))
               .setValue(EAST, var1.getValue(WEST))
               .setValue(SOUTH, var1.getValue(NORTH))
               .setValue(WEST, var1.getValue(EAST));
         case COUNTERCLOCKWISE_90:
            return var1.setValue(NORTH, var1.getValue(EAST))
               .setValue(EAST, var1.getValue(SOUTH))
               .setValue(SOUTH, var1.getValue(WEST))
               .setValue(WEST, var1.getValue(NORTH));
         case CLOCKWISE_90:
            return var1.setValue(NORTH, var1.getValue(WEST))
               .setValue(EAST, var1.getValue(NORTH))
               .setValue(SOUTH, var1.getValue(EAST))
               .setValue(WEST, var1.getValue(SOUTH));
         default:
            return var1;
      }
   }

   @Override
   protected BlockState mirror(BlockState var1, Mirror var2) {
      switch (var2) {
         case LEFT_RIGHT:
            return var1.setValue(NORTH, var1.getValue(SOUTH)).setValue(SOUTH, var1.getValue(NORTH));
         case FRONT_BACK:
            return var1.setValue(EAST, var1.getValue(WEST)).setValue(WEST, var1.getValue(EAST));
         default:
            return super.mirror(var1, var2);
      }
   }
}
