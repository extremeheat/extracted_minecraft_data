package net.minecraft.world.level.block.grower;

import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class DarkOakTreeGrower extends AbstractMegaTreeGrower {
   public DarkOakTreeGrower() {
      super();
   }

   @Nullable
   protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource var1, boolean var2) {
      return null;
   }

   @Nullable
   protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredMegaFeature(RandomSource var1) {
      return TreeFeatures.DARK_OAK;
   }
}
