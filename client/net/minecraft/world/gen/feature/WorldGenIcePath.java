package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenIcePath extends WorldGenerator {
   private Block field_150555_a;
   private int field_150554_b;

   public WorldGenIcePath(int var1) {
      super();
      this.field_150555_a = Blocks.field_150403_cj;
      this.field_150554_b = var1;
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      while(var1.func_175623_d(var3) && var3.func_177956_o() > 2) {
         var3 = var3.func_177977_b();
      }

      if (var1.func_180495_p(var3).func_177230_c() != Blocks.field_150433_aE) {
         return false;
      } else {
         int var4 = var2.nextInt(this.field_150554_b - 2) + 2;
         byte var5 = 1;

         for(int var6 = var3.func_177958_n() - var4; var6 <= var3.func_177958_n() + var4; ++var6) {
            for(int var7 = var3.func_177952_p() - var4; var7 <= var3.func_177952_p() + var4; ++var7) {
               int var8 = var6 - var3.func_177958_n();
               int var9 = var7 - var3.func_177952_p();
               if (var8 * var8 + var9 * var9 <= var4 * var4) {
                  for(int var10 = var3.func_177956_o() - var5; var10 <= var3.func_177956_o() + var5; ++var10) {
                     BlockPos var11 = new BlockPos(var6, var10, var7);
                     Block var12 = var1.func_180495_p(var11).func_177230_c();
                     if (var12 == Blocks.field_150346_d || var12 == Blocks.field_150433_aE || var12 == Blocks.field_150432_aD) {
                        var1.func_180501_a(var11, this.field_150555_a.func_176223_P(), 2);
                     }
                  }
               }
            }
         }

         return true;
      }
   }
}
