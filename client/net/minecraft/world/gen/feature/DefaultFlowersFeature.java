package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class DefaultFlowersFeature extends AbstractFlowersFeature {
   public DefaultFlowersFeature() {
      super();
   }

   public IBlockState func_202355_a(Random var1, BlockPos var2) {
      return var1.nextFloat() > 0.6666667F ? Blocks.field_196605_bc.func_176223_P() : Blocks.field_196606_bd.func_176223_P();
   }
}
