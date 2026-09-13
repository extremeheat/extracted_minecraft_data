package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenFire;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenGlowStone1;
import net.minecraft.world.gen.feature.WorldGenGlowStone2;
import net.minecraft.world.gen.feature.WorldGenHellLava;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.structure.MapGenNetherBridge;

public class ChunkProviderHell implements IChunkProvider {
   private Random field_73181_i;
   private NoiseGeneratorOctaves field_73178_j;
   private NoiseGeneratorOctaves field_73179_k;
   private NoiseGeneratorOctaves field_73176_l;
   private NoiseGeneratorOctaves field_73177_m;
   private NoiseGeneratorOctaves field_73174_n;
   public NoiseGeneratorOctaves field_73173_a;
   public NoiseGeneratorOctaves field_73171_b;
   private World field_73175_o;
   private double[] field_73186_p;
   public MapGenNetherBridge field_73172_c = new MapGenNetherBridge();
   private double[] field_73185_q = new double[256];
   private double[] field_73184_r = new double[256];
   private double[] field_73183_s = new double[256];
   private MapGenBase field_73182_t = new MapGenCavesHell();
   double[] field_73169_d;
   double[] field_73170_e;
   double[] field_73167_f;
   double[] field_73168_g;
   double[] field_73180_h;

   public ChunkProviderHell(World var1, long var2) {
      super();
      this.field_73175_o = var1;
      this.field_73181_i = new Random(var2);
      this.field_73178_j = new NoiseGeneratorOctaves(this.field_73181_i, 16);
      this.field_73179_k = new NoiseGeneratorOctaves(this.field_73181_i, 16);
      this.field_73176_l = new NoiseGeneratorOctaves(this.field_73181_i, 8);
      this.field_73177_m = new NoiseGeneratorOctaves(this.field_73181_i, 4);
      this.field_73174_n = new NoiseGeneratorOctaves(this.field_73181_i, 4);
      this.field_73173_a = new NoiseGeneratorOctaves(this.field_73181_i, 10);
      this.field_73171_b = new NoiseGeneratorOctaves(this.field_73181_i, 16);
   }

   public void func_147419_a(int var1, int var2, Block[] var3) {
      byte var4 = 4;
      byte var5 = 32;
      int var6 = var4 + 1;
      byte var7 = 17;
      int var8 = var4 + 1;
      this.field_73186_p = this.func_73164_a(this.field_73186_p, var1 * var4, 0, var2 * var4, var6, var7, var8);

      for(int var9 = 0; var9 < var4; ++var9) {
         for(int var10 = 0; var10 < var4; ++var10) {
            for(int var11 = 0; var11 < 16; ++var11) {
               double var12 = 0.125;
               double var14 = this.field_73186_p[((var9 + 0) * var8 + var10 + 0) * var7 + var11 + 0];
               double var16 = this.field_73186_p[((var9 + 0) * var8 + var10 + 1) * var7 + var11 + 0];
               double var18 = this.field_73186_p[((var9 + 1) * var8 + var10 + 0) * var7 + var11 + 0];
               double var20 = this.field_73186_p[((var9 + 1) * var8 + var10 + 1) * var7 + var11 + 0];
               double var22 = (this.field_73186_p[((var9 + 0) * var8 + var10 + 0) * var7 + var11 + 1] - var14) * var12;
               double var24 = (this.field_73186_p[((var9 + 0) * var8 + var10 + 1) * var7 + var11 + 1] - var16) * var12;
               double var26 = (this.field_73186_p[((var9 + 1) * var8 + var10 + 0) * var7 + var11 + 1] - var18) * var12;
               double var28 = (this.field_73186_p[((var9 + 1) * var8 + var10 + 1) * var7 + var11 + 1] - var20) * var12;

               for(int var30 = 0; var30 < 8; ++var30) {
                  double var31 = 0.25;
                  double var33 = var14;
                  double var35 = var16;
                  double var37 = (var18 - var14) * var31;
                  double var39 = (var20 - var16) * var31;

                  for(int var41 = 0; var41 < 4; ++var41) {
                     int var42 = var41 + var9 * 4 << 11 | 0 + var10 * 4 << 7 | var11 * 8 + var30;
                     short var43 = 128;
                     double var44 = 0.25;
                     double var46 = var33;
                     double var48 = (var35 - var33) * var44;

                     for(int var50 = 0; var50 < 4; ++var50) {
                        Block var51 = null;
                        if (var11 * 8 + var30 < var5) {
                           var51 = Blocks.field_150353_l;
                        }

                        if (var46 > 0.0) {
                           var51 = Blocks.field_150424_aL;
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

   public void func_147418_b(int var1, int var2, Block[] var3) {
      byte var4 = 64;
      double var5 = 0.03125;
      this.field_73185_q = this.field_73177_m.func_76304_a(this.field_73185_q, var1 * 16, var2 * 16, 0, 16, 16, 1, var5, var5, 1.0);
      this.field_73184_r = this.field_73177_m.func_76304_a(this.field_73184_r, var1 * 16, 109, var2 * 16, 16, 1, 16, var5, 1.0, var5);
      this.field_73183_s = this.field_73174_n.func_76304_a(this.field_73183_s, var1 * 16, var2 * 16, 0, 16, 16, 1, var5 * 2.0, var5 * 2.0, var5 * 2.0);

      for(int var7 = 0; var7 < 16; ++var7) {
         for(int var8 = 0; var8 < 16; ++var8) {
            boolean var9 = this.field_73185_q[var7 + var8 * 16] + this.field_73181_i.nextDouble() * 0.2 > 0.0;
            boolean var10 = this.field_73184_r[var7 + var8 * 16] + this.field_73181_i.nextDouble() * 0.2 > 0.0;
            int var11 = (int)(this.field_73183_s[var7 + var8 * 16] / 3.0 + 3.0 + this.field_73181_i.nextDouble() * 0.25);
            int var12 = -1;
            Block var13 = Blocks.field_150424_aL;
            Block var14 = Blocks.field_150424_aL;

            for(int var15 = 127; var15 >= 0; --var15) {
               int var16 = (var8 * 16 + var7) * 128 + var15;
               if (var15 < 127 - this.field_73181_i.nextInt(5) && var15 > 0 + this.field_73181_i.nextInt(5)) {
                  Block var17 = var3[var16];
                  if (var17 == null || var17.func_149688_o() == Material.field_151579_a) {
                     var12 = -1;
                  } else if (var17 == Blocks.field_150424_aL) {
                     if (var12 == -1) {
                        if (var11 <= 0) {
                           var13 = null;
                           var14 = Blocks.field_150424_aL;
                        } else if (var15 >= var4 - 4 && var15 <= var4 + 1) {
                           var13 = Blocks.field_150424_aL;
                           var14 = Blocks.field_150424_aL;
                           if (var10) {
                              var13 = Blocks.field_150351_n;
                              var14 = Blocks.field_150424_aL;
                           }

                           if (var9) {
                              var13 = Blocks.field_150425_aM;
                              var14 = Blocks.field_150425_aM;
                           }
                        }

                        if (var15 < var4 && (var13 == null || var13.func_149688_o() == Material.field_151579_a)) {
                           var13 = Blocks.field_150353_l;
                        }

                        var12 = var11;
                        if (var15 >= var4 - 1) {
                           var3[var16] = var13;
                        } else {
                           var3[var16] = var14;
                        }
                     } else if (var12 > 0) {
                        --var12;
                        var3[var16] = var14;
                     }
                  }
               } else {
                  var3[var16] = Blocks.field_150357_h;
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
      this.field_73181_i.setSeed((long)var1 * 341873128712L + (long)var2 * 132897987541L);
      Block[] var3 = new Block[32768];
      this.func_147419_a(var1, var2, var3);
      this.func_147418_b(var1, var2, var3);
      this.field_73182_t.func_151539_a(this, this.field_73175_o, var1, var2, var3);
      this.field_73172_c.func_151539_a(this, this.field_73175_o, var1, var2, var3);
      Chunk var4 = new Chunk(this.field_73175_o, var3, var1, var2);
      BiomeGenBase[] var5 = this.field_73175_o.func_72959_q().func_76933_b(null, var1 * 16, var2 * 16, 16, 16);
      byte[] var6 = var4.func_76605_m();

      for(int var7 = 0; var7 < var6.length; ++var7) {
         var6[var7] = (byte)var5[var7].field_76756_M;
      }

      var4.func_76613_n();
      return var4;
   }

   private double[] func_73164_a(double[] var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      if (var1 == null) {
         var1 = new double[var5 * var6 * var7];
      }

      double var8 = 684.412;
      double var10 = 2053.236;
      this.field_73168_g = this.field_73173_a.func_76304_a(this.field_73168_g, var2, var3, var4, var5, 1, var7, 1.0, 0.0, 1.0);
      this.field_73180_h = this.field_73171_b.func_76304_a(this.field_73180_h, var2, var3, var4, var5, 1, var7, 100.0, 0.0, 100.0);
      this.field_73169_d = this.field_73176_l.func_76304_a(this.field_73169_d, var2, var3, var4, var5, var6, var7, var8 / 80.0, var10 / 60.0, var8 / 80.0);
      this.field_73170_e = this.field_73178_j.func_76304_a(this.field_73170_e, var2, var3, var4, var5, var6, var7, var8, var10, var8);
      this.field_73167_f = this.field_73179_k.func_76304_a(this.field_73167_f, var2, var3, var4, var5, var6, var7, var8, var10, var8);
      int var12 = 0;
      int var13 = 0;
      double[] var14 = new double[var6];

      for(int var15 = 0; var15 < var6; ++var15) {
         var14[var15] = Math.cos((double)var15 * 3.141592653589793 * 6.0 / (double)var6) * 2.0;
         double var16 = (double)var15;
         if (var15 > var6 / 2) {
            var16 = (double)(var6 - 1 - var15);
         }

         if (var16 < 4.0) {
            var16 = 4.0 - var16;
            var14[var15] -= var16 * var16 * var16 * 10.0;
         }
      }

      for(int var36 = 0; var36 < var5; ++var36) {
         for(int var38 = 0; var38 < var7; ++var38) {
            double var17 = (this.field_73168_g[var13] + 256.0) / 512.0;
            if (var17 > 1.0) {
               var17 = 1.0;
            }

            double var19 = 0.0;
            double var21 = this.field_73180_h[var13] / 8000.0;
            if (var21 < 0.0) {
               var21 = -var21;
            }

            var21 = var21 * 3.0 - 3.0;
            if (var21 < 0.0) {
               var21 /= 2.0;
               if (var21 < -1.0) {
                  var21 = -1.0;
               }

               var21 /= 1.4;
               var21 /= 2.0;
               var17 = 0.0;
            } else {
               if (var21 > 1.0) {
                  var21 = 1.0;
               }

               var21 /= 6.0;
            }

            var17 += 0.5;
            var21 = var21 * (double)var6 / 16.0;
            ++var13;

            for(int var23 = 0; var23 < var6; ++var23) {
               double var24 = 0.0;
               double var26 = var14[var23];
               double var28 = this.field_73170_e[var12] / 512.0;
               double var30 = this.field_73167_f[var12] / 512.0;
               double var32 = (this.field_73169_d[var12] / 10.0 + 1.0) / 2.0;
               if (var32 < 0.0) {
                  var24 = var28;
               } else if (var32 > 1.0) {
                  var24 = var30;
               } else {
                  var24 = var28 + (var30 - var28) * var32;
               }

               var24 -= var26;
               if (var23 > var6 - 4) {
                  double var34 = (double)((float)(var23 - (var6 - 4)) / 3.0F);
                  var24 = var24 * (1.0 - var34) + -10.0 * var34;
               }

               if ((double)var23 < var19) {
                  double var47 = (var19 - (double)var23) / 4.0;
                  if (var47 < 0.0) {
                     var47 = 0.0;
                  }

                  if (var47 > 1.0) {
                     var47 = 1.0;
                  }

                  var24 = var24 * (1.0 - var47) + -10.0 * var47;
               }

               var1[var12] = var24;
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
      this.field_73172_c.func_75051_a(this.field_73175_o, this.field_73181_i, var2, var3);

      for(int var6 = 0; var6 < 8; ++var6) {
         int var7 = var4 + this.field_73181_i.nextInt(16) + 8;
         int var8 = this.field_73181_i.nextInt(120) + 4;
         int var9 = var5 + this.field_73181_i.nextInt(16) + 8;
         new WorldGenHellLava(Blocks.field_150356_k, false).func_76484_a(this.field_73175_o, this.field_73181_i, var7, var8, var9);
      }

      int var12 = this.field_73181_i.nextInt(this.field_73181_i.nextInt(10) + 1) + 1;

      for(int var14 = 0; var14 < var12; ++var14) {
         int var20 = var4 + this.field_73181_i.nextInt(16) + 8;
         int var27 = this.field_73181_i.nextInt(120) + 4;
         int var10 = var5 + this.field_73181_i.nextInt(16) + 8;
         new WorldGenFire().func_76484_a(this.field_73175_o, this.field_73181_i, var20, var27, var10);
      }

      var12 = this.field_73181_i.nextInt(this.field_73181_i.nextInt(10) + 1);

      for(int var15 = 0; var15 < var12; ++var15) {
         int var21 = var4 + this.field_73181_i.nextInt(16) + 8;
         int var28 = this.field_73181_i.nextInt(120) + 4;
         int var34 = var5 + this.field_73181_i.nextInt(16) + 8;
         new WorldGenGlowStone1().func_76484_a(this.field_73175_o, this.field_73181_i, var21, var28, var34);
      }

      for(int var16 = 0; var16 < 10; ++var16) {
         int var22 = var4 + this.field_73181_i.nextInt(16) + 8;
         int var29 = this.field_73181_i.nextInt(128);
         int var35 = var5 + this.field_73181_i.nextInt(16) + 8;
         new WorldGenGlowStone2().func_76484_a(this.field_73175_o, this.field_73181_i, var22, var29, var35);
      }

      if (this.field_73181_i.nextInt(1) == 0) {
         int var17 = var4 + this.field_73181_i.nextInt(16) + 8;
         int var23 = this.field_73181_i.nextInt(128);
         int var30 = var5 + this.field_73181_i.nextInt(16) + 8;
         new WorldGenFlowers(Blocks.field_150338_P).func_76484_a(this.field_73175_o, this.field_73181_i, var17, var23, var30);
      }

      if (this.field_73181_i.nextInt(1) == 0) {
         int var18 = var4 + this.field_73181_i.nextInt(16) + 8;
         int var24 = this.field_73181_i.nextInt(128);
         int var31 = var5 + this.field_73181_i.nextInt(16) + 8;
         new WorldGenFlowers(Blocks.field_150337_Q).func_76484_a(this.field_73175_o, this.field_73181_i, var18, var24, var31);
      }

      WorldGenMinable var19 = new WorldGenMinable(Blocks.field_150449_bY, 13, Blocks.field_150424_aL);

      for(int var25 = 0; var25 < 16; ++var25) {
         int var32 = var4 + this.field_73181_i.nextInt(16);
         int var36 = this.field_73181_i.nextInt(108) + 10;
         int var11 = var5 + this.field_73181_i.nextInt(16);
         var19.func_76484_a(this.field_73175_o, this.field_73181_i, var32, var36, var11);
      }

      for(int var26 = 0; var26 < 16; ++var26) {
         int var33 = var4 + this.field_73181_i.nextInt(16);
         int var37 = this.field_73181_i.nextInt(108) + 10;
         int var38 = var5 + this.field_73181_i.nextInt(16);
         new WorldGenHellLava(Blocks.field_150356_k, true).func_76484_a(this.field_73175_o, this.field_73181_i, var33, var37, var38);
      }

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
      return "HellRandomLevelSource";
   }

   @Override
   public List func_73155_a(EnumCreatureType var1, int var2, int var3, int var4) {
      if (var1 == EnumCreatureType.monster) {
         if (this.field_73172_c.func_75048_a(var2, var3, var4)) {
            return this.field_73172_c.func_75059_a();
         }

         if (this.field_73172_c.func_142038_b(var2, var3, var4) && this.field_73175_o.func_147439_a(var2, var3 - 1, var4) == Blocks.field_150385_bj) {
            return this.field_73172_c.func_75059_a();
         }
      }

      BiomeGenBase var5 = this.field_73175_o.func_72807_a(var2, var4);
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
      this.field_73172_c.func_151539_a(this, this.field_73175_o, var1, var2, null);
   }
}
