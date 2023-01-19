package net.minecraft.world.level.block.grower;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class BirchTreeGrower extends AbstractTreeGrower {
   public BirchTreeGrower() {
      super();
   }

   @Override
   protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource var1, boolean var2) {
      return var2 ? TreeFeatures.BIRCH_BEES_005 : TreeFeatures.BIRCH;
   }
}
