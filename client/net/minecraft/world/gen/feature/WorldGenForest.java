package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenForest extends WorldGenAbstractTree {
   private boolean field_150531_a;

   public WorldGenForest(boolean var1, boolean var2) {
      super(var1);
      this.field_150531_a = var2;
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      int var6 = var2.nextInt(3) + 5;
      if (this.field_150531_a) {
         var6 += var2.nextInt(7);
      }

      boolean var7 = true;
      if (var4 >= 1 && var4 + var6 + 1 <= 256) {
         for(int var8 = var4; var8 <= var4 + 1 + var6; ++var8) {
            byte var9 = 1;
            if (var8 == var4) {
               var9 = 0;
            }

            if (var8 >= var4 + 1 + var6 - 2) {
               var9 = 2;
            }

            for(int var10 = var3 - var9; var10 <= var3 + var9 && var7; ++var10) {
               for(int var11 = var5 - var9; var11 <= var5 + var9 && var7; ++var11) {
                  if (var8 >= 0 && var8 < 256) {
                     Block var12 = var1.func_147439_a(var10, var8, var11);
                     if (!this.func_150523_a(var12)) {
                        var7 = false;
                     }
                  } else {
                     var7 = false;
                  }
               }
            }
         }

         if (!var7) {
            return false;
         } else {
            Block var17 = var1.func_147439_a(var3, var4 - 1, var5);
            if ((var17 == Blocks.field_150349_c || var17 == Blocks.field_150346_d || var17 == Blocks.field_150458_ak) && var4 < 256 - var6 - 1) {
               this.func_150515_a(var1, var3, var4 - 1, var5, Blocks.field_150346_d);

               for(int var18 = var4 - 3 + var6; var18 <= var4 + var6; ++var18) {
                  int var20 = var18 - (var4 + var6);
                  int var22 = 1 - var20 / 2;

                  for(int var23 = var3 - var22; var23 <= var3 + var22; ++var23) {
                     int var13 = var23 - var3;

                     for(int var14 = var5 - var22; var14 <= var5 + var22; ++var14) {
                        int var15 = var14 - var5;
                        if (Math.abs(var13) != var22 || Math.abs(var15) != var22 || var2.nextInt(2) != 0 && var20 != 0) {
                           Block var16 = var1.func_147439_a(var23, var18, var14);
                           if (var16.func_149688_o() == Material.field_151579_a || var16.func_149688_o() == Material.field_151584_j) {
                              this.func_150516_a(var1, var23, var18, var14, Blocks.field_150362_t, 2);
                           }
                        }
                     }
                  }
               }

               for(int var19 = 0; var19 < var6; ++var19) {
                  Block var21 = var1.func_147439_a(var3, var4 + var19, var5);
                  if (var21.func_149688_o() == Material.field_151579_a || var21.func_149688_o() == Material.field_151584_j) {
                     this.func_150516_a(var1, var3, var4 + var19, var5, Blocks.field_150364_r, 2);
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
