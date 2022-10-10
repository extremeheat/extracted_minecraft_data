package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.placement.BasePlacement;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class CompositeFlowerFeature<D extends IPlacementConfig> extends CompositeFeature<NoFeatureConfig, D> {
   public CompositeFlowerFeature(AbstractFlowersFeature var1, BasePlacement<D> var2, D var3) {
      super(var1, IFeatureConfig.field_202429_e, var2, var3);
   }

   public IBlockState func_202354_a(Random var1, BlockPos var2) {
      return ((AbstractFlowersFeature)this.field_202350_a).func_202355_a(var1, var2);
   }
}
