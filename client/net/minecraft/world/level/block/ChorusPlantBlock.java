package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class ChorusPlantBlock extends PipeBlock {
   public static final MapCodec<ChorusPlantBlock> CODEC = simpleCodec(ChorusPlantBlock::new);

   @Override
   public MapCodec<ChorusPlantBlock> codec() {
      return CODEC;
   }

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
      return getStateWithConnections(var1.getLevel(), var1.getClickedPos(), this.defaultBlockState());
   }

   public static BlockState getStateWithConnections(BlockGetter var0, BlockPos var1, BlockState var2) {
      BlockState var3 = var0.getBlockState(var1.below());
      BlockState var4 = var0.getBlockState(var1.above());
      BlockState var5 = var0.getBlockState(var1.north());
      BlockState var6 = var0.getBlockState(var1.east());
      BlockState var7 = var0.getBlockState(var1.south());
      BlockState var8 = var0.getBlockState(var1.west());
      Block var9 = var2.getBlock();
      return var2.trySetValue(DOWN, Boolean.valueOf(var3.is(var9) || var3.is(Blocks.CHORUS_FLOWER) || var3.is(Blocks.END_STONE)))
         .trySetValue(UP, Boolean.valueOf(var4.is(var9) || var4.is(Blocks.CHORUS_FLOWER)))
         .trySetValue(NORTH, Boolean.valueOf(var5.is(var9) || var5.is(Blocks.CHORUS_FLOWER)))
         .trySetValue(EAST, Boolean.valueOf(var6.is(var9) || var6.is(Blocks.CHORUS_FLOWER)))
         .trySetValue(SOUTH, Boolean.valueOf(var7.is(var9) || var7.is(Blocks.CHORUS_FLOWER)))
         .trySetValue(WEST, Boolean.valueOf(var8.is(var9) || var8.is(Blocks.CHORUS_FLOWER)));
   }

   @Override
   protected BlockState updateShape(
      BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8
   ) {
      if (!var1.canSurvive(var2, var4)) {
         var3.scheduleTick(var4, this, 1);
         return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
      } else {
         boolean var9 = var7.is(this) || var7.is(Blocks.CHORUS_FLOWER) || var5 == Direction.DOWN && var7.is(Blocks.END_STONE);
         return var1.setValue(PROPERTY_BY_DIRECTION.get(var5), Boolean.valueOf(var9));
      }
   }

   @Override
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!var1.canSurvive(var2, var3)) {
         var2.destroyBlock(var3, true);
      }
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3.below());
      boolean var5 = !var2.getBlockState(var3.above()).isAir() && !var4.isAir();

      for (Direction var7 : Direction.Plane.HORIZONTAL) {
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
   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }
}
