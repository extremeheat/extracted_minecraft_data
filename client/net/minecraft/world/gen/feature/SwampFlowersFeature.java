package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class SwampFlowersFeature extends AbstractFlowersFeature {
   public SwampFlowersFeature() {
      super();
   }

   public IBlockState func_202355_a(Random var1, BlockPos var2) {
      return Blocks.field_196607_be.func_176223_P();
   }
}
