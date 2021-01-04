package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.SavannaTreeFeature;

public class AcaciaTreeGrower extends AbstractTreeGrower {
   public AcaciaTreeGrower() {
      super();
   }

   @Nullable
   protected AbstractTreeFeature<NoneFeatureConfiguration> getFeature(Random var1) {
      return new SavannaTreeFeature(NoneFeatureConfiguration::deserialize, true);
   }
}
