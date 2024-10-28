package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.Util;
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
   private static final Direction[] DIRECTIONS = Direction.values();
   public static final BooleanProperty NORTH;
   public static final BooleanProperty EAST;
   public static final BooleanProperty SOUTH;
   public static final BooleanProperty WEST;
   public static final BooleanProperty UP;
   public static final BooleanProperty DOWN;
   public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION;
   protected final VoxelShape[] shapeByIndex;

   protected PipeBlock(float var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.shapeByIndex = this.makeShapes(var1);
   }

   protected abstract MapCodec<? extends PipeBlock> codec();

   private VoxelShape[] makeShapes(float var1) {
      float var2 = 0.5F - var1;
      float var3 = 0.5F + var1;
      VoxelShape var4 = Block.box((double)(var2 * 16.0F), (double)(var2 * 16.0F), (double)(var2 * 16.0F), (double)(var3 * 16.0F), (double)(var3 * 16.0F), (double)(var3 * 16.0F));
      VoxelShape[] var5 = new VoxelShape[DIRECTIONS.length];

      for(int var6 = 0; var6 < DIRECTIONS.length; ++var6) {
         Direction var7 = DIRECTIONS[var6];
         var5[var6] = Shapes.box(0.5 + Math.min((double)(-var1), (double)var7.getStepX() * 0.5), 0.5 + Math.min((double)(-var1), (double)var7.getStepY() * 0.5), 0.5 + Math.min((double)(-var1), (double)var7.getStepZ() * 0.5), 0.5 + Math.max((double)var1, (double)var7.getStepX() * 0.5), 0.5 + Math.max((double)var1, (double)var7.getStepY() * 0.5), 0.5 + Math.max((double)var1, (double)var7.getStepZ() * 0.5));
      }

      VoxelShape[] var10 = new VoxelShape[64];

      for(int var11 = 0; var11 < 64; ++var11) {
         VoxelShape var8 = var4;

         for(int var9 = 0; var9 < DIRECTIONS.length; ++var9) {
            if ((var11 & 1 << var9) != 0) {
               var8 = Shapes.or(var8, var5[var9]);
            }
         }

         var10[var11] = var8;
      }

      return var10;
   }

   protected boolean propagatesSkylightDown(BlockState var1, BlockGetter var2, BlockPos var3) {
      return false;
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.shapeByIndex[this.getAABBIndex(var1)];
   }

   protected int getAABBIndex(BlockState var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < DIRECTIONS.length; ++var3) {
         if ((Boolean)var1.getValue((Property)PROPERTY_BY_DIRECTION.get(DIRECTIONS[var3]))) {
            var2 |= 1 << var3;
         }
      }

      return var2;
   }

   static {
      NORTH = BlockStateProperties.NORTH;
      EAST = BlockStateProperties.EAST;
      SOUTH = BlockStateProperties.SOUTH;
      WEST = BlockStateProperties.WEST;
      UP = BlockStateProperties.UP;
      DOWN = BlockStateProperties.DOWN;
      PROPERTY_BY_DIRECTION = ImmutableMap.copyOf((Map)Util.make(Maps.newEnumMap(Direction.class), (var0) -> {
         var0.put(Direction.NORTH, NORTH);
         var0.put(Direction.EAST, EAST);
         var0.put(Direction.SOUTH, SOUTH);
         var0.put(Direction.WEST, WEST);
         var0.put(Direction.UP, UP);
         var0.put(Direction.DOWN, DOWN);
      }));
   }
}
