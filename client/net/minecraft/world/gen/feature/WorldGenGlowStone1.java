package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldGenGlowStone1 extends WorldGenerator {
   public WorldGenGlowStone1() {
      super();
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      if (!var1.func_175623_d(var3)) {
         return false;
      } else if (var1.func_180495_p(var3.func_177984_a()).func_177230_c() != Blocks.field_150424_aL) {
         return false;
      } else {
         var1.func_180501_a(var3, Blocks.field_150426_aN.func_176223_P(), 2);

         for(int var4 = 0; var4 < 1500; ++var4) {
            BlockPos var5 = var3.func_177982_a(var2.nextInt(8) - var2.nextInt(8), -var2.nextInt(12), var2.nextInt(8) - var2.nextInt(8));
            if (var1.func_180495_p(var5).func_177230_c().func_149688_o() == Material.field_151579_a) {
               int var6 = 0;
               EnumFacing[] var7 = EnumFacing.values();
               int var8 = var7.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  EnumFacing var10 = var7[var9];
                  if (var1.func_180495_p(var5.func_177972_a(var10)).func_177230_c() == Blocks.field_150426_aN) {
                     ++var6;
                  }

                  if (var6 > 1) {
                     break;
                  }
               }

               if (var6 == 1) {
                  var1.func_180501_a(var5, Blocks.field_150426_aN.func_176223_P(), 2);
               }
            }
         }

         return true;
      }
   }
}
