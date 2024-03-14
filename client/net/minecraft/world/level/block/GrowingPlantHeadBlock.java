package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class GrowingPlantHeadBlock extends GrowingPlantBlock implements BonemealableBlock {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_25;
   public static final int MAX_AGE = 25;
   private final double growPerTickProbability;

   protected GrowingPlantHeadBlock(BlockBehaviour.Properties var1, Direction var2, VoxelShape var3, boolean var4, double var5) {
      super(var1, var2, var3, var4);
      this.growPerTickProbability = var5;
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
   }

   @Override
   protected abstract MapCodec<? extends GrowingPlantHeadBlock> codec();

   @Override
   public BlockState getStateForPlacement(LevelAccessor var1) {
      return this.defaultBlockState().setValue(AGE, Integer.valueOf(var1.getRandom().nextInt(25)));
   }

   @Override
   protected boolean isRandomlyTicking(BlockState var1) {
      return var1.getValue(AGE) < 25;
   }

   @Override
   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (var1.getValue(AGE) < 25 && var4.nextDouble() < this.growPerTickProbability) {
         BlockPos var5 = var3.relative(this.growthDirection);
         if (this.canGrowInto(var2.getBlockState(var5))) {
            var2.setBlockAndUpdate(var5, this.getGrowIntoState(var1, var2.random));
         }
      }
   }

   protected BlockState getGrowIntoState(BlockState var1, RandomSource var2) {
      return var1.cycle(AGE);
   }

   public BlockState getMaxAgeState(BlockState var1) {
      return var1.setValue(AGE, Integer.valueOf(25));
   }

   public boolean isMaxAge(BlockState var1) {
      return var1.getValue(AGE) == 25;
   }

   protected BlockState updateBodyAfterConvertedFromHead(BlockState var1, BlockState var2) {
      return var2;
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var2 == this.growthDirection.getOpposite() && !var1.canSurvive(var4, var5)) {
         var4.scheduleTick(var5, this, 1);
      }

      if (var2 != this.growthDirection || !var3.is(this) && !var3.is(this.getBodyBlock())) {
         if (this.scheduleFluidTicks) {
            var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
         }

         return super.updateShape(var1, var2, var3, var4, var5, var6);
      } else {
         return this.updateBodyAfterConvertedFromHead(var1, this.getBodyBlock().defaultBlockState());
      }
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AGE);
   }

   @Override
   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return this.canGrowInto(var1.getBlockState(var2.relative(this.growthDirection)));
   }

   @Override
   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   @Override
   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      BlockPos var5 = var3.relative(this.growthDirection);
      int var6 = Math.min(var4.getValue(AGE) + 1, 25);
      int var7 = this.getBlocksToGrowWhenBonemealed(var2);

      for(int var8 = 0; var8 < var7 && this.canGrowInto(var1.getBlockState(var5)); ++var8) {
         var1.setBlockAndUpdate(var5, var4.setValue(AGE, Integer.valueOf(var6)));
         var5 = var5.relative(this.growthDirection);
         var6 = Math.min(var6 + 1, 25);
      }
   }

   protected abstract int getBlocksToGrowWhenBonemealed(RandomSource var1);

   protected abstract boolean canGrowInto(BlockState var1);

   @Override
   protected GrowingPlantHeadBlock getHeadBlock() {
      return this;
   }
}
