package net.minecraft.world.biome;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenMesa extends BiomeGenBase {
   private byte[] field_150621_aC;
   private long field_150622_aD;
   private NoiseGeneratorPerlin field_150623_aE;
   private NoiseGeneratorPerlin field_150624_aF;
   private NoiseGeneratorPerlin field_150625_aG;
   private boolean field_150626_aH;
   private boolean field_150620_aI;

   public BiomeGenMesa(int var1, boolean var2, boolean var3) {
      super(var1);
      this.field_150626_aH = var2;
      this.field_150620_aI = var3;
      this.func_76745_m();
      this.func_76732_a(2.0F, 0.0F);
      this.field_76762_K.clear();
      this.field_76752_A = Blocks.field_150354_m;
      this.field_150604_aj = 1;
      this.field_76753_B = Blocks.field_150406_ce;
      this.field_76760_I.field_76832_z = -999;
      this.field_76760_I.field_76804_C = 20;
      this.field_76760_I.field_76799_E = 3;
      this.field_76760_I.field_76800_F = 5;
      this.field_76760_I.field_76802_A = 0;
      this.field_76762_K.clear();
      if (var3) {
         this.field_76760_I.field_76832_z = 5;
      }
   }

   @Override
   public WorldGenAbstractTree func_150567_a(Random var1) {
      return this.field_76757_N;
   }

   @Override
   public int func_150571_c(int var1, int var2, int var3) {
      return 10387789;
   }

   @Override
   public int func_150558_b(int var1, int var2, int var3) {
      return 9470285;
   }

   @Override
   public void func_76728_a(World var1, Random var2, int var3, int var4) {
      super.func_76728_a(var1, var2, var3, var4);
   }

   @Override
   public void func_150573_a(World var1, Random var2, Block[] var3, byte[] var4, int var5, int var6, double var7) {
      if (this.field_150621_aC == null || this.field_150622_aD != var1.func_72905_C()) {
         this.func_150619_a(var1.func_72905_C());
      }

      if (this.field_150623_aE == null || this.field_150624_aF == null || this.field_150622_aD != var1.func_72905_C()) {
         Random var9 = new Random(this.field_150622_aD);
         this.field_150623_aE = new NoiseGeneratorPerlin(var9, 4);
         this.field_150624_aF = new NoiseGeneratorPerlin(var9, 1);
      }

      this.field_150622_aD = var1.func_72905_C();
      double var25 = 0.0;
      if (this.field_150626_aH) {
         int var11 = (var5 & -16) + (var6 & 15);
         int var12 = (var6 & -16) + (var5 & 15);
         double var13 = Math.min(Math.abs(var7), this.field_150623_aE.func_151601_a((double)var11 * 0.25, (double)var12 * 0.25));
         if (var13 > 0.0) {
            double var15 = 0.001953125;
            double var17 = Math.abs(this.field_150624_aF.func_151601_a((double)var11 * var15, (double)var12 * var15));
            var25 = var13 * var13 * 2.5;
            double var19 = Math.ceil(var17 * 50.0) + 14.0;
            if (var25 > var19) {
               var25 = var19;
            }

            var25 += 64.0;
         }
      }

      int var27 = var5 & 15;
      int var28 = var6 & 15;
      boolean var29 = true;
      Block var14 = Blocks.field_150406_ce;
      Block var30 = this.field_76753_B;
      int var16 = (int)(var7 / 3.0 + 3.0 + var2.nextDouble() * 0.25);
      boolean var31 = Math.cos(var7 / 3.0 * 3.141592653589793) > 0.0;
      int var18 = -1;
      boolean var32 = false;
      int var20 = var3.length / 256;

      for(int var21 = 255; var21 >= 0; --var21) {
         int var22 = (var28 * 16 + var27) * var20 + var21;
         if ((var3[var22] == null || var3[var22].func_149688_o() == Material.field_151579_a) && var21 < (int)var25) {
            var3[var22] = Blocks.field_150348_b;
         }

         if (var21 <= 0 + var2.nextInt(5)) {
            var3[var22] = Blocks.field_150357_h;
         } else {
            Block var23 = var3[var22];
            if (var23 == null || var23.func_149688_o() == Material.field_151579_a) {
               var18 = -1;
            } else if (var23 == Blocks.field_150348_b) {
               if (var18 == -1) {
                  var32 = false;
                  if (var16 <= 0) {
                     var14 = null;
                     var30 = Blocks.field_150348_b;
                  } else if (var21 >= 59 && var21 <= 64) {
                     var14 = Blocks.field_150406_ce;
                     var30 = this.field_76753_B;
                  }

                  if (var21 < 63 && (var14 == null || var14.func_149688_o() == Material.field_151579_a)) {
                     var14 = Blocks.field_150355_j;
                  }

                  var18 = var16 + Math.max(0, var21 - 63);
                  if (var21 >= 62) {
                     if (this.field_150620_aI && var21 > 86 + var16 * 2) {
                        if (var31) {
                           var3[var22] = Blocks.field_150346_d;
                           var4[var22] = 1;
                        } else {
                           var3[var22] = Blocks.field_150349_c;
                        }
                     } else if (var21 > 66 + var16) {
                        byte var24 = 16;
                        if (var21 < 64 || var21 > 127) {
                           var24 = 1;
                        } else if (!var31) {
                           var24 = this.func_150618_d(var5, var21, var6);
                        }

                        if (var24 < 16) {
                           var3[var22] = Blocks.field_150406_ce;
                           var4[var22] = (byte)var24;
                        } else {
                           var3[var22] = Blocks.field_150405_ch;
                        }
                     } else {
                        var3[var22] = this.field_76752_A;
                        var4[var22] = (byte)this.field_150604_aj;
                        var32 = true;
                     }
                  } else {
                     var3[var22] = var30;
                     if (var30 == Blocks.field_150406_ce) {
                        var4[var22] = 1;
                     }
                  }
               } else if (var18 > 0) {
                  --var18;
                  if (var32) {
                     var3[var22] = Blocks.field_150406_ce;
                     var4[var22] = 1;
                  } else {
                     byte var33 = this.func_150618_d(var5, var21, var6);
                     if (var33 < 16) {
                        var3[var22] = Blocks.field_150406_ce;
                        var4[var22] = var33;
                     } else {
                        var3[var22] = Blocks.field_150405_ch;
                     }
                  }
               }
            }
         }
      }
   }

   private void func_150619_a(long var1) {
      this.field_150621_aC = new byte[64];
      Arrays.fill(this.field_150621_aC, (byte)16);
      Random var3 = new Random(var1);
      this.field_150625_aG = new NoiseGeneratorPerlin(var3, 1);

      for(int var12 = 0; var12 < 64; ++var12) {
         var12 += var3.nextInt(5) + 1;
         if (var12 < 64) {
            this.field_150621_aC[var12] = 1;
         }
      }

      int var13 = var3.nextInt(4) + 2;

      for(int var5 = 0; var5 < var13; ++var5) {
         int var6 = var3.nextInt(3) + 1;
         int var7 = var3.nextInt(64);

         for(int var8 = 0; var7 + var8 < 64 && var8 < var6; ++var8) {
            this.field_150621_aC[var7 + var8] = 4;
         }
      }

      int var14 = var3.nextInt(4) + 2;

      for(int var15 = 0; var15 < var14; ++var15) {
         int var17 = var3.nextInt(3) + 2;
         int var20 = var3.nextInt(64);

         for(int var9 = 0; var20 + var9 < 64 && var9 < var17; ++var9) {
            this.field_150621_aC[var20 + var9] = 12;
         }
      }

      int var16 = var3.nextInt(4) + 2;

      for(int var18 = 0; var18 < var16; ++var18) {
         int var21 = var3.nextInt(3) + 1;
         int var23 = var3.nextInt(64);

         for(int var10 = 0; var23 + var10 < 64 && var10 < var21; ++var10) {
            this.field_150621_aC[var23 + var10] = 14;
         }
      }

      int var19 = var3.nextInt(3) + 3;
      int var22 = 0;

      for(int var24 = 0; var24 < var19; ++var24) {
         byte var25 = 1;
         var22 += var3.nextInt(16) + 4;

         for(int var11 = 0; var22 + var11 < 64 && var11 < var25; ++var11) {
            this.field_150621_aC[var22 + var11] = 0;
            if (var22 + var11 > 1 && var3.nextBoolean()) {
               this.field_150621_aC[var22 + var11 - 1] = 8;
            }

            if (var22 + var11 < 63 && var3.nextBoolean()) {
               this.field_150621_aC[var22 + var11 + 1] = 8;
            }
         }
      }
   }

   private byte func_150618_d(int var1, int var2, int var3) {
      int var4 = (int)Math.round(this.field_150625_aG.func_151601_a((double)var1 * 1.0 / 512.0, (double)var1 * 1.0 / 512.0) * 2.0);
      return this.field_150621_aC[(var2 + var4 + 64) % 64];
   }

   @Override
   protected BiomeGenBase func_150566_k() {
      boolean var1 = this.field_76756_M == BiomeGenBase.field_150589_Z.field_76756_M;
      BiomeGenMesa var2 = new BiomeGenMesa(this.field_76756_M + 128, var1, this.field_150620_aI);
      if (!var1) {
         var2.func_150570_a(field_150591_g);
         var2.func_76735_a(this.field_76791_y + " M");
      } else {
         var2.func_76735_a(this.field_76791_y + " (Bryce)");
      }

      var2.func_150557_a(this.field_76790_z, true);
      return var2;
   }
}
