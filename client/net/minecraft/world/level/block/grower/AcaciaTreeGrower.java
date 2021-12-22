package net.minecraft.world.level.block.grower;

import java.util.Random;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class AcaciaTreeGrower extends AbstractTreeGrower {
   public AcaciaTreeGrower() {
      super();
   }

   protected ConfiguredFeature<?, ?> getConfiguredFeature(Random var1, boolean var2) {
      return TreeFeatures.ACACIA;
   }
}
