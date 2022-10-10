package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.CanopyTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class DarkOakTree extends AbstractBigTree {
   public DarkOakTree() {
      super();
   }

   @Nullable
   protected AbstractTreeFeature<NoFeatureConfig> func_196936_b(Random var1) {
      return null;
   }

   @Nullable
   protected AbstractTreeFeature<NoFeatureConfig> func_196938_a(Random var1) {
      return new CanopyTreeFeature(true);
   }
}
