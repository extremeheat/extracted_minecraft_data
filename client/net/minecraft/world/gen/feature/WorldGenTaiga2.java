package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenTaiga2 extends WorldGenAbstractTree {
   public WorldGenTaiga2(boolean var1) {
      super(var1);
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      int var6 = var2.nextInt(4) + 6;
      int var7 = 1 + var2.nextInt(2);
      int var8 = var6 - var7;
      int var9 = 2 + var2.nextInt(2);
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
                     if (var15.func_149688_o() != Material.field_151579_a && var15.func_149688_o() != Material.field_151584_j) {
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
            Block var21 = var1.func_147439_a(var3, var4 - 1, var5);
            if ((var21 == Blocks.field_150349_c || var21 == Blocks.field_150346_d || var21 == Blocks.field_150458_ak) && var4 < 256 - var6 - 1) {
               this.func_150515_a(var1, var3, var4 - 1, var5, Blocks.field_150346_d);
               int var23 = var2.nextInt(2);
               int var24 = 1;
               byte var25 = 0;

               for(int var26 = 0; var26 <= var8; ++var26) {
                  int var16 = var4 + var6 - var26;

                  for(int var17 = var3 - var23; var17 <= var3 + var23; ++var17) {
                     int var18 = var17 - var3;

                     for(int var19 = var5 - var23; var19 <= var5 + var23; ++var19) {
                        int var20 = var19 - var5;
                        if ((Math.abs(var18) != var23 || Math.abs(var20) != var23 || var23 <= 0) && !var1.func_147439_a(var17, var16, var19).func_149730_j()) {
                           this.func_150516_a(var1, var17, var16, var19, Blocks.field_150362_t, 1);
                        }
                     }
                  }

                  if (var23 >= var24) {
                     var23 = var25;
                     var25 = 1;
                     if (++var24 > var9) {
                        var24 = var9;
                     }
                  } else {
                     ++var23;
                  }
               }

               int var27 = var2.nextInt(3);

               for(int var28 = 0; var28 < var6 - var27; ++var28) {
                  Block var29 = var1.func_147439_a(var3, var4 + var28, var5);
                  if (var29.func_149688_o() == Material.field_151579_a || var29.func_149688_o() == Material.field_151584_j) {
                     this.func_150516_a(var1, var3, var4 + var28, var5, Blocks.field_150364_r, 1);
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
