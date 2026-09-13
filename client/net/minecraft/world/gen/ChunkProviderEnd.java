package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

public class ChunkProviderEnd implements IChunkProvider {
   private Random field_73204_i;
   private NoiseGeneratorOctaves field_73201_j;
   private NoiseGeneratorOctaves field_73202_k;
   private NoiseGeneratorOctaves field_73199_l;
   public NoiseGeneratorOctaves field_73196_a;
   public NoiseGeneratorOctaves field_73194_b;
   private World field_73200_m;
   private double[] field_73197_n;
   private BiomeGenBase[] field_73198_o;
   double[] field_73195_c;
   double[] field_73192_d;
   double[] field_73193_e;
   double[] field_73190_f;
   double[] field_73191_g;
   int[][] field_73203_h = new int[32][32];

   public ChunkProviderEnd(World var1, long var2) {
      super();
      this.field_73200_m = var1;
      this.field_73204_i = new Random(var2);
      this.field_73201_j = new NoiseGeneratorOctaves(this.field_73204_i, 16);
      this.field_73202_k = new NoiseGeneratorOctaves(this.field_73204_i, 16);
      this.field_73199_l = new NoiseGeneratorOctaves(this.field_73204_i, 8);
      this.field_73196_a = new NoiseGeneratorOctaves(this.field_73204_i, 10);
      this.field_73194_b = new NoiseGeneratorOctaves(this.field_73204_i, 16);
   }

   public void func_147420_a(int var1, int var2, Block[] var3, BiomeGenBase[] var4) {
      byte var5 = 2;
      int var6 = var5 + 1;
      byte var7 = 33;
      int var8 = var5 + 1;
      this.field_73197_n = this.func_73187_a(this.field_73197_n, var1 * var5, 0, var2 * var5, var6, var7, var8);

      for(int var9 = 0; var9 < var5; ++var9) {
         for(int var10 = 0; var10 < var5; ++var10) {
            for(int var11 = 0; var11 < 32; ++var11) {
               double var12 = 0.25;
               double var14 = this.field_73197_n[((var9 + 0) * var8 + var10 + 0) * var7 + var11 + 0];
               double var16 = this.field_73197_n[((var9 + 0) * var8 + var10 + 1) * var7 + var11 + 0];
               double var18 = this.field_73197_n[((var9 + 1) * var8 + var10 + 0) * var7 + var11 + 0];
               double var20 = this.field_73197_n[((var9 + 1) * var8 + var10 + 1) * var7 + var11 + 0];
               double var22 = (this.field_73197_n[((var9 + 0) * var8 + var10 + 0) * var7 + var11 + 1] - var14) * var12;
               double var24 = (this.field_73197_n[((var9 + 0) * var8 + var10 + 1) * var7 + var11 + 1] - var16) * var12;
               double var26 = (this.field_73197_n[((var9 + 1) * var8 + var10 + 0) * var7 + var11 + 1] - var18) * var12;
               double var28 = (this.field_73197_n[((var9 + 1) * var8 + var10 + 1) * var7 + var11 + 1] - var20) * var12;

               for(int var30 = 0; var30 < 4; ++var30) {
                  double var31 = 0.125;
                  double var33 = var14;
                  double var35 = var16;
                  double var37 = (var18 - var14) * var31;
                  double var39 = (var20 - var16) * var31;

                  for(int var41 = 0; var41 < 8; ++var41) {
                     int var42 = var41 + var9 * 8 << 11 | 0 + var10 * 8 << 7 | var11 * 4 + var30;
                     short var43 = 128;
                     double var44 = 0.125;
                     double var46 = var33;
                     double var48 = (var35 - var33) * var44;

                     for(int var50 = 0; var50 < 8; ++var50) {
                        Block var51 = null;
                        if (var46 > 0.0) {
                           var51 = Blocks.field_150377_bs;
                        }

                        var3[var42] = var51;
                        var42 += var43;
                        var46 += var48;
                     }

                     var33 += var37;
                     var35 += var39;
                  }

                  var14 += var22;
                  var16 += var24;
                  var18 += var26;
                  var20 += var28;
               }
            }
         }
      }
   }

   public void func_147421_b(int var1, int var2, Block[] var3, BiomeGenBase[] var4) {
      for(int var5 = 0; var5 < 16; ++var5) {
         for(int var6 = 0; var6 < 16; ++var6) {
            byte var7 = 1;
            int var8 = -1;
            Block var9 = Blocks.field_150377_bs;
            Block var10 = Blocks.field_150377_bs;

            for(int var11 = 127; var11 >= 0; --var11) {
               int var12 = (var6 * 16 + var5) * 128 + var11;
               Block var13 = var3[var12];
               if (var13 == null || var13.func_149688_o() == Material.field_151579_a) {
                  var8 = -1;
               } else if (var13 == Blocks.field_150348_b) {
                  if (var8 == -1) {
                     if (var7 <= 0) {
                        var9 = null;
                        var10 = Blocks.field_150377_bs;
                     }

                     var8 = var7;
                     if (var11 >= 0) {
                        var3[var12] = var9;
                     } else {
                        var3[var12] = var10;
                     }
                  } else if (var8 > 0) {
                     --var8;
                     var3[var12] = var10;
                  }
               }
            }
         }
      }
   }

   @Override
   public Chunk func_73158_c(int var1, int var2) {
      return this.func_73154_d(var1, var2);
   }

   @Override
   public Chunk func_73154_d(int var1, int var2) {
      this.field_73204_i.setSeed((long)var1 * 341873128712L + (long)var2 * 132897987541L);
      Block[] var3 = new Block[32768];
      this.field_73198_o = this.field_73200_m.func_72959_q().func_76933_b(this.field_73198_o, var1 * 16, var2 * 16, 16, 16);
      this.func_147420_a(var1, var2, var3, this.field_73198_o);
      this.func_147421_b(var1, var2, var3, this.field_73198_o);
      Chunk var4 = new Chunk(this.field_73200_m, var3, var1, var2);
      byte[] var5 = var4.func_76605_m();

      for(int var6 = 0; var6 < var5.length; ++var6) {
         var5[var6] = (byte)this.field_73198_o[var6].field_76756_M;
      }

      var4.func_76603_b();
      return var4;
   }

   private double[] func_73187_a(double[] var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      if (var1 == null) {
         var1 = new double[var5 * var6 * var7];
      }

      double var8 = 684.412;
      double var10 = 684.412;
      this.field_73190_f = this.field_73196_a.func_76305_a(this.field_73190_f, var2, var4, var5, var7, 1.121, 1.121, 0.5);
      this.field_73191_g = this.field_73194_b.func_76305_a(this.field_73191_g, var2, var4, var5, var7, 200.0, 200.0, 0.5);
      var8 *= 2.0;
      this.field_73195_c = this.field_73199_l.func_76304_a(this.field_73195_c, var2, var3, var4, var5, var6, var7, var8 / 80.0, var10 / 160.0, var8 / 80.0);
      this.field_73192_d = this.field_73201_j.func_76304_a(this.field_73192_d, var2, var3, var4, var5, var6, var7, var8, var10, var8);
      this.field_73193_e = this.field_73202_k.func_76304_a(this.field_73193_e, var2, var3, var4, var5, var6, var7, var8, var10, var8);
      int var12 = 0;
      int var13 = 0;

      for(int var14 = 0; var14 < var5; ++var14) {
         for(int var15 = 0; var15 < var7; ++var15) {
            double var16 = (this.field_73190_f[var13] + 256.0) / 512.0;
            if (var16 > 1.0) {
               var16 = 1.0;
            }

            double var18 = this.field_73191_g[var13] / 8000.0;
            if (var18 < 0.0) {
               var18 = -var18 * 0.3;
            }

            var18 = var18 * 3.0 - 2.0;
            float var20 = (float)(var14 + var2 - 0) / 1.0F;
            float var21 = (float)(var15 + var4 - 0) / 1.0F;
            float var22 = 100.0F - MathHelper.func_76129_c(var20 * var20 + var21 * var21) * 8.0F;
            if (var22 > 80.0F) {
               var22 = 80.0F;
            }

            if (var22 < -100.0F) {
               var22 = -100.0F;
            }

            if (var18 > 1.0) {
               var18 = 1.0;
            }

            var18 /= 8.0;
            var18 = 0.0;
            if (var16 < 0.0) {
               var16 = 0.0;
            }

            var16 += 0.5;
            var18 = var18 * (double)var6 / 16.0;
            ++var13;
            double var23 = (double)var6 / 2.0;

            for(int var25 = 0; var25 < var6; ++var25) {
               double var26 = 0.0;
               double var28 = ((double)var25 - var23) * 8.0 / var16;
               if (var28 < 0.0) {
                  var28 *= -1.0;
               }

               double var30 = this.field_73192_d[var12] / 512.0;
               double var32 = this.field_73193_e[var12] / 512.0;
               double var34 = (this.field_73195_c[var12] / 10.0 + 1.0) / 2.0;
               if (var34 < 0.0) {
                  var26 = var30;
               } else if (var34 > 1.0) {
                  var26 = var32;
               } else {
                  var26 = var30 + (var32 - var30) * var34;
               }

               var26 -= 8.0;
               var26 += (double)var22;
               byte var36 = 2;
               if (var25 > var6 / 2 - var36) {
                  double var37 = (double)((float)(var25 - (var6 / 2 - var36)) / 64.0F);
                  if (var37 < 0.0) {
                     var37 = 0.0;
                  }

                  if (var37 > 1.0) {
                     var37 = 1.0;
                  }

                  var26 = var26 * (1.0 - var37) + -3000.0 * var37;
               }

               var36 = 8;
               if (var25 < var36) {
                  double var50 = (double)((float)(var36 - var25) / ((float)var36 - 1.0F));
                  var26 = var26 * (1.0 - var50) + -30.0 * var50;
               }

               var1[var12] = var26;
               ++var12;
            }
         }
      }

      return var1;
   }

   @Override
   public boolean func_73149_a(int var1, int var2) {
      return true;
   }

   @Override
   public void func_73153_a(IChunkProvider var1, int var2, int var3) {
      BlockFalling.field_149832_M = true;
      int var4 = var2 * 16;
      int var5 = var3 * 16;
      BiomeGenBase var6 = this.field_73200_m.func_72807_a(var4 + 16, var5 + 16);
      var6.func_76728_a(this.field_73200_m, this.field_73200_m.field_73012_v, var4, var5);
      BlockFalling.field_149832_M = false;
   }

   @Override
   public boolean func_73151_a(boolean var1, IProgressUpdate var2) {
      return true;
   }

   @Override
   public void func_104112_b() {
   }

   @Override
   public boolean func_73156_b() {
      return false;
   }

   @Override
   public boolean func_73157_c() {
      return true;
   }

   @Override
   public String func_73148_d() {
      return "RandomLevelSource";
   }

   @Override
   public List func_73155_a(EnumCreatureType var1, int var2, int var3, int var4) {
      BiomeGenBase var5 = this.field_73200_m.func_72807_a(var2, var4);
      return var5.func_76747_a(var1);
   }

   @Override
   public ChunkPosition func_147416_a(World var1, String var2, int var3, int var4, int var5) {
      return null;
   }

   @Override
   public int func_73152_e() {
      return 0;
   }

   @Override
   public void func_82695_e(int var1, int var2) {
   }
}
