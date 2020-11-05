package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.data.worldgen.Features;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public class OakTreeGrower extends AbstractTreeGrower {
   public OakTreeGrower() {
      super();
   }

   @Nullable
   protected ConfiguredFeature<TreeConfiguration, ?> getConfiguredFeature(Random var1, boolean var2) {
      if (var1.nextInt(10) == 0) {
         return var2 ? Features.FANCY_OAK_BEES_005 : Features.FANCY_OAK;
      } else {
         return var2 ? Features.OAK_BEES_005 : Features.OAK;
      }
   }
}
