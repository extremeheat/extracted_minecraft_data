package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;

public class SimpleBlockFeature extends Feature<SimpleBlockConfiguration> {
   public SimpleBlockFeature(Codec<SimpleBlockConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, SimpleBlockConfiguration var5) {
      if (var5.placeOn.contains(var1.getBlockState(var4.below())) && var5.placeIn.contains(var1.getBlockState(var4)) && var5.placeUnder.contains(var1.getBlockState(var4.above()))) {
         var1.setBlock(var4, var5.toPlace, 2);
         return true;
      } else {
         return false;
      }
   }
}
