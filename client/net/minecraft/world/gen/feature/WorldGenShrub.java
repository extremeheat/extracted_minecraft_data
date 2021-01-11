package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenShrub extends WorldGenTrees {
   private final IBlockState field_150528_a;
   private final IBlockState field_150527_b;

   public WorldGenShrub(IBlockState var1, IBlockState var2) {
      super(false);
      this.field_150527_b = var1;
      this.field_150528_a = var2;
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      Block var4;
      while(((var4 = var1.func_180495_p(var3).func_177230_c()).func_149688_o() == Material.field_151579_a || var4.func_149688_o() == Material.field_151584_j) && var3.func_177956_o() > 0) {
         var3 = var3.func_177977_b();
      }

      Block var5 = var1.func_180495_p(var3).func_177230_c();
      if (var5 == Blocks.field_150346_d || var5 == Blocks.field_150349_c) {
         var3 = var3.func_177984_a();
         this.func_175903_a(var1, var3, this.field_150527_b);

         for(int var6 = var3.func_177956_o(); var6 <= var3.func_177956_o() + 2; ++var6) {
            int var7 = var6 - var3.func_177956_o();
            int var8 = 2 - var7;

            for(int var9 = var3.func_177958_n() - var8; var9 <= var3.func_177958_n() + var8; ++var9) {
               int var10 = var9 - var3.func_177958_n();

               for(int var11 = var3.func_177952_p() - var8; var11 <= var3.func_177952_p() + var8; ++var11) {
                  int var12 = var11 - var3.func_177952_p();
                  if (Math.abs(var10) != var8 || Math.abs(var12) != var8 || var2.nextInt(2) != 0) {
                     BlockPos var13 = new BlockPos(var9, var6, var11);
                     if (!var1.func_180495_p(var13).func_177230_c().func_149730_j()) {
                        this.func_175903_a(var1, var13, this.field_150528_a);
                     }
                  }
               }
            }
         }
      }

      return true;
   }
}
