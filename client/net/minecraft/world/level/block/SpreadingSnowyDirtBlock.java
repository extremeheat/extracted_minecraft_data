package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;

public abstract class SpreadingSnowyDirtBlock extends SnowyDirtBlock {
   protected SpreadingSnowyDirtBlock(final BlockBehaviour.Properties properties) {
      super(properties);
   }

   private static boolean canBeGrass(final BlockState state, final LevelReader level, final BlockPos pos) {
      BlockPos above = pos.above();
      BlockState aboveState = level.getBlockState(above);
      if (aboveState.is(Blocks.SNOW) && (Integer)aboveState.getValue(SnowLayerBlock.LAYERS) == 1) {
         return true;
      } else if (aboveState.getFluidState().isFull()) {
         return false;
      } else {
         int lightBlockInto = LightEngine.getLightBlockInto(state, aboveState, Direction.UP, aboveState.getLightBlock());
         return lightBlockInto < 15;
      }
   }

   protected abstract MapCodec<? extends SpreadingSnowyDirtBlock> codec();

   private static boolean canPropagate(final BlockState state, final LevelReader level, final BlockPos pos) {
      BlockPos above = pos.above();
      return canBeGrass(state, level, pos) && !level.getFluidState(above).is(FluidTags.WATER);
   }

   protected void randomTick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (!canBeGrass(state, level, pos)) {
         level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
      } else {
         if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
            BlockState defaultBlockState = this.defaultBlockState();

            for(int i = 0; i < 4; ++i) {
               BlockPos testPos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
               if (level.getBlockState(testPos).is(Blocks.DIRT) && canPropagate(defaultBlockState, level, testPos)) {
                  level.setBlockAndUpdate(testPos, (BlockState)defaultBlockState.setValue(SNOWY, isSnowySetting(level.getBlockState(testPos.above()))));
               }
            }
         }

      }
   }
}
