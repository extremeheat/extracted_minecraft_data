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

   public boolean place(FeaturePlaceContext<DiskConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      ChunkGenerator var3 = var1.chunkGenerator();
      Random var4 = var1.random();
      DiskConfiguration var5 = (DiskConfiguration)var1.config();

      BlockPos var6;
      for(var6 = var1.origin(); var2.isEmptyBlock(var6) && var6.getY() > var2.getMinBuildHeight() + 2; var6 = var6.below()) {
      }

      return !var2.getBlockState(var6).is(Blocks.SNOW_BLOCK) ? false : super.place(new FeaturePlaceContext(var1.topFeature(), var2, var1.chunkGenerator(), var1.random(), var6, (DiskConfiguration)var1.config()));
   }
}
