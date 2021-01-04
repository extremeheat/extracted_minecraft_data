package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.feature.BigTreeFeature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.TreeFeature;

public class OakTreeGrower extends AbstractTreeGrower {
   public OakTreeGrower() {
      super();
   }

   @Nullable
   protected AbstractTreeFeature<NoneFeatureConfiguration> getFeature(Random var1) {
      return (AbstractTreeFeature)(var1.nextInt(10) == 0 ? new BigTreeFeature(NoneFeatureConfiguration::deserialize, true) : new TreeFeature(NoneFeatureConfiguration::deserialize, true));
   }
}
