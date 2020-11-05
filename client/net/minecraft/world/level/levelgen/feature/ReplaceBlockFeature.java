package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

public class ReplaceBlockFeature extends Feature<ReplaceBlockConfiguration> {
   public ReplaceBlockFeature(Codec<ReplaceBlockConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, ReplaceBlockConfiguration var5) {
      if (var1.getBlockState(var4).is(var5.target.getBlock())) {
         var1.setBlock(var4, var5.state, 2);
      }

      return true;
   }
}
