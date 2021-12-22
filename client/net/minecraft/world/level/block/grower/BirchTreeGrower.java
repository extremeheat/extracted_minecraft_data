package net.minecraft.world.level.block.grower;

import java.util.Random;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class BirchTreeGrower extends AbstractTreeGrower {
   public BirchTreeGrower() {
      super();
   }

   protected ConfiguredFeature<?, ?> getConfiguredFeature(Random var1, boolean var2) {
      return var2 ? TreeFeatures.BIRCH_BEES_005 : TreeFeatures.BIRCH;
   }
}
