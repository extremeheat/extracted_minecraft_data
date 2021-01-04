package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.feature.DarkOakFeature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public class DarkOakTreeGrower extends AbstractMegaTreeGrower {
   public DarkOakTreeGrower() {
      super();
   }

   @Nullable
   protected AbstractTreeFeature<NoneFeatureConfiguration> getFeature(Random var1) {
      return null;
   }

   @Nullable
   protected AbstractTreeFeature<NoneFeatureConfiguration> getMegaFeature(Random var1) {
      return new DarkOakFeature(NoneFeatureConfiguration::deserialize, true);
   }
}
