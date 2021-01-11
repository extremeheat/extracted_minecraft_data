package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenIceSpike extends WorldGenerator {
   public WorldGenIceSpike() {
      super();
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      while(var1.func_175623_d(var3) && var3.func_177956_o() > 2) {
         var3 = var3.func_177977_b();
      }

      if (var1.func_180495_p(var3).func_177230_c() != Blocks.field_150433_aE) {
         return false;
      } else {
         var3 = var3.func_177981_b(var2.nextInt(4));
         int var4 = var2.nextInt(4) + 7;
         int var5 = var4 / 4 + var2.nextInt(2);
         if (var5 > 1 && var2.nextInt(60) == 0) {
            var3 = var3.func_177981_b(10 + var2.nextInt(30));
         }

         int var6;
         int var8;
         for(var6 = 0; var6 < var4; ++var6) {
            float var7 = (1.0F - (float)var6 / (float)var4) * (float)var5;
            var8 = MathHelper.func_76123_f(var7);

            for(int var9 = -var8; var9 <= var8; ++var9) {
               float var10 = (float)MathHelper.func_76130_a(var9) - 0.25F;

               for(int var11 = -var8; var11 <= var8; ++var11) {
                  float var12 = (float)MathHelper.func_76130_a(var11) - 0.25F;
                  if ((var9 == 0 && var11 == 0 || var10 * var10 + var12 * var12 <= var7 * var7) && (var9 != -var8 && var9 != var8 && var11 != -var8 && var11 != var8 || var2.nextFloat() <= 0.75F)) {
                     Block var13 = var1.func_180495_p(var3.func_177982_a(var9, var6, var11)).func_177230_c();
                     if (var13.func_149688_o() == Material.field_151579_a || var13 == Blocks.field_150346_d || var13 == Blocks.field_150433_aE || var13 == Blocks.field_150432_aD) {
                        this.func_175903_a(var1, var3.func_177982_a(var9, var6, var11), Blocks.field_150403_cj.func_176223_P());
                     }

                     if (var6 != 0 && var8 > 1) {
                        var13 = var1.func_180495_p(var3.func_177982_a(var9, -var6, var11)).func_177230_c();
                        if (var13.func_149688_o() == Material.field_151579_a || var13 == Blocks.field_150346_d || var13 == Blocks.field_150433_aE || var13 == Blocks.field_150432_aD) {
                           this.func_175903_a(var1, var3.func_177982_a(var9, -var6, var11), Blocks.field_150403_cj.func_176223_P());
                        }
                     }
                  }
               }
            }
         }

         var6 = var5 - 1;
         if (var6 < 0) {
            var6 = 0;
         } else if (var6 > 1) {
            var6 = 1;
         }

         for(int var14 = -var6; var14 <= var6; ++var14) {
            for(var8 = -var6; var8 <= var6; ++var8) {
               BlockPos var15 = var3.func_177982_a(var14, -1, var8);
               int var16 = 50;
               if (Math.abs(var14) == 1 && Math.abs(var8) == 1) {
                  var16 = var2.nextInt(5);
               }

               while(var15.func_177956_o() > 50) {
                  Block var17 = var1.func_180495_p(var15).func_177230_c();
                  if (var17.func_149688_o() != Material.field_151579_a && var17 != Blocks.field_150346_d && var17 != Blocks.field_150433_aE && var17 != Blocks.field_150432_aD && var17 != Blocks.field_150403_cj) {
                     break;
                  }

                  this.func_175903_a(var1, var15, Blocks.field_150403_cj.func_176223_P());
                  var15 = var15.func_177977_b();
                  --var16;
                  if (var16 <= 0) {
                     var15 = var15.func_177979_c(var2.nextInt(5) + 1);
                     var16 = var2.nextInt(5);
                  }
               }
            }
         }

         return true;
      }
   }
}
