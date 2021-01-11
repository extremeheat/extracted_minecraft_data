package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;

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
   private WorldType field_177475_o;
   private final double[] field_147434_q;
   private final float[] field_147433_r;
   private ChunkProviderSettings field_177477_r;
   private Block field_177476_s;
   private double[] field_73227_s;
   private MapGenBase field_73226_t;
   private MapGenStronghold field_73225_u;
   private MapGenVillage field_73224_v;
   private MapGenMineshaft field_73223_w;
   private MapGenScatteredFeature field_73233_x;
   private MapGenBase field_73232_y;
   private StructureOceanMonument field_177474_A;
   private BiomeGenBase[] field_73231_z;
   double[] field_147427_d;
   double[] field_147428_e;
   double[] field_147425_f;
   double[] field_147426_g;

   public ChunkProviderGenerate(World var1, long var2, boolean var4, String var5) {
      super();
      this.field_177476_s = Blocks.field_150355_j;
      this.field_73227_s = new double[256];
      this.field_73226_t = new MapGenCaves();
      this.field_73225_u = new MapGenStronghold();
      this.field_73224_v = new MapGenVillage();
      this.field_73223_w = new MapGenMineshaft();
      this.field_73233_x = new MapGenScatteredFeature();
      this.field_73232_y = new MapGenRavine();
      this.field_177474_A = new StructureOceanMonument();
      this.field_73230_p = var1;
      this.field_73229_q = var4;
      this.field_177475_o = var1.func_72912_H().func_76067_t();
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

      for(int var6 = -2; var6 <= 2; ++var6) {
         for(int var7 = -2; var7 <= 2; ++var7) {
            float var8 = 10.0F / MathHelper.func_76129_c((float)(var6 * var6 + var7 * var7) + 0.2F);
            this.field_147433_r[var6 + 2 + (var7 + 2) * 5] = var8;
         }
      }

      if (var5 != null) {
         this.field_177477_r = ChunkProviderSettings.Factory.func_177865_a(var5).func_177864_b();
         this.field_177476_s = this.field_177477_r.field_177778_E ? Blocks.field_150353_l : Blocks.field_150355_j;
         var1.func_181544_b(this.field_177477_r.field_177841_q);
      }

   }

   public void func_180518_a(int var1, int var2, ChunkPrimer var3) {
      this.field_73231_z = this.field_73230_p.func_72959_q().func_76937_a(this.field_73231_z, var1 * 4 - 2, var2 * 4 - 2, 10, 10);
      this.func_147423_a(var1 * 4, 0, var2 * 4);

      for(int var4 = 0; var4 < 4; ++var4) {
         int var5 = var4 * 5;
         int var6 = (var4 + 1) * 5;

         for(int var7 = 0; var7 < 4; ++var7) {
            int var8 = (var5 + var7) * 33;
            int var9 = (var5 + var7 + 1) * 33;
            int var10 = (var6 + var7) * 33;
            int var11 = (var6 + var7 + 1) * 33;

            for(int var12 = 0; var12 < 32; ++var12) {
               double var13 = 0.125D;
               double var15 = this.field_147434_q[var8 + var12];
               double var17 = this.field_147434_q[var9 + var12];
               double var19 = this.field_147434_q[var10 + var12];
               double var21 = this.field_147434_q[var11 + var12];
               double var23 = (this.field_147434_q[var8 + var12 + 1] - var15) * var13;
               double var25 = (this.field_147434_q[var9 + var12 + 1] - var17) * var13;
               double var27 = (this.field_147434_q[var10 + var12 + 1] - var19) * var13;
               double var29 = (this.field_147434_q[var11 + var12 + 1] - var21) * var13;

               for(int var31 = 0; var31 < 8; ++var31) {
                  double var32 = 0.25D;
                  double var34 = var15;
                  double var36 = var17;
                  double var38 = (var19 - var15) * var32;
                  double var40 = (var21 - var17) * var32;

                  for(int var42 = 0; var42 < 4; ++var42) {
                     double var43 = 0.25D;
                     double var47 = (var36 - var34) * var43;
                     double var45 = var34 - var47;

                     for(int var49 = 0; var49 < 4; ++var49) {
                        if ((var45 += var47) > 0.0D) {
                           var3.func_177855_a(var4 * 4 + var42, var12 * 8 + var31, var7 * 4 + var49, Blocks.field_150348_b.func_176223_P());
                        } else if (var12 * 8 + var31 < this.field_177477_r.field_177841_q) {
                           var3.func_177855_a(var4 * 4 + var42, var12 * 8 + var31, var7 * 4 + var49, this.field_177476_s.func_176223_P());
                        }
                     }

                     var34 += var38;
                     var36 += var40;
                  }

                  var15 += var23;
                  var17 += var25;
                  var19 += var27;
                  var21 += var29;
               }
            }
         }
      }

   }

   public void func_180517_a(int var1, int var2, ChunkPrimer var3, BiomeGenBase[] var4) {
      double var5 = 0.03125D;
      this.field_73227_s = this.field_147430_m.func_151599_a(this.field_73227_s, (double)(var1 * 16), (double)(var2 * 16), 16, 16, var5 * 2.0D, var5 * 2.0D, 1.0D);

      for(int var7 = 0; var7 < 16; ++var7) {
         for(int var8 = 0; var8 < 16; ++var8) {
            BiomeGenBase var9 = var4[var8 + var7 * 16];
            var9.func_180622_a(this.field_73230_p, this.field_73220_k, var3, var1 * 16 + var7, var2 * 16 + var8, this.field_73227_s[var8 + var7 * 16]);
         }
      }

   }

   public Chunk func_73154_d(int var1, int var2) {
      this.field_73220_k.setSeed((long)var1 * 341873128712L + (long)var2 * 132897987541L);
      ChunkPrimer var3 = new ChunkPrimer();
      this.func_180518_a(var1, var2, var3);
      this.field_73231_z = this.field_73230_p.func_72959_q().func_76933_b(this.field_73231_z, var1 * 16, var2 * 16, 16, 16);
      this.func_180517_a(var1, var2, var3, this.field_73231_z);
      if (this.field_177477_r.field_177839_r) {
         this.field_73226_t.func_175792_a(this, this.field_73230_p, var1, var2, var3);
      }

      if (this.field_177477_r.field_177850_z) {
         this.field_73232_y.func_175792_a(this, this.field_73230_p, var1, var2, var3);
      }

      if (this.field_177477_r.field_177829_w && this.field_73229_q) {
         this.field_73223_w.func_175792_a(this, this.field_73230_p, var1, var2, var3);
      }

      if (this.field_177477_r.field_177831_v && this.field_73229_q) {
         this.field_73224_v.func_175792_a(this, this.field_73230_p, var1, var2, var3);
      }

      if (this.field_177477_r.field_177833_u && this.field_73229_q) {
         this.field_73225_u.func_175792_a(this, this.field_73230_p, var1, var2, var3);
      }

      if (this.field_177477_r.field_177854_x && this.field_73229_q) {
         this.field_73233_x.func_175792_a(this, this.field_73230_p, var1, var2, var3);
      }

      if (this.field_177477_r.field_177852_y && this.field_73229_q) {
         this.field_177474_A.func_175792_a(this, this.field_73230_p, var1, var2, var3);
      }

      Chunk var4 = new Chunk(this.field_73230_p, var3, var1, var2);
      byte[] var5 = var4.func_76605_m();

      for(int var6 = 0; var6 < var5.length; ++var6) {
         var5[var6] = (byte)this.field_73231_z[var6].field_76756_M;
      }

      var4.func_76603_b();
      return var4;
   }

   private void func_147423_a(int var1, int var2, int var3) {
      this.field_147426_g = this.field_73212_b.func_76305_a(this.field_147426_g, var1, var3, 5, 5, (double)this.field_177477_r.field_177808_e, (double)this.field_177477_r.field_177803_f, (double)this.field_177477_r.field_177804_g);
      float var4 = this.field_177477_r.field_177811_a;
      float var5 = this.field_177477_r.field_177809_b;
      this.field_147427_d = this.field_147429_l.func_76304_a(this.field_147427_d, var1, var2, var3, 5, 33, 5, (double)(var4 / this.field_177477_r.field_177825_h), (double)(var5 / this.field_177477_r.field_177827_i), (double)(var4 / this.field_177477_r.field_177821_j));
      this.field_147428_e = this.field_147431_j.func_76304_a(this.field_147428_e, var1, var2, var3, 5, 33, 5, (double)var4, (double)var5, (double)var4);
      this.field_147425_f = this.field_147432_k.func_76304_a(this.field_147425_f, var1, var2, var3, 5, 33, 5, (double)var4, (double)var5, (double)var4);
      boolean var37 = false;
      boolean var36 = false;
      int var6 = 0;
      int var7 = 0;

      for(int var8 = 0; var8 < 5; ++var8) {
         for(int var9 = 0; var9 < 5; ++var9) {
            float var10 = 0.0F;
            float var11 = 0.0F;
            float var12 = 0.0F;
            byte var13 = 2;
            BiomeGenBase var14 = this.field_73231_z[var8 + 2 + (var9 + 2) * 10];

            for(int var15 = -var13; var15 <= var13; ++var15) {
               for(int var16 = -var13; var16 <= var13; ++var16) {
                  BiomeGenBase var17 = this.field_73231_z[var8 + var15 + 2 + (var9 + var16 + 2) * 10];
                  float var18 = this.field_177477_r.field_177813_n + var17.field_76748_D * this.field_177477_r.field_177819_m;
                  float var19 = this.field_177477_r.field_177843_p + var17.field_76749_E * this.field_177477_r.field_177815_o;
                  if (this.field_177475_o == WorldType.field_151360_e && var18 > 0.0F) {
                     var18 = 1.0F + var18 * 2.0F;
                     var19 = 1.0F + var19 * 4.0F;
                  }

                  float var20 = this.field_147433_r[var15 + 2 + (var16 + 2) * 5] / (var18 + 2.0F);
                  if (var17.field_76748_D > var14.field_76748_D) {
                     var20 /= 2.0F;
                  }

                  var10 += var19 * var20;
                  var11 += var18 * var20;
                  var12 += var20;
               }
            }

            var10 /= var12;
            var11 /= var12;
            var10 = var10 * 0.9F + 0.1F;
            var11 = (var11 * 4.0F - 1.0F) / 8.0F;
            double var38 = this.field_147426_g[var7] / 8000.0D;
            if (var38 < 0.0D) {
               var38 = -var38 * 0.3D;
            }

            var38 = var38 * 3.0D - 2.0D;
            if (var38 < 0.0D) {
               var38 /= 2.0D;
               if (var38 < -1.0D) {
                  var38 = -1.0D;
               }

               var38 /= 1.4D;
               var38 /= 2.0D;
            } else {
               if (var38 > 1.0D) {
                  var38 = 1.0D;
               }

               var38 /= 8.0D;
            }

            ++var7;
            double var39 = (double)var11;
            double var40 = (double)var10;
            var39 += var38 * 0.2D;
            var39 = var39 * (double)this.field_177477_r.field_177823_k / 8.0D;
            double var21 = (double)this.field_177477_r.field_177823_k + var39 * 4.0D;

            for(int var23 = 0; var23 < 33; ++var23) {
               double var24 = ((double)var23 - var21) * (double)this.field_177477_r.field_177817_l * 128.0D / 256.0D / var40;
               if (var24 < 0.0D) {
                  var24 *= 4.0D;
               }

               double var26 = this.field_147428_e[var6] / (double)this.field_177477_r.field_177806_d;
               double var28 = this.field_147425_f[var6] / (double)this.field_177477_r.field_177810_c;
               double var30 = (this.field_147427_d[var6] / 10.0D + 1.0D) / 2.0D;
               double var32 = MathHelper.func_151238_b(var26, var28, var30) - var24;
               if (var23 > 29) {
                  double var34 = (double)((float)(var23 - 29) / 3.0F);
                  var32 = var32 * (1.0D - var34) + -10.0D * var34;
               }

               this.field_147434_q[var6] = var32;
               ++var6;
            }
         }
      }

   }

   public boolean func_73149_a(int var1, int var2) {
      return true;
   }

   public void func_73153_a(IChunkProvider var1, int var2, int var3) {
      BlockFalling.field_149832_M = true;
      int var4 = var2 * 16;
      int var5 = var3 * 16;
      BlockPos var6 = new BlockPos(var4, 0, var5);
      BiomeGenBase var7 = this.field_73230_p.func_180494_b(var6.func_177982_a(16, 0, 16));
      this.field_73220_k.setSeed(this.field_73230_p.func_72905_C());
      long var8 = this.field_73220_k.nextLong() / 2L * 2L + 1L;
      long var10 = this.field_73220_k.nextLong() / 2L * 2L + 1L;
      this.field_73220_k.setSeed((long)var2 * var8 + (long)var3 * var10 ^ this.field_73230_p.func_72905_C());
      boolean var12 = false;
      ChunkCoordIntPair var13 = new ChunkCoordIntPair(var2, var3);
      if (this.field_177477_r.field_177829_w && this.field_73229_q) {
         this.field_73223_w.func_175794_a(this.field_73230_p, this.field_73220_k, var13);
      }

      if (this.field_177477_r.field_177831_v && this.field_73229_q) {
         var12 = this.field_73224_v.func_175794_a(this.field_73230_p, this.field_73220_k, var13);
      }

      if (this.field_177477_r.field_177833_u && this.field_73229_q) {
         this.field_73225_u.func_175794_a(this.field_73230_p, this.field_73220_k, var13);
      }

      if (this.field_177477_r.field_177854_x && this.field_73229_q) {
         this.field_73233_x.func_175794_a(this.field_73230_p, this.field_73220_k, var13);
      }

      if (this.field_177477_r.field_177852_y && this.field_73229_q) {
         this.field_177474_A.func_175794_a(this.field_73230_p, this.field_73220_k, var13);
      }

      int var14;
      int var15;
      int var16;
      if (var7 != BiomeGenBase.field_76769_d && var7 != BiomeGenBase.field_76786_s && this.field_177477_r.field_177781_A && !var12 && this.field_73220_k.nextInt(this.field_177477_r.field_177782_B) == 0) {
         var14 = this.field_73220_k.nextInt(16) + 8;
         var15 = this.field_73220_k.nextInt(256);
         var16 = this.field_73220_k.nextInt(16) + 8;
         (new WorldGenLakes(Blocks.field_150355_j)).func_180709_b(this.field_73230_p, this.field_73220_k, var6.func_177982_a(var14, var15, var16));
      }

      if (!var12 && this.field_73220_k.nextInt(this.field_177477_r.field_177777_D / 10) == 0 && this.field_177477_r.field_177783_C) {
         var14 = this.field_73220_k.nextInt(16) + 8;
         var15 = this.field_73220_k.nextInt(this.field_73220_k.nextInt(248) + 8);
         var16 = this.field_73220_k.nextInt(16) + 8;
         if (var15 < this.field_73230_p.func_181545_F() || this.field_73220_k.nextInt(this.field_177477_r.field_177777_D / 8) == 0) {
            (new WorldGenLakes(Blocks.field_150353_l)).func_180709_b(this.field_73230_p, this.field_73220_k, var6.func_177982_a(var14, var15, var16));
         }
      }

      if (this.field_177477_r.field_177837_s) {
         for(var14 = 0; var14 < this.field_177477_r.field_177835_t; ++var14) {
            var15 = this.field_73220_k.nextInt(16) + 8;
            var16 = this.field_73220_k.nextInt(256);
            int var17 = this.field_73220_k.nextInt(16) + 8;
            (new WorldGenDungeons()).func_180709_b(this.field_73230_p, this.field_73220_k, var6.func_177982_a(var15, var16, var17));
         }
      }

      var7.func_180624_a(this.field_73230_p, this.field_73220_k, new BlockPos(var4, 0, var5));
      SpawnerAnimals.func_77191_a(this.field_73230_p, var7, var4 + 8, var5 + 8, 16, 16, this.field_73220_k);
      var6 = var6.func_177982_a(8, 0, 8);

      for(var14 = 0; var14 < 16; ++var14) {
         for(var15 = 0; var15 < 16; ++var15) {
            BlockPos var18 = this.field_73230_p.func_175725_q(var6.func_177982_a(var14, 0, var15));
            BlockPos var19 = var18.func_177977_b();
            if (this.field_73230_p.func_175675_v(var19)) {
               this.field_73230_p.func_180501_a(var19, Blocks.field_150432_aD.func_176223_P(), 2);
            }

            if (this.field_73230_p.func_175708_f(var18, true)) {
               this.field_73230_p.func_180501_a(var18, Blocks.field_150431_aC.func_176223_P(), 2);
            }
         }
      }

      BlockFalling.field_149832_M = false;
   }

   public boolean func_177460_a(IChunkProvider var1, Chunk var2, int var3, int var4) {
      boolean var5 = false;
      if (this.field_177477_r.field_177852_y && this.field_73229_q && var2.func_177416_w() < 3600L) {
         var5 |= this.field_177474_A.func_175794_a(this.field_73230_p, this.field_73220_k, new ChunkCoordIntPair(var3, var4));
      }

      return var5;
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
      return "RandomLevelSource";
   }

   public List<BiomeGenBase.SpawnListEntry> func_177458_a(EnumCreatureType var1, BlockPos var2) {
      BiomeGenBase var3 = this.field_73230_p.func_180494_b(var2);
      if (this.field_73229_q) {
         if (var1 == EnumCreatureType.MONSTER && this.field_73233_x.func_175798_a(var2)) {
            return this.field_73233_x.func_82667_a();
         }

         if (var1 == EnumCreatureType.MONSTER && this.field_177477_r.field_177852_y && this.field_177474_A.func_175796_a(this.field_73230_p, var2)) {
            return this.field_177474_A.func_175799_b();
         }
      }

      return var3.func_76747_a(var1);
   }

   public BlockPos func_180513_a(World var1, String var2, BlockPos var3) {
      return "Stronghold".equals(var2) && this.field_73225_u != null ? this.field_73225_u.func_180706_b(var1, var3) : null;
   }

   public int func_73152_e() {
      return 0;
   }

   public void func_180514_a(Chunk var1, int var2, int var3) {
      if (this.field_177477_r.field_177829_w && this.field_73229_q) {
         this.field_73223_w.func_175792_a(this, this.field_73230_p, var2, var3, (ChunkPrimer)null);
      }

      if (this.field_177477_r.field_177831_v && this.field_73229_q) {
         this.field_73224_v.func_175792_a(this, this.field_73230_p, var2, var3, (ChunkPrimer)null);
      }

      if (this.field_177477_r.field_177833_u && this.field_73229_q) {
         this.field_73225_u.func_175792_a(this, this.field_73230_p, var2, var3, (ChunkPrimer)null);
      }

      if (this.field_177477_r.field_177854_x && this.field_73229_q) {
         this.field_73233_x.func_175792_a(this, this.field_73230_p, var2, var3, (ChunkPrimer)null);
      }

      if (this.field_177477_r.field_177852_y && this.field_73229_q) {
         this.field_177474_A.func_175792_a(this, this.field_73230_p, var2, var3, (ChunkPrimer)null);
      }

   }

   public Chunk func_177459_a(BlockPos var1) {
      return this.func_73154_d(var1.func_177958_n() >> 4, var1.func_177952_p() >> 4);
   }
}
