package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class LakesFeature extends Feature<LakesConfig> {
   private static final IBlockState field_205188_a;

   public LakesFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, LakesConfig var5) {
      while(var4.func_177956_o() > 5 && var1.func_175623_d(var4)) {
         var4 = var4.func_177977_b();
      }

      if (var4.func_177956_o() <= 4) {
         return false;
      } else {
         var4 = var4.func_177979_c(4);
         boolean[] var6 = new boolean[2048];
         int var7 = var3.nextInt(4) + 4;

         int var8;
         for(var8 = 0; var8 < var7; ++var8) {
            double var9 = var3.nextDouble() * 6.0D + 3.0D;
            double var11 = var3.nextDouble() * 4.0D + 2.0D;
            double var13 = var3.nextDouble() * 6.0D + 3.0D;
            double var15 = var3.nextDouble() * (16.0D - var9 - 2.0D) + 1.0D + var9 / 2.0D;
            double var17 = var3.nextDouble() * (8.0D - var11 - 4.0D) + 2.0D + var11 / 2.0D;
            double var19 = var3.nextDouble() * (16.0D - var13 - 2.0D) + 1.0D + var13 / 2.0D;

            for(int var21 = 1; var21 < 15; ++var21) {
               for(int var22 = 1; var22 < 15; ++var22) {
                  for(int var23 = 1; var23 < 7; ++var23) {
                     double var24 = ((double)var21 - var15) / (var9 / 2.0D);
                     double var26 = ((double)var23 - var17) / (var11 / 2.0D);
                     double var28 = ((double)var22 - var19) / (var13 / 2.0D);
                     double var30 = var24 * var24 + var26 * var26 + var28 * var28;
                     if (var30 < 1.0D) {
                        var6[(var21 * 16 + var22) * 8 + var23] = true;
                     }
                  }
               }
            }
         }

         int var10;
         int var32;
         boolean var33;
         for(var8 = 0; var8 < 16; ++var8) {
            for(var32 = 0; var32 < 16; ++var32) {
               for(var10 = 0; var10 < 8; ++var10) {
                  var33 = !var6[(var8 * 16 + var32) * 8 + var10] && (var8 < 15 && var6[((var8 + 1) * 16 + var32) * 8 + var10] || var8 > 0 && var6[((var8 - 1) * 16 + var32) * 8 + var10] || var32 < 15 && var6[(var8 * 16 + var32 + 1) * 8 + var10] || var32 > 0 && var6[(var8 * 16 + (var32 - 1)) * 8 + var10] || var10 < 7 && var6[(var8 * 16 + var32) * 8 + var10 + 1] || var10 > 0 && var6[(var8 * 16 + var32) * 8 + (var10 - 1)]);
                  if (var33) {
                     Material var12 = var1.func_180495_p(var4.func_177982_a(var8, var10, var32)).func_185904_a();
                     if (var10 >= 4 && var12.func_76224_d()) {
                        return false;
                     }

                     if (var10 < 4 && !var12.func_76220_a() && var1.func_180495_p(var4.func_177982_a(var8, var10, var32)).func_177230_c() != var5.field_202438_a) {
                        return false;
                     }
                  }
               }
            }
         }

         for(var8 = 0; var8 < 16; ++var8) {
            for(var32 = 0; var32 < 16; ++var32) {
               for(var10 = 0; var10 < 8; ++var10) {
                  if (var6[(var8 * 16 + var32) * 8 + var10]) {
                     var1.func_180501_a(var4.func_177982_a(var8, var10, var32), var10 >= 4 ? field_205188_a : var5.field_202438_a.func_176223_P(), 2);
                  }
               }
            }
         }

         BlockPos var34;
         for(var8 = 0; var8 < 16; ++var8) {
            for(var32 = 0; var32 < 16; ++var32) {
               for(var10 = 4; var10 < 8; ++var10) {
                  if (var6[(var8 * 16 + var32) * 8 + var10]) {
                     var34 = var4.func_177982_a(var8, var10 - 1, var32);
                     if (Block.func_196245_f(var1.func_180495_p(var34).func_177230_c()) && var1.func_175642_b(EnumLightType.SKY, var4.func_177982_a(var8, var10, var32)) > 0) {
                        Biome var35 = var1.func_180494_b(var34);
                        if (var35.func_203944_q().func_204108_a().func_177230_c() == Blocks.field_150391_bh) {
                           var1.func_180501_a(var34, Blocks.field_150391_bh.func_176223_P(), 2);
                        } else {
                           var1.func_180501_a(var34, Blocks.field_196658_i.func_176223_P(), 2);
                        }
                     }
                  }
               }
            }
         }

         if (var5.field_202438_a.func_176223_P().func_185904_a() == Material.field_151587_i) {
            for(var8 = 0; var8 < 16; ++var8) {
               for(var32 = 0; var32 < 16; ++var32) {
                  for(var10 = 0; var10 < 8; ++var10) {
                     var33 = !var6[(var8 * 16 + var32) * 8 + var10] && (var8 < 15 && var6[((var8 + 1) * 16 + var32) * 8 + var10] || var8 > 0 && var6[((var8 - 1) * 16 + var32) * 8 + var10] || var32 < 15 && var6[(var8 * 16 + var32 + 1) * 8 + var10] || var32 > 0 && var6[(var8 * 16 + (var32 - 1)) * 8 + var10] || var10 < 7 && var6[(var8 * 16 + var32) * 8 + var10 + 1] || var10 > 0 && var6[(var8 * 16 + var32) * 8 + (var10 - 1)]);
                     if (var33 && (var10 < 4 || var3.nextInt(2) != 0) && var1.func_180495_p(var4.func_177982_a(var8, var10, var32)).func_185904_a().func_76220_a()) {
                        var1.func_180501_a(var4.func_177982_a(var8, var10, var32), Blocks.field_150348_b.func_176223_P(), 2);
                     }
                  }
               }
            }
         }

         if (var5.field_202438_a.func_176223_P().func_185904_a() == Material.field_151586_h) {
            for(var8 = 0; var8 < 16; ++var8) {
               for(var32 = 0; var32 < 16; ++var32) {
                  boolean var36 = true;
                  var34 = var4.func_177982_a(var8, 4, var32);
                  if (var1.func_180494_b(var34).func_201854_a(var1, var34, false)) {
                     var1.func_180501_a(var34, Blocks.field_150432_aD.func_176223_P(), 2);
                  }
               }
            }
         }

         return true;
      }
   }

   static {
      field_205188_a = Blocks.field_201941_jj.func_176223_P();
   }
}
