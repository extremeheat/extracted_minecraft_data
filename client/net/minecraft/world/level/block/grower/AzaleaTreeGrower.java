package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class AzaleaTreeGrower extends AbstractTreeGrower {
   public AzaleaTreeGrower() {
      super();
   }

   @Nullable
   protected ConfiguredFeature<?, ?> getConfiguredFeature(Random var1, boolean var2) {
      return TreeFeatures.AZALEA_TREE;
   }
}
