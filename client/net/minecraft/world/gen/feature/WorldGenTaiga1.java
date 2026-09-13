package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenTaiga1 extends WorldGenAbstractTree {
   public WorldGenTaiga1() {
      super(false);
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      int var6 = var2.nextInt(5) + 7;
      int var7 = var6 - var2.nextInt(2) - 3;
      int var8 = var6 - var7;
      int var9 = 1 + var2.nextInt(var8 + 1);
      boolean var10 = true;
      if (var4 >= 1 && var4 + var6 + 1 <= 256) {
         for(int var11 = var4; var11 <= var4 + 1 + var6 && var10; ++var11) {
            int var12 = 1;
            if (var11 - var4 < var7) {
               var12 = 0;
            } else {
               var12 = var9;
            }

            for(int var13 = var3 - var12; var13 <= var3 + var12 && var10; ++var13) {
               for(int var14 = var5 - var12; var14 <= var5 + var12 && var10; ++var14) {
                  if (var11 >= 0 && var11 < 256) {
                     Block var15 = var1.func_147439_a(var13, var11, var14);
                     if (!this.func_150523_a(var15)) {
                        var10 = false;
                     }
                  } else {
                     var10 = false;
                  }
               }
            }
         }

         if (!var10) {
            return false;
         } else {
            Block var18 = var1.func_147439_a(var3, var4 - 1, var5);
            if ((var18 == Blocks.field_150349_c || var18 == Blocks.field_150346_d) && var4 < 256 - var6 - 1) {
               this.func_150515_a(var1, var3, var4 - 1, var5, Blocks.field_150346_d);
               int var20 = 0;

               for(int var21 = var4 + var6; var21 >= var4 + var7; --var21) {
                  for(int var23 = var3 - var20; var23 <= var3 + var20; ++var23) {
                     int var25 = var23 - var3;

                     for(int var16 = var5 - var20; var16 <= var5 + var20; ++var16) {
                        int var17 = var16 - var5;
                        if ((Math.abs(var25) != var20 || Math.abs(var17) != var20 || var20 <= 0) && !var1.func_147439_a(var23, var21, var16).func_149730_j()) {
                           this.func_150516_a(var1, var23, var21, var16, Blocks.field_150362_t, 1);
                        }
                     }
                  }

                  if (var20 >= 1 && var21 == var4 + var7 + 1) {
                     --var20;
                  } else if (var20 < var9) {
                     ++var20;
                  }
               }

               for(int var22 = 0; var22 < var6 - 1; ++var22) {
                  Block var24 = var1.func_147439_a(var3, var4 + var22, var5);
                  if (var24.func_149688_o() == Material.field_151579_a || var24.func_149688_o() == Material.field_151584_j) {
                     this.func_150516_a(var1, var3, var4 + var22, var5, Blocks.field_150364_r, 1);
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
}
