package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.BirchTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class BirchTree extends AbstractTree {
   public BirchTree() {
      super();
   }

   @Nullable
   protected AbstractTreeFeature<NoFeatureConfig> func_196936_b(Random var1) {
      return new BirchTreeFeature(true, false);
   }
}
