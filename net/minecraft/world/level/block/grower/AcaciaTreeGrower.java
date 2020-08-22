package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.level.biome.BiomeDefaultFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;

public class AcaciaTreeGrower extends AbstractTreeGrower {
   @Nullable
   protected ConfiguredFeature getConfiguredFeature(Random var1) {
      return Feature.ACACIA_TREE.configured(BiomeDefaultFeatures.ACACIA_TREE_CONFIG);
   }
}
