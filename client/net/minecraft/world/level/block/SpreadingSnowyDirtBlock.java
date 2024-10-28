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
   protected SpreadingSnowyDirtBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   private static boolean canBeGrass(BlockState var0, LevelReader var1, BlockPos var2) {
      BlockPos var3 = var2.above();
      BlockState var4 = var1.getBlockState(var3);
      if (var4.is(Blocks.SNOW) && (Integer)var4.getValue(SnowLayerBlock.LAYERS) == 1) {
         return true;
      } else if (var4.getFluidState().getAmount() == 8) {
         return false;
      } else {
         int var5 = LightEngine.getLightBlockInto(var1, var0, var2, var4, var3, Direction.UP, var4.getLightBlock(var1, var3));
         return var5 < var1.getMaxLightLevel();
      }
   }

   protected abstract MapCodec<? extends SpreadingSnowyDirtBlock> codec();

   private static boolean canPropagate(BlockState var0, LevelReader var1, BlockPos var2) {
      BlockPos var3 = var2.above();
      return canBeGrass(var0, var1, var2) && !var1.getFluidState(var3).is(FluidTags.WATER);
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!canBeGrass(var1, var2, var3)) {
         var2.setBlockAndUpdate(var3, Blocks.DIRT.defaultBlockState());
      } else {
         if (var2.getMaxLocalRawBrightness(var3.above()) >= 9) {
            BlockState var5 = this.defaultBlockState();

            for(int var6 = 0; var6 < 4; ++var6) {
               BlockPos var7 = var3.offset(var4.nextInt(3) - 1, var4.nextInt(5) - 3, var4.nextInt(3) - 1);
               if (var2.getBlockState(var7).is(Blocks.DIRT) && canPropagate(var5, var2, var7)) {
                  var2.setBlockAndUpdate(var7, (BlockState)var5.setValue(SNOWY, var2.getBlockState(var7.above()).is(Blocks.SNOW)));
               }
            }
         }

      }
   }
}
