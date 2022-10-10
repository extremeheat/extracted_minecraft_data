package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

public class ForestFlowersFeature extends AbstractFlowersFeature {
   private static final Block[] field_202356_a;

   public ForestFlowersFeature() {
      super();
   }

   public IBlockState func_202355_a(Random var1, BlockPos var2) {
      double var3 = MathHelper.func_151237_a((1.0D + Biome.field_180281_af.func_151601_a((double)var2.func_177958_n() / 48.0D, (double)var2.func_177952_p() / 48.0D)) / 2.0D, 0.0D, 0.9999D);
      Block var5 = field_202356_a[(int)(var3 * (double)field_202356_a.length)];
      return var5 == Blocks.field_196607_be ? Blocks.field_196606_bd.func_176223_P() : var5.func_176223_P();
   }

   static {
      field_202356_a = new Block[]{Blocks.field_196605_bc, Blocks.field_196606_bd, Blocks.field_196607_be, Blocks.field_196609_bf, Blocks.field_196610_bg, Blocks.field_196612_bh, Blocks.field_196613_bi, Blocks.field_196614_bj, Blocks.field_196615_bk, Blocks.field_196616_bl};
   }
}
