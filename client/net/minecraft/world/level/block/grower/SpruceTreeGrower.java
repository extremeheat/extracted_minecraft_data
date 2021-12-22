package net.minecraft.world.level.block.grower;

import java.util.Random;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class SpruceTreeGrower extends AbstractMegaTreeGrower {
   public SpruceTreeGrower() {
      super();
   }

   protected ConfiguredFeature<?, ?> getConfiguredFeature(Random var1, boolean var2) {
      return TreeFeatures.SPRUCE;
   }

   protected ConfiguredFeature<?, ?> getConfiguredMegaFeature(Random var1) {
      return var1.nextBoolean() ? TreeFeatures.MEGA_SPRUCE : TreeFeatures.MEGA_PINE;
   }
}
