package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenDeadBush extends WorldGenerator {
   public WorldGenDeadBush() {
      super();
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      Block var4;
      while(((var4 = var1.func_180495_p(var3).func_177230_c()).func_149688_o() == Material.field_151579_a || var4.func_149688_o() == Material.field_151584_j) && var3.func_177956_o() > 0) {
         var3 = var3.func_177977_b();
      }

      for(int var5 = 0; var5 < 4; ++var5) {
         BlockPos var6 = var3.func_177982_a(var2.nextInt(8) - var2.nextInt(8), var2.nextInt(4) - var2.nextInt(4), var2.nextInt(8) - var2.nextInt(8));
         if (var1.func_175623_d(var6) && Blocks.field_150330_I.func_180671_f(var1, var6, Blocks.field_150330_I.func_176223_P())) {
            var1.func_180501_a(var6, Blocks.field_150330_I.func_176223_P(), 2);
         }
      }

      return true;
   }
}
