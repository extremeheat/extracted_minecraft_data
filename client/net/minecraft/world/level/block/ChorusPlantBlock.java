package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class ChorusPlantBlock extends PipeBlock {
   protected ChorusPlantBlock(BlockBehaviour.Properties var1) {
      super(0.3125F, var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false)).setValue(UP, false)).setValue(DOWN, false));
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.getStateForPlacement(var1.getLevel(), var1.getClickedPos());
   }

   public BlockState getStateForPlacement(BlockGetter var1, BlockPos var2) {
      BlockState var3 = var1.getBlockState(var2.below());
      BlockState var4 = var1.getBlockState(var2.above());
      BlockState var5 = var1.getBlockState(var2.north());
      BlockState var6 = var1.getBlockState(var2.east());
      BlockState var7 = var1.getBlockState(var2.south());
      BlockState var8 = var1.getBlockState(var2.west());
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(DOWN, var3.is(this) || var3.is(Blocks.CHORUS_FLOWER) || var3.is(Blocks.END_STONE))).setValue(UP, var4.is(this) || var4.is(Blocks.CHORUS_FLOWER))).setValue(NORTH, var5.is(this) || var5.is(Blocks.CHORUS_FLOWER))).setValue(EAST, var6.is(this) || var6.is(Blocks.CHORUS_FLOWER))).setValue(SOUTH, var7.is(this) || var7.is(Blocks.CHORUS_FLOWER))).setValue(WEST, var8.is(this) || var8.is(Blocks.CHORUS_FLOWER));
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (!var1.canSurvive(var4, var5)) {
         var4.getBlockTicks().scheduleTick(var5, this, 1);
         return super.updateShape(var1, var2, var3, var4, var5, var6);
      } else {
         boolean var7 = var3.is(this) || var3.is(Blocks.CHORUS_FLOWER) || var2 == Direction.DOWN && var3.is(Blocks.END_STONE);
         return (BlockState)var1.setValue((Property)PROPERTY_BY_DIRECTION.get(var2), var7);
      }
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      if (!var1.canSurvive(var2, var3)) {
         var2.destroyBlock(var3, true);
      }

   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3.below());
      boolean var5 = !var2.getBlockState(var3.above()).isAir() && !var4.isAir();
      Iterator var6 = Direction.Plane.HORIZONTAL.iterator();

      BlockState var10;
      do {
         BlockPos var8;
         BlockState var9;
         do {
            if (!var6.hasNext()) {
               return var4.is(this) || var4.is(Blocks.END_STONE);
            }

            Direction var7 = (Direction)var6.next();
            var8 = var3.relative(var7);
            var9 = var2.getBlockState(var8);
         } while(!var9.is(this));

         if (var5) {
            return false;
         }

         var10 = var2.getBlockState(var8.below());
      } while(!var10.is(this) && !var10.is(Blocks.END_STONE));

      return true;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }
}
