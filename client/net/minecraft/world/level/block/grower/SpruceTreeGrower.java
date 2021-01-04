package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.feature.MegaPineTreeFeature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.SpruceFeature;

public class SpruceTreeGrower extends AbstractMegaTreeGrower {
   public SpruceTreeGrower() {
      super();
   }

   @Nullable
   protected AbstractTreeFeature<NoneFeatureConfiguration> getFeature(Random var1) {
      return new SpruceFeature(NoneFeatureConfiguration::deserialize, true);
   }

   @Nullable
   protected AbstractTreeFeature<NoneFeatureConfiguration> getMegaFeature(Random var1) {
      return new MegaPineTreeFeature(NoneFeatureConfiguration::deserialize, false, var1.nextBoolean());
   }
}
