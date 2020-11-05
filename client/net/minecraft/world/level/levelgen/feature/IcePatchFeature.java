package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;

public class IcePatchFeature extends BaseDiskFeature {
   public IcePatchFeature(Codec<DiskConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, DiskConfiguration var5) {
      while(var1.isEmptyBlock(var4) && var4.getY() > 2) {
         var4 = var4.below();
      }

      if (!var1.getBlockState(var4).is(Blocks.SNOW_BLOCK)) {
         return false;
      } else {
         return super.place(var1, var2, var3, var4, var5);
      }
   }
}
