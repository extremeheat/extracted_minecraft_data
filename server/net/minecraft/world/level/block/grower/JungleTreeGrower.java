package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.data.worldgen.Features;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class JungleTreeGrower extends AbstractMegaTreeGrower {
   public JungleTreeGrower() {
      super();
   }

   @Nullable
   protected ConfiguredFeature<TreeConfiguration, ?> getConfiguredFeature(Random var1, boolean var2) {
      return Features.JUNGLE_TREE_NO_VINE;
   }

   @Nullable
   protected ConfiguredFeature<TreeConfiguration, ?> getConfiguredMegaFeature(Random var1) {
      return Features.MEGA_JUNGLE_TREE;
   }
}
