package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenTaiga1 extends WorldGenAbstractTree {
   private static final IBlockState field_181636_a;
   private static final IBlockState field_181637_b;

   public WorldGenTaiga1() {
      super(false);
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      int var4 = var2.nextInt(5) + 7;
      int var5 = var4 - var2.nextInt(2) - 3;
      int var6 = var4 - var5;
      int var7 = 1 + var2.nextInt(var6 + 1);
      boolean var8 = true;
      if (var3.func_177956_o() >= 1 && var3.func_177956_o() + var4 + 1 <= 256) {
         int var12;
         int var13;
         int var18;
         for(int var9 = var3.func_177956_o(); var9 <= var3.func_177956_o() + 1 + var4 && var8; ++var9) {
            boolean var10 = true;
            if (var9 - var3.func_177956_o() < var5) {
               var18 = 0;
            } else {
               var18 = var7;
            }

            BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();

            for(var12 = var3.func_177958_n() - var18; var12 <= var3.func_177958_n() + var18 && var8; ++var12) {
               for(var13 = var3.func_177952_p() - var18; var13 <= var3.func_177952_p() + var18 && var8; ++var13) {
                  if (var9 >= 0 && var9 < 256) {
                     if (!this.func_150523_a(var1.func_180495_p(var11.func_181079_c(var12, var9, var13)).func_177230_c())) {
                        var8 = false;
                     }
                  } else {
                     var8 = false;
                  }
               }
            }
         }

         if (!var8) {
            return false;
         } else {
            Block var17 = var1.func_180495_p(var3.func_177977_b()).func_177230_c();
            if ((var17 == Blocks.field_150349_c || var17 == Blocks.field_150346_d) && var3.func_177956_o() < 256 - var4 - 1) {
               this.func_175921_a(var1, var3.func_177977_b());
               var18 = 0;

               int var19;
               for(var19 = var3.func_177956_o() + var4; var19 >= var3.func_177956_o() + var5; --var19) {
                  for(var12 = var3.func_177958_n() - var18; var12 <= var3.func_177958_n() + var18; ++var12) {
                     var13 = var12 - var3.func_177958_n();

                     for(int var14 = var3.func_177952_p() - var18; var14 <= var3.func_177952_p() + var18; ++var14) {
                        int var15 = var14 - var3.func_177952_p();
                        if (Math.abs(var13) != var18 || Math.abs(var15) != var18 || var18 <= 0) {
                           BlockPos var16 = new BlockPos(var12, var19, var14);
                           if (!var1.func_180495_p(var16).func_177230_c().func_149730_j()) {
                              this.func_175903_a(var1, var16, field_181637_b);
                           }
                        }
                     }
                  }

                  if (var18 >= 1 && var19 == var3.func_177956_o() + var5 + 1) {
                     --var18;
                  } else if (var18 < var7) {
                     ++var18;
                  }
               }

               for(var19 = 0; var19 < var4 - 1; ++var19) {
                  Block var20 = var1.func_180495_p(var3.func_177981_b(var19)).func_177230_c();
                  if (var20.func_149688_o() == Material.field_151579_a || var20.func_149688_o() == Material.field_151584_j) {
                     this.func_175903_a(var1, var3.func_177981_b(var19), field_181636_a);
                  }
               }

               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   static {
      field_181636_a = Blocks.field_150364_r.func_176223_P().func_177226_a(BlockOldLog.field_176301_b, BlockPlanks.EnumType.SPRUCE);
      field_181637_b = Blocks.field_150362_t.func_176223_P().func_177226_a(BlockOldLeaf.field_176239_P, BlockPlanks.EnumType.SPRUCE).func_177226_a(BlockLeaves.field_176236_b, false);
   }
}
