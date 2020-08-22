package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.level.biome.BiomeDefaultFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SmallTreeConfiguration;

public class JungleTreeGrower extends AbstractMegaTreeGrower {
   @Nullable
   protected ConfiguredFeature getConfiguredFeature(Random var1) {
      return (new TreeFeature(SmallTreeConfiguration::deserialize)).configured(BiomeDefaultFeatures.JUNGLE_TREE_NOVINE_CONFIG);
   }

   @Nullable
   protected ConfiguredFeature getConfiguredMegaFeature(Random var1) {
      return Feature.MEGA_JUNGLE_TREE.configured(BiomeDefaultFeatures.MEGA_JUNGLE_TREE_CONFIG);
   }
}
