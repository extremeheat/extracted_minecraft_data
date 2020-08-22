package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;

public class SimpleBlockFeature extends Feature {
   public SimpleBlockFeature(Function var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator var2, Random var3, BlockPos var4, SimpleBlockConfiguration var5) {
      if (var5.placeOn.contains(var1.getBlockState(var4.below())) && var5.placeIn.contains(var1.getBlockState(var4)) && var5.placeUnder.contains(var1.getBlockState(var4.above()))) {
         var1.setBlock(var4, var5.toPlace, 2);
         return true;
      } else {
         return false;
      }
   }
}
