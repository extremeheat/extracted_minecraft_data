package net.minecraft.world.gen.feature;

import java.util.function.Predicate;
import net.minecraft.block.state.IBlockState;

public class ReplaceBlockConfig implements IFeatureConfig {
   public final Predicate<IBlockState> field_202457_a;
   public final IBlockState field_202458_b;

   public ReplaceBlockConfig(Predicate<IBlockState> var1, IBlockState var2) {
      super();
      this.field_202457_a = var1;
      this.field_202458_b = var2;
   }
}
