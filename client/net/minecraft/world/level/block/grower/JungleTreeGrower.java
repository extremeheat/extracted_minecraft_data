package net.minecraft.world.level.block.grower;

import java.util.Random;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class JungleTreeGrower extends AbstractMegaTreeGrower {
   public JungleTreeGrower() {
      super();
   }

   protected ConfiguredFeature<?, ?> getConfiguredFeature(Random var1, boolean var2) {
      return TreeFeatures.JUNGLE_TREE_NO_VINE;
   }

   protected ConfiguredFeature<?, ?> getConfiguredMegaFeature(Random var1) {
      return TreeFeatures.MEGA_JUNGLE_TREE;
   }
}
