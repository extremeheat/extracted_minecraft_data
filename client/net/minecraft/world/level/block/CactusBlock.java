package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CactusBlock extends Block {
   public static final MapCodec<CactusBlock> CODEC = simpleCodec(CactusBlock::new);
   public static final IntegerProperty AGE = BlockStateProperties.AGE_15;
   public static final int MAX_AGE = 15;
   protected static final int AABB_OFFSET = 1;
   protected static final VoxelShape COLLISION_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
   protected static final VoxelShape OUTLINE_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

   @Override
   public MapCodec<CactusBlock> codec() {
      return CODEC;
   }

   protected CactusBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
   }

   @Override
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!var1.canSurvive(var2, var3)) {
         var2.destroyBlock(var3, true);
      }
   }

   @Override
   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      BlockPos var5 = var3.above();
      if (var2.isEmptyBlock(var5)) {
         int var6 = 1;

         while(var2.getBlockState(var3.below(var6)).is(this)) {
            ++var6;
         }

         if (var6 < 3) {
            int var7 = var1.getValue(AGE);
            if (var7 == 15) {
               var2.setBlockAndUpdate(var5, this.defaultBlockState());
               BlockState var8 = var1.setValue(AGE, Integer.valueOf(0));
               var2.setBlock(var3, var8, 4);
               var2.neighborChanged(var8, var5, this, var3, false);
            } else {
               var2.setBlock(var3, var1.setValue(AGE, Integer.valueOf(var7 + 1)), 4);
            }
         }
      }
   }

   @Override
   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return COLLISION_SHAPE;
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return OUTLINE_SHAPE;
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (!var1.canSurvive(var4, var5)) {
         var4.scheduleTick(var5, this, 1);
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      for(Direction var5 : Direction.Plane.HORIZONTAL) {
         BlockState var6 = var2.getBlockState(var3.relative(var5));
         if (var6.isSolid() || var2.getFluidState(var3.relative(var5)).is(FluidTags.LAVA)) {
            return false;
         }
      }

      BlockState var7 = var2.getBlockState(var3.below());
      return (var7.is(Blocks.CACTUS) || var7.is(BlockTags.SAND)) && !var2.getBlockState(var3.above()).liquid();
   }

   @Override
   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      var4.hurt(var2.damageSources().cactus(), 1.0F);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AGE);
   }

   @Override
   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }
}