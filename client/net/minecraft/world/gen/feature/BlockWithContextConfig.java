package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.state.IBlockState;

public class BlockWithContextConfig implements IFeatureConfig {
   final IBlockState field_206924_a;
   final List<IBlockState> field_206925_b;
   final List<IBlockState> field_206926_c;
   final List<IBlockState> field_206927_d;

   public BlockWithContextConfig(IBlockState var1, IBlockState[] var2, IBlockState[] var3, IBlockState[] var4) {
      super();
      this.field_206924_a = var1;
      this.field_206925_b = Lists.newArrayList(var2);
      this.field_206926_c = Lists.newArrayList(var3);
      this.field_206927_d = Lists.newArrayList(var4);
   }
}
