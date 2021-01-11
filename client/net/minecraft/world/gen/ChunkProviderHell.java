package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenFire;
import net.minecraft.world.gen.feature.WorldGenGlowStone1;
import net.minecraft.world.gen.feature.WorldGenGlowStone2;
import net.minecraft.world.gen.feature.WorldGenHellLava;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.MapGenNetherBridge;

public class ChunkProviderHell implements IChunkProvider {
   private final World field_73175_o;
   private final boolean field_177466_i;
   private final Random field_73181_i;
   private double[] field_73185_q = new double[256];
   private double[] field_73184_r = new double[256];
   private double[] field_73183_s = new double[256];
   private double[] field_73186_p;
   private final NoiseGeneratorOctaves field_73178_j;
   private final NoiseGeneratorOctaves field_73179_k;
   private final NoiseGeneratorOctaves field_73176_l;
   private final NoiseGeneratorOctaves field_73177_m;
   private final NoiseGeneratorOctaves field_73174_n;
   public final NoiseGeneratorOctaves field_73173_a;
   public final NoiseGeneratorOctaves field_73171_b;
   private final WorldGenFire field_177470_t = new WorldGenFire();
   private final WorldGenGlowStone1 field_177469_u = new WorldGenGlowStone1();
   private final WorldGenGlowStone2 field_177468_v = new WorldGenGlowStone2();
   private final WorldGenerator field_177467_w;
   private final WorldGenHellLava field_177473_x;
   private final WorldGenHellLava field_177472_y;
   private final GeneratorBushFeature field_177471_z;
   private final GeneratorBushFeature field_177465_A;
   private final MapGenNetherBridge field_73172_c;
   private final MapGenBase field_73182_t;
   double[] field_73169_d;
   double[] field_73170_e;
   double[] field_73167_f;
   double[] field_73168_g;
   double[] field_73180_h;

   public ChunkProviderHell(World var1, boolean var2, long var3) {
      super();
      this.field_177467_w = new WorldGenMinable(Blocks.field_150449_bY.func_176223_P(), 14, BlockHelper.func_177642_a(Blocks.field_150424_aL));
      this.field_177473_x = new WorldGenHellLava(Blocks.field_150356_k, true);
      this.field_177472_y = new WorldGenHellLava(Blocks.field_150356_k, false);
      this.field_177471_z = new GeneratorBushFeature(Blocks.field_150338_P);
      this.field_177465_A = new GeneratorBushFeature(Blocks.field_150337_Q);
      this.field_73172_c = new MapGenNetherBridge();
      this.field_73182_t = new MapGenCavesHell();
      this.field_73175_o = var1;
      this.field_177466_i = var2;
      this.field_73181_i = new Random(var3);
      this.field_73178_j = new NoiseGeneratorOctaves(this.field_73181_i, 16);
      this.field_73179_k = new NoiseGeneratorOctaves(this.field_73181_i, 16);
      this.field_73176_l = new NoiseGeneratorOctaves(this.field_73181_i, 8);
      this.field_73177_m = new NoiseGeneratorOctaves(this.field_73181_i, 4);
      this.field_73174_n = new NoiseGeneratorOctaves(this.field_73181_i, 4);
      this.field_73173_a = new NoiseGeneratorOctaves(this.field_73181_i, 10);
      this.field_73171_b = new NoiseGeneratorOctaves(this.field_73181_i, 16);
      var1.func_181544_b(63);
   }

   public void func_180515_a(int var1, int var2, ChunkPrimer var3) {
      byte var4 = 4;
      int var5 = this.field_73175_o.func_181545_F() / 2 + 1;
      int var6 = var4 + 1;
      byte var7 = 17;
      int var8 = var4 + 1;
      this.field_73186_p = this.func_73164_a(this.field_73186_p, var1 * var4, 0, var2 * var4, var6, var7, var8);

      for(int var9 = 0; var9 < var4; ++var9) {
         for(int var10 = 0; var10 < var4; ++var10) {
            for(int var11 = 0; var11 < 16; ++var11) {
               double var12 = 0.125D;
               double var14 = this.field_73186_p[((var9 + 0) * var8 + var10 + 0) * var7 + var11 + 0];
               double var16 = this.field_73186_p[((var9 + 0) * var8 + var10 + 1) * var7 + var11 + 0];
               double var18 = this.field_73186_p[((var9 + 1) * var8 + var10 + 0) * var7 + var11 + 0];
               double var20 = this.field_73186_p[((var9 + 1) * var8 + var10 + 1) * var7 + var11 + 0];
               double var22 = (this.field_73186_p[((var9 + 0) * var8 + var10 + 0) * var7 + var11 + 1] - var14) * var12;
               double var24 = (this.field_73186_p[((var9 + 0) * var8 + var10 + 1) * var7 + var11 + 1] - var16) * var12;
               double var26 = (this.field_73186_p[((var9 + 1) * var8 + var10 + 0) * var7 + var11 + 1] - var18) * var12;
               double var28 = (this.field_73186_p[((var9 + 1) * var8 + var10 + 1) * var7 + var11 + 1] - var20) * var12;

               for(int var30 = 0; var30 < 8; ++var30) {
                  double var31 = 0.25D;
                  double var33 = var14;
                  double var35 = var16;
                  double var37 = (var18 - var14) * var31;
                  double var39 = (var20 - var16) * var31;

                  for(int var41 = 0; var41 < 4; ++var41) {
                     double var42 = 0.25D;
                     double var44 = var33;
                     double var46 = (var35 - var33) * var42;

                     for(int var48 = 0; var48 < 4; ++var48) {
                        IBlockState var49 = null;
                        if (var11 * 8 + var30 < var5) {
                           var49 = Blocks.field_150353_l.func_176223_P();
                        }

                        if (var44 > 0.0D) {
                           var49 = Blocks.field_150424_aL.func_176223_P();
                        }

                        int var50 = var41 + var9 * 4;
                        int var51 = var30 + var11 * 8;
                        int var52 = var48 + var10 * 4;
                        var3.func_177855_a(var50, var51, var52, var49);
                        var44 += var46;
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

   public void func_180516_b(int var1, int var2, ChunkPrimer var3) {
      int var4 = this.field_73175_o.func_181545_F() + 1;
      double var5 = 0.03125D;
      this.field_73185_q = this.field_73177_m.func_76304_a(this.field_73185_q, var1 * 16, var2 * 16, 0, 16, 16, 1, var5, var5, 1.0D);
      this.field_73184_r = this.field_73177_m.func_76304_a(this.field_73184_r, var1 * 16, 109, var2 * 16, 16, 1, 16, var5, 1.0D, var5);
      this.field_73183_s = this.field_73174_n.func_76304_a(this.field_73183_s, var1 * 16, var2 * 16, 0, 16, 16, 1, var5 * 2.0D, var5 * 2.0D, var5 * 2.0D);

      for(int var7 = 0; var7 < 16; ++var7) {
         for(int var8 = 0; var8 < 16; ++var8) {
            boolean var9 = this.field_73185_q[var7 + var8 * 16] + this.field_73181_i.nextDouble() * 0.2D > 0.0D;
            boolean var10 = this.field_73184_r[var7 + var8 * 16] + this.field_73181_i.nextDouble() * 0.2D > 0.0D;
            int var11 = (int)(this.field_73183_s[var7 + var8 * 16] / 3.0D + 3.0D + this.field_73181_i.nextDouble() * 0.25D);
            int var12 = -1;
            IBlockState var13 = Blocks.field_150424_aL.func_176223_P();
            IBlockState var14 = Blocks.field_150424_aL.func_176223_P();

            for(int var15 = 127; var15 >= 0; --var15) {
               if (var15 < 127 - this.field_73181_i.nextInt(5) && var15 > this.field_73181_i.nextInt(5)) {
                  IBlockState var16 = var3.func_177856_a(var8, var15, var7);
                  if (var16.func_177230_c() != null && var16.func_177230_c().func_149688_o() != Material.field_151579_a) {
                     if (var16.func_177230_c() == Blocks.field_150424_aL) {
                        if (var12 == -1) {
                           if (var11 <= 0) {
                              var13 = null;
                              var14 = Blocks.field_150424_aL.func_176223_P();
                           } else if (var15 >= var4 - 4 && var15 <= var4 + 1) {
                              var13 = Blocks.field_150424_aL.func_176223_P();
                              var14 = Blocks.field_150424_aL.func_176223_P();
                              if (var10) {
                                 var13 = Blocks.field_150351_n.func_176223_P();
                                 var14 = Blocks.field_150424_aL.func_176223_P();
                              }

                              if (var9) {
                                 var13 = Blocks.field_150425_aM.func_176223_P();
                                 var14 = Blocks.field_150425_aM.func_176223_P();
                              }
                           }

                           if (var15 < var4 && (var13 == null || var13.func_177230_c().func_149688_o() == Material.field_151579_a)) {
                              var13 = Blocks.field_150353_l.func_176223_P();
                           }

                           var12 = var11;
                           if (var15 >= var4 - 1) {
                              var3.func_177855_a(var8, var15, var7, var13);
                           } else {
                              var3.func_177855_a(var8, var15, var7, var14);
                           }
                        } else if (var12 > 0) {
                           --var12;
                           var3.func_177855_a(var8, var15, var7, var14);
                        }
                     }
                  } else {
                     var12 = -1;
                  }
               } else {
                  var3.func_177855_a(var8, var15, var7, Blocks.field_150357_h.func_176223_P());
               }
            }
         }
      }

   }

   public Chunk func_73154_d(int var1, int var2) {
      this.field_73181_i.setSeed((long)var1 * 341873128712L + (long)var2 * 132897987541L);
      ChunkPrimer var3 = new ChunkPrimer();
      this.func_180515_a(var1, var2, var3);
      this.func_180516_b(var1, var2, var3);
      this.field_73182_t.func_175792_a(this, this.field_73175_o, var1, var2, var3);
      if (this.field_177466_i) {
         this.field_73172_c.func_175792_a(this, this.field_73175_o, var1, var2, var3);
      }

      Chunk var4 = new Chunk(this.field_73175_o, var3, var1, var2);
      BiomeGenBase[] var5 = this.field_73175_o.func_72959_q().func_76933_b((BiomeGenBase[])null, var1 * 16, var2 * 16, 16, 16);
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

      double var8 = 684.412D;
      double var10 = 2053.236D;
      this.field_73168_g = this.field_73173_a.func_76304_a(this.field_73168_g, var2, var3, var4, var5, 1, var7, 1.0D, 0.0D, 1.0D);
      this.field_73180_h = this.field_73171_b.func_76304_a(this.field_73180_h, var2, var3, var4, var5, 1, var7, 100.0D, 0.0D, 100.0D);
      this.field_73169_d = this.field_73176_l.func_76304_a(this.field_73169_d, var2, var3, var4, var5, var6, var7, var8 / 80.0D, var10 / 60.0D, var8 / 80.0D);
      this.field_73170_e = this.field_73178_j.func_76304_a(this.field_73170_e, var2, var3, var4, var5, var6, var7, var8, var10, var8);
      this.field_73167_f = this.field_73179_k.func_76304_a(this.field_73167_f, var2, var3, var4, var5, var6, var7, var8, var10, var8);
      int var12 = 0;
      double[] var13 = new double[var6];

      int var14;
      for(var14 = 0; var14 < var6; ++var14) {
         var13[var14] = Math.cos((double)var14 * 3.141592653589793D * 6.0D / (double)var6) * 2.0D;
         double var15 = (double)var14;
         if (var14 > var6 / 2) {
            var15 = (double)(var6 - 1 - var14);
         }

         if (var15 < 4.0D) {
            var15 = 4.0D - var15;
            var13[var14] -= var15 * var15 * var15 * 10.0D;
         }
      }

      for(var14 = 0; var14 < var5; ++var14) {
         for(int var31 = 0; var31 < var7; ++var31) {
            double var16 = 0.0D;

            for(int var18 = 0; var18 < var6; ++var18) {
               double var19 = 0.0D;
               double var21 = var13[var18];
               double var23 = this.field_73170_e[var12] / 512.0D;
               double var25 = this.field_73167_f[var12] / 512.0D;
               double var27 = (this.field_73169_d[var12] / 10.0D + 1.0D) / 2.0D;
               if (var27 < 0.0D) {
                  var19 = var23;
               } else if (var27 > 1.0D) {
                  var19 = var25;
               } else {
                  var19 = var23 + (var25 - var23) * var27;
               }

               var19 -= var21;
               double var29;
               if (var18 > var6 - 4) {
                  var29 = (double)((float)(var18 - (var6 - 4)) / 3.0F);
                  var19 = var19 * (1.0D - var29) + -10.0D * var29;
               }

               if ((double)var18 < var16) {
                  var29 = (var16 - (double)var18) / 4.0D;
                  var29 = MathHelper.func_151237_a(var29, 0.0D, 1.0D);
                  var19 = var19 * (1.0D - var29) + -10.0D * var29;
               }

               var1[var12] = var19;
               ++var12;
            }
         }
      }

      return var1;
   }

   public boolean func_73149_a(int var1, int var2) {
      return true;
   }

   public void func_73153_a(IChunkProvider var1, int var2, int var3) {
      BlockFalling.field_149832_M = true;
      BlockPos var4 = new BlockPos(var2 * 16, 0, var3 * 16);
      ChunkCoordIntPair var5 = new ChunkCoordIntPair(var2, var3);
      this.field_73172_c.func_175794_a(this.field_73175_o, this.field_73181_i, var5);

      int var6;
      for(var6 = 0; var6 < 8; ++var6) {
         this.field_177472_y.func_180709_b(this.field_73175_o, this.field_73181_i, var4.func_177982_a(this.field_73181_i.nextInt(16) + 8, this.field_73181_i.nextInt(120) + 4, this.field_73181_i.nextInt(16) + 8));
      }

      for(var6 = 0; var6 < this.field_73181_i.nextInt(this.field_73181_i.nextInt(10) + 1) + 1; ++var6) {
         this.field_177470_t.func_180709_b(this.field_73175_o, this.field_73181_i, var4.func_177982_a(this.field_73181_i.nextInt(16) + 8, this.field_73181_i.nextInt(120) + 4, this.field_73181_i.nextInt(16) + 8));
      }

      for(var6 = 0; var6 < this.field_73181_i.nextInt(this.field_73181_i.nextInt(10) + 1); ++var6) {
         this.field_177469_u.func_180709_b(this.field_73175_o, this.field_73181_i, var4.func_177982_a(this.field_73181_i.nextInt(16) + 8, this.field_73181_i.nextInt(120) + 4, this.field_73181_i.nextInt(16) + 8));
      }

      for(var6 = 0; var6 < 10; ++var6) {
         this.field_177468_v.func_180709_b(this.field_73175_o, this.field_73181_i, var4.func_177982_a(this.field_73181_i.nextInt(16) + 8, this.field_73181_i.nextInt(128), this.field_73181_i.nextInt(16) + 8));
      }

      if (this.field_73181_i.nextBoolean()) {
         this.field_177471_z.func_180709_b(this.field_73175_o, this.field_73181_i, var4.func_177982_a(this.field_73181_i.nextInt(16) + 8, this.field_73181_i.nextInt(128), this.field_73181_i.nextInt(16) + 8));
      }

      if (this.field_73181_i.nextBoolean()) {
         this.field_177465_A.func_180709_b(this.field_73175_o, this.field_73181_i, var4.func_177982_a(this.field_73181_i.nextInt(16) + 8, this.field_73181_i.nextInt(128), this.field_73181_i.nextInt(16) + 8));
      }

      for(var6 = 0; var6 < 16; ++var6) {
         this.field_177467_w.func_180709_b(this.field_73175_o, this.field_73181_i, var4.func_177982_a(this.field_73181_i.nextInt(16), this.field_73181_i.nextInt(108) + 10, this.field_73181_i.nextInt(16)));
      }

      for(var6 = 0; var6 < 16; ++var6) {
         this.field_177473_x.func_180709_b(this.field_73175_o, this.field_73181_i, var4.func_177982_a(this.field_73181_i.nextInt(16), this.field_73181_i.nextInt(108) + 10, this.field_73181_i.nextInt(16)));
      }

      BlockFalling.field_149832_M = false;
   }

   public boolean func_177460_a(IChunkProvider var1, Chunk var2, int var3, int var4) {
      return false;
   }

   public boolean func_73151_a(boolean var1, IProgressUpdate var2) {
      return true;
   }

   public void func_104112_b() {
   }

   public boolean func_73156_b() {
      return false;
   }

   public boolean func_73157_c() {
      return true;
   }

   public String func_73148_d() {
      return "HellRandomLevelSource";
   }

   public List<BiomeGenBase.SpawnListEntry> func_177458_a(EnumCreatureType var1, BlockPos var2) {
      if (var1 == EnumCreatureType.MONSTER) {
         if (this.field_73172_c.func_175795_b(var2)) {
            return this.field_73172_c.func_75059_a();
         }

         if (this.field_73172_c.func_175796_a(this.field_73175_o, var2) && this.field_73175_o.func_180495_p(var2.func_177977_b()).func_177230_c() == Blocks.field_150385_bj) {
            return this.field_73172_c.func_75059_a();
         }
      }

      BiomeGenBase var3 = this.field_73175_o.func_180494_b(var2);
      return var3.func_76747_a(var1);
   }

   public BlockPos func_180513_a(World var1, String var2, BlockPos var3) {
      return null;
   }

   public int func_73152_e() {
      return 0;
   }

   public void func_180514_a(Chunk var1, int var2, int var3) {
      this.field_73172_c.func_175792_a(this, this.field_73175_o, var2, var3, (ChunkPrimer)null);
   }

   public Chunk func_177459_a(BlockPos var1) {
      return this.func_73154_d(var1.func_177958_n() >> 4, var1.func_177952_p() >> 4);
   }
}
