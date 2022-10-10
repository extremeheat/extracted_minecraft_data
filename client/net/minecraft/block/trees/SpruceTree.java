package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.MegaPineTree;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.TallTaigaTreeFeature;

public class SpruceTree extends AbstractBigTree {
   public SpruceTree() {
      super();
   }

   @Nullable
   protected AbstractTreeFeature<NoFeatureConfig> func_196936_b(Random var1) {
      return new TallTaigaTreeFeature(true);
   }

   @Nullable
   protected AbstractTreeFeature<NoFeatureConfig> func_196938_a(Random var1) {
      return new MegaPineTree(false, var1.nextBoolean());
   }
}
