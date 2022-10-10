package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.MegaJungleFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;

public class JungleTree extends AbstractBigTree {
   public JungleTree() {
      super();
   }

   @Nullable
   protected AbstractTreeFeature<NoFeatureConfig> func_196936_b(Random var1) {
      return new TreeFeature(true, 4 + var1.nextInt(7), Blocks.field_196620_N.func_176223_P(), Blocks.field_196648_Z.func_176223_P(), false);
   }

   @Nullable
   protected AbstractTreeFeature<NoFeatureConfig> func_196938_a(Random var1) {
      return new MegaJungleFeature(true, 10, 20, Blocks.field_196620_N.func_176223_P(), Blocks.field_196648_Z.func_176223_P());
   }
}
