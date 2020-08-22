package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.level.biome.BiomeDefaultFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;

public class OakTreeGrower extends AbstractTreeGrower {
   @Nullable
   protected ConfiguredFeature getConfiguredFeature(Random var1) {
      return var1.nextInt(10) == 0 ? Feature.FANCY_TREE.configured(BiomeDefaultFeatures.FANCY_TREE_CONFIG) : Feature.NORMAL_TREE.configured(BiomeDefaultFeatures.NORMAL_TREE_CONFIG);
   }
}
