package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;

public class ChunkProviderGenerate implements IChunkProvider {
   private Random field_73220_k;
   private NoiseGeneratorOctaves field_147431_j;
   private NoiseGeneratorOctaves field_147432_k;
   private NoiseGeneratorOctaves field_147429_l;
   private NoiseGeneratorPerlin field_147430_m;
   public NoiseGeneratorOctaves field_73214_a;
   public NoiseGeneratorOctaves field_73212_b;
   public NoiseGeneratorOctaves field_73213_c;
   private World field_73230_p;
   private final boolean field_73229_q;
   private WorldType field_147435_p;
   private final double[] field_147434_q;
   private final float[] field_147433_r;
   private double[] field_73227_s = new double[256];
   private MapGenBase field_73226_t = new MapGenCaves();
   private MapGenStronghold field_73225_u = new MapGenStronghold();
   private MapGenVillage field_73224_v = new MapGenVillage();
   private MapGenMineshaft field_73223_w = new MapGenMineshaft();
   private MapGenScatteredFeature field_73233_x = new MapGenScatteredFeature();
   private MapGenBase field_73232_y = new MapGenRavine();
   private BiomeGenBase[] field_73231_z;
   double[] field_147427_d;
   double[] field_147428_e;
   double[] field_147425_f;
   double[] field_147426_g;
   int[][] field_73219_j = new int[32][32];

   public ChunkProviderGenerate(World var1, long var2, boolean var4) {
      super();
      this.field_73230_p = var1;
      this.field_73229_q = var4;
      this.field_147435_p = var1.func_72912_H().func_76067_t();
      this.field_73220_k = new Random(var2);
      this.field_147431_j = new NoiseGeneratorOctaves(this.field_73220_k, 16);
      this.field_147432_k = new NoiseGeneratorOctaves(this.field_73220_k, 16);
      this.field_147429_l = new NoiseGeneratorOctaves(this.field_73220_k, 8);
      this.field_147430_m = new NoiseGeneratorPerlin(this.field_73220_k, 4);
      this.field_73214_a = new NoiseGeneratorOctaves(this.field_73220_k, 10);
      this.field_73212_b = new NoiseGeneratorOctaves(this.field_73220_k, 16);
      this.field_73213_c = new NoiseGeneratorOctaves(this.field_73220_k, 8);
      this.field_147434_q = new double[825];
      this.field_147433_r = new float[25];

      for(int var5 = -2; var5 <= 2; ++var5) {
         for(int var6 = -2; var6 <= 2; ++var6) {
            float var7 = 10.0F / MathHelper.func_76129_c((float)(var5 * var5 + var6 * var6) + 0.2F);
            this.field_147433_r[var5 + 2 + (var6 + 2) * 5] = var7;
         }
      }
   }

   public void func_147424_a(int var1, int var2, Block[] var3) {
      byte var4 = 63;
      this.field_73231_z = this.field_73230_p.func_72959_q().func_76937_a(this.field_73231_z, var1 * 4 - 2, var2 * 4 - 2, 10, 10);
      this.func_147423_a(var1 * 4, 0, var2 * 4);

      for(int var5 = 0; var5 < 4; ++var5) {
         int var6 = var5 * 5;
         int var7 = (var5 + 1) * 5;

         for(int var8 = 0; var8 < 4; ++var8) {
            int var9 = (var6 + var8) * 33;
            int var10 = (var6 + var8 + 1) * 33;
            int var11 = (var7 + var8) * 33;
            int var12 = (var7 + var8 + 1) * 33;

            for(int var13 = 0; var13 < 32; ++var13) {
               double var14 = 0.125;
               double var16 = this.field_147434_q[var9 + var13];
               double var18 = this.field_147434_q[var10 + var13];
               double var20 = this.field_147434_q[var11 + var13];
               double var22 = this.field_147434_q[var12 + var13];
               double var24 = (this.field_147434_q[var9 + var13 + 1] - var16) * var14;
               double var26 = (this.field_147434_q[var10 + var13 + 1] - var18) * var14;
               double var28 = (this.field_147434_q[var11 + var13 + 1] - var20) * var14;
               double var30 = (this.field_147434_q[var12 + var13 + 1] - var22) * var14;

               for(int var32 = 0; var32 < 8; ++var32) {
                  double var33 = 0.25;
                  double var35 = var16;
                  double var37 = var18;
                  double var39 = (var20 - var16) * var33;
                  double var41 = (var22 - var18) * var33;

                  for(int var43 = 0; var43 < 4; ++var43) {
                     int var44 = var43 + var5 * 4 << 12 | 0 + var8 * 4 << 8 | var13 * 8 + var32;
                     short var45 = 256;
                     var44 -= var45;
                     double var46 = 0.25;
                     double var50 = (var37 - var35) * var46;
                     double var48 = var35 - var50;

                     for(int var52 = 0; var52 < 4; ++var52) {
                        if ((var48 += var50) > 0.0) {
                           var3[var44 += var45] = Blocks.field_150348_b;
                        } else if (var13 * 8 + var32 < var4) {
                           var3[var44 += var45] = Blocks.field_150355_j;
                        } else {
                           var3[var44 += var45] = null;
                        }
                     }

                     var35 += var39;
                     var37 += var41;
                  }

                  var16 += var24;
                  var18 += var26;
                  var20 += var28;
                  var22 += var30;
               }
            }
         }
      }
   }

   public void func_147422_a(int var1, int var2, Block[] var3, byte[] var4, BiomeGenBase[] var5) {
      double var6 = 0.03125;
      this.field_73227_s = this.field_147430_m
         .func_151599_a(this.field_73227_s, (double)(var1 * 16), (double)(var2 * 16), 16, 16, var6 * 2.0, var6 * 2.0, 1.0);

      for(int var8 = 0; var8 < 16; ++var8) {
         for(int var9 = 0; var9 < 16; ++var9) {
            BiomeGenBase var10 = var5[var9 + var8 * 16];
            var10.func_150573_a(this.field_73230_p, this.field_73220_k, var3, var4, var1 * 16 + var8, var2 * 16 + var9, this.field_73227_s[var9 + var8 * 16]);
         }
      }
   }

   @Override
   public Chunk func_73158_c(int var1, int var2) {
      return this.func_73154_d(var1, var2);
   }

   @Override
   public Chunk func_73154_d(int var1, int var2) {
      this.field_73220_k.setSeed((long)var1 * 341873128712L + (long)var2 * 132897987541L);
      Block[] var3 = new Block[65536];
      byte[] var4 = new byte[65536];
      this.func_147424_a(var1, var2, var3);
      this.field_73231_z = this.field_73230_p.func_72959_q().func_76933_b(this.field_73231_z, var1 * 16, var2 * 16, 16, 16);
      this.func_147422_a(var1, var2, var3, var4, this.field_73231_z);
      this.field_73226_t.func_151539_a(this, this.field_73230_p, var1, var2, var3);
      this.field_73232_y.func_151539_a(this, this.field_73230_p, var1, var2, var3);
      if (this.field_73229_q) {
         this.field_73223_w.func_151539_a(this, this.field_73230_p, var1, var2, var3);
         this.field_73224_v.func_151539_a(this, this.field_73230_p, var1, var2, var3);
         this.field_73225_u.func_151539_a(this, this.field_73230_p, var1, var2, var3);
         this.field_73233_x.func_151539_a(this, this.field_73230_p, var1, var2, var3);
      }

      Chunk var5 = new Chunk(this.field_73230_p, var3, var4, var1, var2);
      byte[] var6 = var5.func_76605_m();

      for(int var7 = 0; var7 < var6.length; ++var7) {
         var6[var7] = (byte)this.field_73231_z[var7].field_76756_M;
      }

      var5.func_76603_b();
      return var5;
   }

   private void func_147423_a(int var1, int var2, int var3) {
      double var4 = 684.412;
      double var6 = 684.412;
      double var8 = 512.0;
      double var10 = 512.0;
      this.field_147426_g = this.field_73212_b.func_76305_a(this.field_147426_g, var1, var3, 5, 5, 200.0, 200.0, 0.5);
      this.field_147427_d = this.field_147429_l
         .func_76304_a(this.field_147427_d, var1, var2, var3, 5, 33, 5, 8.555150000000001, 4.277575000000001, 8.555150000000001);
      this.field_147428_e = this.field_147431_j.func_76304_a(this.field_147428_e, var1, var2, var3, 5, 33, 5, 684.412, 684.412, 684.412);
      this.field_147425_f = this.field_147432_k.func_76304_a(this.field_147425_f, var1, var2, var3, 5, 33, 5, 684.412, 684.412, 684.412);
      boolean var45 = false;
      boolean var44 = false;
      int var12 = 0;
      int var13 = 0;
      double var14 = 8.5;

      for(int var16 = 0; var16 < 5; ++var16) {
         for(int var17 = 0; var17 < 5; ++var17) {
            float var18 = 0.0F;
            float var19 = 0.0F;
            float var20 = 0.0F;
            byte var21 = 2;
            BiomeGenBase var22 = this.field_73231_z[var16 + 2 + (var17 + 2) * 10];

            for(int var23 = -var21; var23 <= var21; ++var23) {
               for(int var24 = -var21; var24 <= var21; ++var24) {
                  BiomeGenBase var25 = this.field_73231_z[var16 + var23 + 2 + (var17 + var24 + 2) * 10];
                  float var26 = var25.field_76748_D;
                  float var27 = var25.field_76749_E;
                  if (this.field_147435_p == WorldType.field_151360_e && var26 > 0.0F) {
                     var26 = 1.0F + var26 * 2.0F;
                     var27 = 1.0F + var27 * 4.0F;
                  }

                  float var28 = this.field_147433_r[var23 + 2 + (var24 + 2) * 5] / (var26 + 2.0F);
                  if (var25.field_76748_D > var22.field_76748_D) {
                     var28 /= 2.0F;
                  }

                  var18 += var27 * var28;
                  var19 += var26 * var28;
                  var20 += var28;
               }
            }

            var18 /= var20;
            var19 /= var20;
            var18 = var18 * 0.9F + 0.1F;
            var19 = (var19 * 4.0F - 1.0F) / 8.0F;
            double var50 = this.field_147426_g[var13] / 8000.0;
            if (var50 < 0.0) {
               var50 = -var50 * 0.3;
            }

            var50 = var50 * 3.0 - 2.0;
            if (var50 < 0.0) {
               var50 /= 2.0;
               if (var50 < -1.0) {
                  var50 = -1.0;
               }

               var50 /= 1.4;
               var50 /= 2.0;
            } else {
               if (var50 > 1.0) {
                  var50 = 1.0;
               }

               var50 /= 8.0;
            }

            ++var13;
            double var55 = (double)var19;
            double var58 = (double)var18;
            var55 += var50 * 0.2;
            var55 = var55 * 8.5 / 8.0;
            double var29 = 8.5 + var55 * 4.0;

            for(int var31 = 0; var31 < 33; ++var31) {
               double var32 = ((double)var31 - var29) * 12.0 * 128.0 / 256.0 / var58;
               if (var32 < 0.0) {
                  var32 *= 4.0;
               }

               double var34 = this.field_147428_e[var12] / 512.0;
               double var36 = this.field_147425_f[var12] / 512.0;
               double var38 = (this.field_147427_d[var12] / 10.0 + 1.0) / 2.0;
               double var40 = MathHelper.func_151238_b(var34, var36, var38) - var32;
               if (var31 > 29) {
                  double var42 = (double)((float)(var31 - 29) / 3.0F);
                  var40 = var40 * (1.0 - var42) + -10.0 * var42;
               }

               this.field_147434_q[var12] = var40;
               ++var12;
            }
         }
      }
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
      BiomeGenBase var6 = this.field_73230_p.func_72807_a(var4 + 16, var5 + 16);
      this.field_73220_k.setSeed(this.field_73230_p.func_72905_C());
      long var7 = this.field_73220_k.nextLong() / 2L * 2L + 1L;
      long var9 = this.field_73220_k.nextLong() / 2L * 2L + 1L;
      this.field_73220_k.setSeed((long)var2 * var7 + (long)var3 * var9 ^ this.field_73230_p.func_72905_C());
      boolean var11 = false;
      if (this.field_73229_q) {
         this.field_73223_w.func_75051_a(this.field_73230_p, this.field_73220_k, var2, var3);
         var11 = this.field_73224_v.func_75051_a(this.field_73230_p, this.field_73220_k, var2, var3);
         this.field_73225_u.func_75051_a(this.field_73230_p, this.field_73220_k, var2, var3);
         this.field_73233_x.func_75051_a(this.field_73230_p, this.field_73220_k, var2, var3);
      }

      if (var6 != BiomeGenBase.field_76769_d && var6 != BiomeGenBase.field_76786_s && !var11 && this.field_73220_k.nextInt(4) == 0) {
         int var12 = var4 + this.field_73220_k.nextInt(16) + 8;
         int var13 = this.field_73220_k.nextInt(256);
         int var14 = var5 + this.field_73220_k.nextInt(16) + 8;
         new WorldGenLakes(Blocks.field_150355_j).func_76484_a(this.field_73230_p, this.field_73220_k, var12, var13, var14);
      }

      if (!var11 && this.field_73220_k.nextInt(8) == 0) {
         int var18 = var4 + this.field_73220_k.nextInt(16) + 8;
         int var21 = this.field_73220_k.nextInt(this.field_73220_k.nextInt(248) + 8);
         int var24 = var5 + this.field_73220_k.nextInt(16) + 8;
         if (var21 < 63 || this.field_73220_k.nextInt(10) == 0) {
            new WorldGenLakes(Blocks.field_150353_l).func_76484_a(this.field_73230_p, this.field_73220_k, var18, var21, var24);
         }
      }

      for(int var19 = 0; var19 < 8; ++var19) {
         int var22 = var4 + this.field_73220_k.nextInt(16) + 8;
         int var25 = this.field_73220_k.nextInt(256);
         int var15 = var5 + this.field_73220_k.nextInt(16) + 8;
         new WorldGenDungeons().func_76484_a(this.field_73230_p, this.field_73220_k, var22, var25, var15);
      }

      var6.func_76728_a(this.field_73230_p, this.field_73220_k, var4, var5);
      SpawnerAnimals.func_77191_a(this.field_73230_p, var6, var4 + 8, var5 + 8, 16, 16, this.field_73220_k);
      var4 += 8;
      var5 += 8;

      for(int var20 = 0; var20 < 16; ++var20) {
         for(int var23 = 0; var23 < 16; ++var23) {
            int var26 = this.field_73230_p.func_72874_g(var4 + var20, var5 + var23);
            if (this.field_73230_p.func_72884_u(var20 + var4, var26 - 1, var23 + var5)) {
               this.field_73230_p.func_147465_d(var20 + var4, var26 - 1, var23 + var5, Blocks.field_150432_aD, 0, 2);
            }

            if (this.field_73230_p.func_147478_e(var20 + var4, var26, var23 + var5, true)) {
               this.field_73230_p.func_147465_d(var20 + var4, var26, var23 + var5, Blocks.field_150431_aC, 0, 2);
            }
         }
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
      return "RandomLevelSource";
   }

   @Override
   public List func_73155_a(EnumCreatureType var1, int var2, int var3, int var4) {
      BiomeGenBase var5 = this.field_73230_p.func_72807_a(var2, var4);
      return var1 == EnumCreatureType.monster && this.field_73233_x.func_143030_a(var2, var3, var4)
         ? this.field_73233_x.func_82667_a()
         : var5.func_76747_a(var1);
   }

   @Override
   public ChunkPosition func_147416_a(World var1, String var2, int var3, int var4, int var5) {
      return "Stronghold".equals(var2) && this.field_73225_u != null ? this.field_73225_u.func_151545_a(var1, var3, var4, var5) : null;
   }

   @Override
   public int func_73152_e() {
      return 0;
   }

   @Override
   public void func_82695_e(int var1, int var2) {
      if (this.field_73229_q) {
         this.field_73223_w.func_151539_a(this, this.field_73230_p, var1, var2, null);
         this.field_73224_v.func_151539_a(this, this.field_73230_p, var1, var2, null);
         this.field_73225_u.func_151539_a(this, this.field_73230_p, var1, var2, null);
         this.field_73233_x.func_151539_a(this, this.field_73230_p, var1, var2, null);
      }
   }
}
