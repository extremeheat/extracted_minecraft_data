package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class WorldGenLakes extends WorldGenerator {
   private Block field_150556_a;

   public WorldGenLakes(Block var1) {
      super();
      this.field_150556_a = var1;
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      for(var3 = var3.func_177982_a(-8, 0, -8); var3.func_177956_o() > 5 && var1.func_175623_d(var3); var3 = var3.func_177977_b()) {
      }

      if (var3.func_177956_o() <= 4) {
         return false;
      } else {
         var3 = var3.func_177979_c(4);
         boolean[] var4 = new boolean[2048];
         int var5 = var2.nextInt(4) + 4;

         int var6;
         for(var6 = 0; var6 < var5; ++var6) {
            double var7 = var2.nextDouble() * 6.0D + 3.0D;
            double var9 = var2.nextDouble() * 4.0D + 2.0D;
            double var11 = var2.nextDouble() * 6.0D + 3.0D;
            double var13 = var2.nextDouble() * (16.0D - var7 - 2.0D) + 1.0D + var7 / 2.0D;
            double var15 = var2.nextDouble() * (8.0D - var9 - 4.0D) + 2.0D + var9 / 2.0D;
            double var17 = var2.nextDouble() * (16.0D - var11 - 2.0D) + 1.0D + var11 / 2.0D;

            for(int var19 = 1; var19 < 15; ++var19) {
               for(int var20 = 1; var20 < 15; ++var20) {
                  for(int var21 = 1; var21 < 7; ++var21) {
                     double var22 = ((double)var19 - var13) / (var7 / 2.0D);
                     double var24 = ((double)var21 - var15) / (var9 / 2.0D);
                     double var26 = ((double)var20 - var17) / (var11 / 2.0D);
                     double var28 = var22 * var22 + var24 * var24 + var26 * var26;
                     if (var28 < 1.0D) {
                        var4[(var19 * 16 + var20) * 8 + var21] = true;
                     }
                  }
               }
            }
         }

         int var8;
         int var30;
         boolean var31;
         for(var6 = 0; var6 < 16; ++var6) {
            for(var30 = 0; var30 < 16; ++var30) {
               for(var8 = 0; var8 < 8; ++var8) {
                  var31 = !var4[(var6 * 16 + var30) * 8 + var8] && (var6 < 15 && var4[((var6 + 1) * 16 + var30) * 8 + var8] || var6 > 0 && var4[((var6 - 1) * 16 + var30) * 8 + var8] || var30 < 15 && var4[(var6 * 16 + var30 + 1) * 8 + var8] || var30 > 0 && var4[(var6 * 16 + (var30 - 1)) * 8 + var8] || var8 < 7 && var4[(var6 * 16 + var30) * 8 + var8 + 1] || var8 > 0 && var4[(var6 * 16 + var30) * 8 + (var8 - 1)]);
                  if (var31) {
                     Material var10 = var1.func_180495_p(var3.func_177982_a(var6, var8, var30)).func_177230_c().func_149688_o();
                     if (var8 >= 4 && var10.func_76224_d()) {
                        return false;
                     }

                     if (var8 < 4 && !var10.func_76220_a() && var1.func_180495_p(var3.func_177982_a(var6, var8, var30)).func_177230_c() != this.field_150556_a) {
                        return false;
                     }
                  }
               }
            }
         }

         for(var6 = 0; var6 < 16; ++var6) {
            for(var30 = 0; var30 < 16; ++var30) {
               for(var8 = 0; var8 < 8; ++var8) {
                  if (var4[(var6 * 16 + var30) * 8 + var8]) {
                     var1.func_180501_a(var3.func_177982_a(var6, var8, var30), var8 >= 4 ? Blocks.field_150350_a.func_176223_P() : this.field_150556_a.func_176223_P(), 2);
                  }
               }
            }
         }

         for(var6 = 0; var6 < 16; ++var6) {
            for(var30 = 0; var30 < 16; ++var30) {
               for(var8 = 4; var8 < 8; ++var8) {
                  if (var4[(var6 * 16 + var30) * 8 + var8]) {
                     BlockPos var32 = var3.func_177982_a(var6, var8 - 1, var30);
                     if (var1.func_180495_p(var32).func_177230_c() == Blocks.field_150346_d && var1.func_175642_b(EnumSkyBlock.SKY, var3.func_177982_a(var6, var8, var30)) > 0) {
                        BiomeGenBase var33 = var1.func_180494_b(var32);
                        if (var33.field_76752_A.func_177230_c() == Blocks.field_150391_bh) {
                           var1.func_180501_a(var32, Blocks.field_150391_bh.func_176223_P(), 2);
                        } else {
                           var1.func_180501_a(var32, Blocks.field_150349_c.func_176223_P(), 2);
                        }
                     }
                  }
               }
            }
         }

         if (this.field_150556_a.func_149688_o() == Material.field_151587_i) {
            for(var6 = 0; var6 < 16; ++var6) {
               for(var30 = 0; var30 < 16; ++var30) {
                  for(var8 = 0; var8 < 8; ++var8) {
                     var31 = !var4[(var6 * 16 + var30) * 8 + var8] && (var6 < 15 && var4[((var6 + 1) * 16 + var30) * 8 + var8] || var6 > 0 && var4[((var6 - 1) * 16 + var30) * 8 + var8] || var30 < 15 && var4[(var6 * 16 + var30 + 1) * 8 + var8] || var30 > 0 && var4[(var6 * 16 + (var30 - 1)) * 8 + var8] || var8 < 7 && var4[(var6 * 16 + var30) * 8 + var8 + 1] || var8 > 0 && var4[(var6 * 16 + var30) * 8 + (var8 - 1)]);
                     if (var31 && (var8 < 4 || var2.nextInt(2) != 0) && var1.func_180495_p(var3.func_177982_a(var6, var8, var30)).func_177230_c().func_149688_o().func_76220_a()) {
                        var1.func_180501_a(var3.func_177982_a(var6, var8, var30), Blocks.field_150348_b.func_176223_P(), 2);
                     }
                  }
               }
            }
         }

         if (this.field_150556_a.func_149688_o() == Material.field_151586_h) {
            for(var6 = 0; var6 < 16; ++var6) {
               for(var30 = 0; var30 < 16; ++var30) {
                  byte var34 = 4;
                  if (var1.func_175675_v(var3.func_177982_a(var6, var34, var30))) {
                     var1.func_180501_a(var3.func_177982_a(var6, var34, var30), Blocks.field_150432_aD.func_176223_P(), 2);
                  }
               }
            }
         }

         return true;
      }
   }
}
