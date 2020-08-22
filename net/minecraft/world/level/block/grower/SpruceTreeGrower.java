package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.level.biome.BiomeDefaultFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;

public class SpruceTreeGrower extends AbstractMegaTreeGrower {
   @Nullable
   protected ConfiguredFeature getConfiguredFeature(Random var1) {
      return Feature.NORMAL_TREE.configured(BiomeDefaultFeatures.SPRUCE_TREE_CONFIG);
   }

   @Nullable
   protected ConfiguredFeature getConfiguredMegaFeature(Random var1) {
      return Feature.MEGA_SPRUCE_TREE.configured(var1.nextBoolean() ? BiomeDefaultFeatures.MEGA_SPRUCE_TREE_CONFIG : BiomeDefaultFeatures.MEGA_PINE_TREE_CONFIG);
   }
}
