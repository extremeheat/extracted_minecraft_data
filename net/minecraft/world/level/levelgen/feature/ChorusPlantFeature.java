package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class ChorusPlantFeature extends Feature {
   public ChorusPlantFeature(Function var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      if (var1.isEmptyBlock(var4.above()) && var1.getBlockState(var4).getBlock() == Blocks.END_STONE) {
         ChorusFlowerBlock.generatePlant(var1, var4.above(), var3, 8);
         return true;
      } else {
         return false;
      }
   }
}
