package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.BigTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;

public class OakTree extends AbstractTree {
   public OakTree() {
      super();
   }

   @Nullable
   protected AbstractTreeFeature<NoFeatureConfig> func_196936_b(Random var1) {
      return (AbstractTreeFeature)(var1.nextInt(10) == 0 ? new BigTreeFeature(true) : new TreeFeature(true));
   }
}
