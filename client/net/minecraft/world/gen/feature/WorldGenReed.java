package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenReed extends WorldGenerator {
   public WorldGenReed() {
      super();
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      for(int var4 = 0; var4 < 20; ++var4) {
         BlockPos var5 = var3.func_177982_a(var2.nextInt(4) - var2.nextInt(4), 0, var2.nextInt(4) - var2.nextInt(4));
         if (var1.func_175623_d(var5)) {
            BlockPos var6 = var5.func_177977_b();
            if (var1.func_180495_p(var6.func_177976_e()).func_177230_c().func_149688_o() == Material.field_151586_h || var1.func_180495_p(var6.func_177974_f()).func_177230_c().func_149688_o() == Material.field_151586_h || var1.func_180495_p(var6.func_177978_c()).func_177230_c().func_149688_o() == Material.field_151586_h || var1.func_180495_p(var6.func_177968_d()).func_177230_c().func_149688_o() == Material.field_151586_h) {
               int var7 = 2 + var2.nextInt(var2.nextInt(3) + 1);

               for(int var8 = 0; var8 < var7; ++var8) {
                  if (Blocks.field_150436_aH.func_176354_d(var1, var5)) {
                     var1.func_180501_a(var5.func_177981_b(var8), Blocks.field_150436_aH.func_176223_P(), 2);
                  }
               }
            }
         }
      }

      return true;
   }
}
