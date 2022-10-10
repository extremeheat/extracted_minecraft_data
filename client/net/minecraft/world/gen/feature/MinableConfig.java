package net.minecraft.world.gen.feature;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class MinableConfig implements IFeatureConfig {
   public static final Predicate<IBlockState> field_202441_a = (var0) -> {
      if (var0 == null) {
         return false;
      } else {
         Block var1 = var0.func_177230_c();
         return var1 == Blocks.field_150348_b || var1 == Blocks.field_196650_c || var1 == Blocks.field_196654_e || var1 == Blocks.field_196656_g;
      }
   };
   public final Predicate<IBlockState> field_202442_b;
   public final int field_202443_c;
   public final IBlockState field_202444_d;

   public MinableConfig(Predicate<IBlockState> var1, IBlockState var2, int var3) {
      super();
      this.field_202443_c = var3;
      this.field_202444_d = var2;
      this.field_202442_b = var1;
   }
}
