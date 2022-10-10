package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class PlainsFlowersFeature extends AbstractFlowersFeature {
   public PlainsFlowersFeature() {
      super();
   }

   public IBlockState func_202355_a(Random var1, BlockPos var2) {
      double var3 = Biome.field_180281_af.func_151601_a((double)var2.func_177958_n() / 200.0D, (double)var2.func_177952_p() / 200.0D);
      int var5;
      if (var3 < -0.8D) {
         var5 = var1.nextInt(4);
         switch(var5) {
         case 0:
            return Blocks.field_196613_bi.func_176223_P();
         case 1:
            return Blocks.field_196612_bh.func_176223_P();
         case 2:
            return Blocks.field_196615_bk.func_176223_P();
         case 3:
         default:
            return Blocks.field_196614_bj.func_176223_P();
         }
      } else if (var1.nextInt(3) > 0) {
         var5 = var1.nextInt(3);
         if (var5 == 0) {
            return Blocks.field_196606_bd.func_176223_P();
         } else {
            return var5 == 1 ? Blocks.field_196610_bg.func_176223_P() : Blocks.field_196616_bl.func_176223_P();
         }
      } else {
         return Blocks.field_196605_bc.func_176223_P();
      }
   }
}
