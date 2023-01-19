package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class ChorusPlantBlock extends PipeBlock {
   protected ChorusPlantBlock(BlockBehaviour.Properties var1) {
      super(0.3125F, var1);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(NORTH, Boolean.valueOf(false))
            .setValue(EAST, Boolean.valueOf(false))
            .setValue(SOUTH, Boolean.valueOf(false))
            .setValue(WEST, Boolean.valueOf(false))
            .setValue(UP, Boolean.valueOf(false))
            .setValue(DOWN, Boolean.valueOf(false))
      );
   }

   @Override
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
      return this.defaultBlockState()
         .setValue(DOWN, Boolean.valueOf(var3.is(this) || var3.is(Blocks.CHORUS_FLOWER) || var3.is(Blocks.END_STONE)))
         .setValue(UP, Boolean.valueOf(var4.is(this) || var4.is(Blocks.CHORUS_FLOWER)))
         .setValue(NORTH, Boolean.valueOf(var5.is(this) || var5.is(Blocks.CHORUS_FLOWER)))
         .setValue(EAST, Boolean.valueOf(var6.is(this) || var6.is(Blocks.CHORUS_FLOWER)))
         .setValue(SOUTH, Boolean.valueOf(var7.is(this) || var7.is(Blocks.CHORUS_FLOWER)))
         .setValue(WEST, Boolean.valueOf(var8.is(this) || var8.is(Blocks.CHORUS_FLOWER)));
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (!var1.canSurvive(var4, var5)) {
         var4.scheduleTick(var5, this, 1);
         return super.updateShape(var1, var2, var3, var4, var5, var6);
      } else {
         boolean var7 = var3.is(this) || var3.is(Blocks.CHORUS_FLOWER) || var2 == Direction.DOWN && var3.is(Blocks.END_STONE);
         return var1.setValue(PROPERTY_BY_DIRECTION.get(var2), Boolean.valueOf(var7));
      }
   }

   @Override
   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!var1.canSurvive(var2, var3)) {
         var2.destroyBlock(var3, true);
      }
   }

   @Override
   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3.below());
      boolean var5 = !var2.getBlockState(var3.above()).isAir() && !var4.isAir();

      for(Direction var7 : Direction.Plane.HORIZONTAL) {
         BlockPos var8 = var3.relative(var7);
         BlockState var9 = var2.getBlockState(var8);
         if (var9.is(this)) {
            if (var5) {
               return false;
            }

            BlockState var10 = var2.getBlockState(var8.below());
            if (var10.is(this) || var10.is(Blocks.END_STONE)) {
               return true;
            }
         }
      }

      return var4.is(this) || var4.is(Blocks.END_STONE);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
   }

   @Override
   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }
}