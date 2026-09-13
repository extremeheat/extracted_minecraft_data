package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenIceSpike extends WorldGenerator {
   public WorldGenIceSpike() {
      super();
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      while(var1.func_147437_c(var3, var4, var5) && var4 > 2) {
         --var4;
      }

      if (var1.func_147439_a(var3, var4, var5) != Blocks.field_150433_aE) {
         return false;
      } else {
         var4 += var2.nextInt(4);
         int var6 = var2.nextInt(4) + 7;
         int var7 = var6 / 4 + var2.nextInt(2);
         if (var7 > 1 && var2.nextInt(60) == 0) {
            var4 += 10 + var2.nextInt(30);
         }

         for(int var8 = 0; var8 < var6; ++var8) {
            float var9 = (1.0F - (float)var8 / (float)var6) * (float)var7;
            int var10 = MathHelper.func_76123_f(var9);

            for(int var11 = -var10; var11 <= var10; ++var11) {
               float var12 = (float)MathHelper.func_76130_a(var11) - 0.25F;

               for(int var13 = -var10; var13 <= var10; ++var13) {
                  float var14 = (float)MathHelper.func_76130_a(var13) - 0.25F;
                  if ((var11 == 0 && var13 == 0 || !(var12 * var12 + var14 * var14 > var9 * var9))
                     && (var11 != -var10 && var11 != var10 && var13 != -var10 && var13 != var10 || !(var2.nextFloat() > 0.75F))) {
                     Block var15 = var1.func_147439_a(var3 + var11, var4 + var8, var5 + var13);
                     if (var15.func_149688_o() == Material.field_151579_a
                        || var15 == Blocks.field_150346_d
                        || var15 == Blocks.field_150433_aE
                        || var15 == Blocks.field_150432_aD) {
                        this.func_150515_a(var1, var3 + var11, var4 + var8, var5 + var13, Blocks.field_150403_cj);
                     }

                     if (var8 != 0 && var10 > 1) {
                        var15 = var1.func_147439_a(var3 + var11, var4 - var8, var5 + var13);
                        if (var15.func_149688_o() == Material.field_151579_a
                           || var15 == Blocks.field_150346_d
                           || var15 == Blocks.field_150433_aE
                           || var15 == Blocks.field_150432_aD) {
                           this.func_150515_a(var1, var3 + var11, var4 - var8, var5 + var13, Blocks.field_150403_cj);
                        }
                     }
                  }
               }
            }
         }

         int var17 = var7 - 1;
         if (var17 < 0) {
            var17 = 0;
         } else if (var17 > 1) {
            var17 = 1;
         }

         for(int var18 = -var17; var18 <= var17; ++var18) {
            for(int var19 = -var17; var19 <= var17; ++var19) {
               int var20 = var4 - 1;
               int var21 = 50;
               if (Math.abs(var18) == 1 && Math.abs(var19) == 1) {
                  var21 = var2.nextInt(5);
               }

               while(var20 > 50) {
                  Block var22 = var1.func_147439_a(var3 + var18, var20, var5 + var19);
                  if (var22.func_149688_o() != Material.field_151579_a
                     && var22 != Blocks.field_150346_d
                     && var22 != Blocks.field_150433_aE
                     && var22 != Blocks.field_150432_aD
                     && var22 != Blocks.field_150403_cj) {
                     break;
                  }

                  this.func_150515_a(var1, var3 + var18, var20, var5 + var19, Blocks.field_150403_cj);
                  --var20;
                  if (--var21 <= 0) {
                     var20 -= var2.nextInt(5) + 1;
                     var21 = var2.nextInt(5);
                  }
               }
            }
         }

         return true;
      }
   }
}
