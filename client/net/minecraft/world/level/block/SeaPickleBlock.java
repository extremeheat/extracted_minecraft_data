package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SeaPickleBlock extends BushBlock implements BonemealableBlock, SimpleWaterloggedBlock {
   public static final MapCodec<SeaPickleBlock> CODEC = simpleCodec(SeaPickleBlock::new);
   public static final int MAX_PICKLES = 4;
   public static final IntegerProperty PICKLES = BlockStateProperties.PICKLES;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape ONE_AABB = Block.box(6.0, 0.0, 6.0, 10.0, 6.0, 10.0);
   protected static final VoxelShape TWO_AABB = Block.box(3.0, 0.0, 3.0, 13.0, 6.0, 13.0);
   protected static final VoxelShape THREE_AABB = Block.box(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
   protected static final VoxelShape FOUR_AABB = Block.box(2.0, 0.0, 2.0, 14.0, 7.0, 14.0);

   @Override
   public MapCodec<SeaPickleBlock> codec() {
      return CODEC;
   }

   protected SeaPickleBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(PICKLES, Integer.valueOf(1)).setValue(WATERLOGGED, Boolean.valueOf(true)));
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = var1.getLevel().getBlockState(var1.getClickedPos());
      if (var2.is(this)) {
         return var2.setValue(PICKLES, Integer.valueOf(Math.min(4, var2.getValue(PICKLES) + 1)));
      } else {
         FluidState var3 = var1.getLevel().getFluidState(var1.getClickedPos());
         boolean var4 = var3.getType() == Fluids.WATER;
         return super.getStateForPlacement(var1).setValue(WATERLOGGED, Boolean.valueOf(var4));
      }
   }

   public static boolean isDead(BlockState var0) {
      return !var0.getValue(WATERLOGGED);
   }

   @Override
   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return !var1.getCollisionShape(var2, var3).getFaceShape(Direction.UP).isEmpty() || var1.isFaceSturdy(var2, var3, Direction.UP);
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockPos var4 = var3.below();
      return this.mayPlaceOn(var2.getBlockState(var4), var2, var4);
   }

   @Override
   protected BlockState updateShape(
      BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8
   ) {
      if (!var1.canSurvive(var2, var4)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if (var1.getValue(WATERLOGGED)) {
            var3.scheduleTick(var4, Fluids.WATER, Fluids.WATER.getTickDelay(var2));
         }

         return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }

   @Override
   protected boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      return !var2.isSecondaryUseActive() && var2.getItemInHand().is(this.asItem()) && var1.getValue(PICKLES) < 4 ? true : super.canBeReplaced(var1, var2);
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      switch (var1.getValue(PICKLES)) {
         case 1:
         default:
            return ONE_AABB;
         case 2:
            return TWO_AABB;
         case 3:
            return THREE_AABB;
         case 4:
            return FOUR_AABB;
      }
   }

   @Override
   protected FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(PICKLES, WATERLOGGED);
   }

   @Override
   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return !isDead(var3) && var1.getBlockState(var2.below()).is(BlockTags.CORAL_BLOCKS);
   }

   @Override
   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   @Override
   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      byte var5 = 5;
      byte var6 = 1;
      byte var7 = 2;
      int var8 = 0;
      int var9 = var3.getX() - 2;
      int var10 = 0;

      for (int var11 = 0; var11 < 5; var11++) {
         for (int var12 = 0; var12 < var6; var12++) {
            int var13 = 2 + var3.getY() - 1;

            for (int var14 = var13 - 2; var14 < var13; var14++) {
               BlockPos var15 = new BlockPos(var9 + var11, var14, var3.getZ() - var10 + var12);
               if (var15 != var3 && var2.nextInt(6) == 0 && var1.getBlockState(var15).is(Blocks.WATER)) {
                  BlockState var16 = var1.getBlockState(var15.below());
                  if (var16.is(BlockTags.CORAL_BLOCKS)) {
                     var1.setBlock(var15, Blocks.SEA_PICKLE.defaultBlockState().setValue(PICKLES, Integer.valueOf(var2.nextInt(4) + 1)), 3);
                  }
               }
            }
         }

         if (var8 < 2) {
            var6 += 2;
            var10++;
         } else {
            var6 -= 2;
            var10--;
         }

         var8++;
      }

      var1.setBlock(var3, var4.setValue(PICKLES, Integer.valueOf(4)), 2);
   }

   @Override
   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }
}
