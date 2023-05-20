package net.minecraft.world.level.block.grower;

import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class CherryTreeGrower extends AbstractTreeGrower {
   public CherryTreeGrower() {
      super();
   }

   @Override
   protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource var1, boolean var2) {
      return var2 ? TreeFeatures.CHERRY_BEES_005 : TreeFeatures.CHERRY;
   }
}
