package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;

public class SpringFeature extends Feature {
   public SpringFeature(Function var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator var2, Random var3, BlockPos var4, SpringConfiguration var5) {
      if (!var5.validBlocks.contains(var1.getBlockState(var4.above()).getBlock())) {
         return false;
      } else if (var5.requiresBlockBelow && !var5.validBlocks.contains(var1.getBlockState(var4.below()).getBlock())) {
         return false;
      } else {
         BlockState var6 = var1.getBlockState(var4);
         if (!var6.isAir() && !var5.validBlocks.contains(var6.getBlock())) {
            return false;
         } else {
            int var7 = 0;
            int var8 = 0;
            if (var5.validBlocks.contains(var1.getBlockState(var4.west()).getBlock())) {
               ++var8;
            }

            if (var5.validBlocks.contains(var1.getBlockState(var4.east()).getBlock())) {
               ++var8;
            }

            if (var5.validBlocks.contains(var1.getBlockState(var4.north()).getBlock())) {
               ++var8;
            }

            if (var5.validBlocks.contains(var1.getBlockState(var4.south()).getBlock())) {
               ++var8;
            }

            if (var5.validBlocks.contains(var1.getBlockState(var4.below()).getBlock())) {
               ++var8;
            }

            int var9 = 0;
            if (var1.isEmptyBlock(var4.west())) {
               ++var9;
            }

            if (var1.isEmptyBlock(var4.east())) {
               ++var9;
            }

            if (var1.isEmptyBlock(var4.north())) {
               ++var9;
            }

            if (var1.isEmptyBlock(var4.south())) {
               ++var9;
            }

            if (var1.isEmptyBlock(var4.below())) {
               ++var9;
            }

            if (var8 == var5.rockCount && var9 == var5.holeCount) {
               var1.setBlock(var4, var5.state.createLegacyBlock(), 2);
               var1.getLiquidTicks().scheduleTick(var4, var5.state.getType(), 0);
               ++var7;
            }

            return var7 > 0;
         }
      }
   }
}
