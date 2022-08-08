package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class ChorusPlantFeature extends Feature<NoneFeatureConfiguration> {
   public ChorusPlantFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();
      RandomSource var4 = var1.random();
      if (var2.isEmptyBlock(var3) && var2.getBlockState(var3.below()).is(Blocks.END_STONE)) {
         ChorusFlowerBlock.generatePlant(var2, var3, var4, 8);
         return true;
      } else {
         return false;
      }
   }
}
