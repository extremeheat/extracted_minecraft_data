package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class WorldGenAbstractTree extends WorldGenerator {
   public WorldGenAbstractTree(boolean var1) {
      super(var1);
   }

   protected boolean func_150523_a(Block var1) {
      Material var2 = var1.func_149688_o();
      return var2 == Material.field_151579_a || var2 == Material.field_151584_j || var1 == Blocks.field_150349_c || var1 == Blocks.field_150346_d || var1 == Blocks.field_150364_r || var1 == Blocks.field_150363_s || var1 == Blocks.field_150345_g || var1 == Blocks.field_150395_bd;
   }

   public void func_180711_a(World var1, Random var2, BlockPos var3) {
   }

   protected void func_175921_a(World var1, BlockPos var2) {
      if (var1.func_180495_p(var2).func_177230_c() != Blocks.field_150346_d) {
         this.func_175903_a(var1, var2, Blocks.field_150346_d.func_176223_P());
      }

   }
}
