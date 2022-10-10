package net.minecraft.world.gen;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.PhantomSpawner;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.SwampHutStructure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkGeneratorOverworld extends AbstractChunkGenerator<OverworldGenSettings> {
   private static final Logger field_202111_e = LogManager.getLogger();
   private final NoiseGeneratorOctaves field_185991_j;
   private final NoiseGeneratorOctaves field_185992_k;
   private final NoiseGeneratorOctaves field_185993_l;
   private final NoiseGeneratorPerlin field_185994_m;
   private final OverworldGenSettings field_186000_s;
   private final NoiseGeneratorOctaves field_185983_b;
   private final NoiseGeneratorOctaves field_185984_c;
   private final WorldType field_185997_p;
   private final float[] field_185999_r;
   private final PhantomSpawner field_203230_r = new PhantomSpawner();
   private final IBlockState field_205475_r;
   private final IBlockState field_205476_s;

   public ChunkGeneratorOverworld(IWorld var1, BiomeProvider var2, OverworldGenSettings var3) {
      super(var1, var2);
      this.field_185997_p = var1.func_72912_H().func_76067_t();
      SharedSeedRandom var4 = new SharedSeedRandom(this.field_202096_b);
      this.field_185991_j = new NoiseGeneratorOctaves(var4, 16);
      this.field_185992_k = new NoiseGeneratorOctaves(var4, 16);
      this.field_185993_l = new NoiseGeneratorOctaves(var4, 8);
      this.field_185994_m = new NoiseGeneratorPerlin(var4, 4);
      this.field_185983_b = new NoiseGeneratorOctaves(var4, 10);
      this.field_185984_c = new NoiseGeneratorOctaves(var4, 16);
      this.field_185999_r = new float[25];

      for(int var5 = -2; var5 <= 2; ++var5) {
         for(int var6 = -2; var6 <= 2; ++var6) {
            float var7 = 10.0F / MathHelper.func_76129_c((float)(var5 * var5 + var6 * var6) + 0.2F);
            this.field_185999_r[var5 + 2 + (var6 + 2) * 5] = var7;
         }
      }

      this.field_186000_s = var3;
      this.field_205475_r = this.field_186000_s.func_205532_l();
      this.field_205476_s = this.field_186000_s.func_205533_m();
   }

   public void func_202088_a(IChunk var1) {
      ChunkPos var2 = var1.func_76632_l();
      int var3 = var2.field_77276_a;
      int var4 = var2.field_77275_b;
      SharedSeedRandom var5 = new SharedSeedRandom();
      var5.func_202422_a(var3, var4);
      Biome[] var6 = this.field_202097_c.func_201539_b(var3 * 16, var4 * 16, 16, 16);
      var1.func_201577_a(var6);
      this.func_185976_a(var3, var4, var1);
      var1.func_201588_a(Heightmap.Type.WORLD_SURFACE_WG, Heightmap.Type.OCEAN_FLOOR_WG);
      this.func_205471_a(var1, var6, var5, this.field_202095_a.func_181545_F());
      this.func_205472_a(var1, var5);
      var1.func_201588_a(Heightmap.Type.WORLD_SURFACE_WG, Heightmap.Type.OCEAN_FLOOR_WG);
      var1.func_201574_a(ChunkStatus.BASE);
   }

   public void func_202093_c(WorldGenRegion var1) {
      int var2 = var1.func_201679_a();
      int var3 = var1.func_201680_b();
      Biome var4 = var1.func_72964_e(var2, var3).func_201590_e()[0];
      SharedSeedRandom var5 = new SharedSeedRandom();
      var5.func_202424_a(var1.func_72905_C(), var2 << 4, var3 << 4);
      WorldEntitySpawner.func_77191_a(var1, var4, var2, var3, var5);
   }

   public void func_185976_a(int var1, int var2, IChunk var3) {
      Biome[] var4 = this.field_202097_c.func_201535_a(var3.func_76632_l().field_77276_a * 4 - 2, var3.func_76632_l().field_77275_b * 4 - 2, 10, 10);
      double[] var5 = new double[825];
      this.func_202108_a(var4, var3.func_76632_l().field_77276_a * 4, 0, var3.func_76632_l().field_77275_b * 4, var5);
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();

      for(int var7 = 0; var7 < 4; ++var7) {
         int var8 = var7 * 5;
         int var9 = (var7 + 1) * 5;

         for(int var10 = 0; var10 < 4; ++var10) {
            int var11 = (var8 + var10) * 33;
            int var12 = (var8 + var10 + 1) * 33;
            int var13 = (var9 + var10) * 33;
            int var14 = (var9 + var10 + 1) * 33;

            for(int var15 = 0; var15 < 32; ++var15) {
               double var16 = 0.125D;
               double var18 = var5[var11 + var15];
               double var20 = var5[var12 + var15];
               double var22 = var5[var13 + var15];
               double var24 = var5[var14 + var15];
               double var26 = (var5[var11 + var15 + 1] - var18) * 0.125D;
               double var28 = (var5[var12 + var15 + 1] - var20) * 0.125D;
               double var30 = (var5[var13 + var15 + 1] - var22) * 0.125D;
               double var32 = (var5[var14 + var15 + 1] - var24) * 0.125D;

               for(int var34 = 0; var34 < 8; ++var34) {
                  double var35 = 0.25D;
                  double var37 = var18;
                  double var39 = var20;
                  double var41 = (var22 - var18) * 0.25D;
                  double var43 = (var24 - var20) * 0.25D;

                  for(int var45 = 0; var45 < 4; ++var45) {
                     double var46 = 0.25D;
                     double var50 = (var39 - var37) * 0.25D;
                     double var48 = var37 - var50;

                     for(int var52 = 0; var52 < 4; ++var52) {
                        var6.func_181079_c(var7 * 4 + var45, var15 * 8 + var34, var10 * 4 + var52);
                        if ((var48 += var50) > 0.0D) {
                           var3.func_177436_a(var6, this.field_205475_r, false);
                        } else if (var15 * 8 + var34 < this.field_186000_s.func_202197_m()) {
                           var3.func_177436_a(var6, this.field_205476_s, false);
                        }
                     }

                     var37 += var41;
                     var39 += var43;
                  }

                  var18 += var26;
                  var20 += var28;
                  var22 += var30;
                  var24 += var32;
               }
            }
         }
      }

   }

   private void func_202108_a(Biome[] var1, int var2, int var3, int var4, double[] var5) {
      double[] var6 = this.field_185984_c.func_202646_a(var2, var4, 5, 5, this.field_186000_s.func_202193_n(), this.field_186000_s.func_202194_o(), this.field_186000_s.func_202189_p());
      float var7 = this.field_186000_s.func_202195_q();
      float var8 = this.field_186000_s.func_202196_r();
      double[] var9 = this.field_185993_l.func_202647_a(var2, var3, var4, 5, 33, 5, (double)(var7 / this.field_186000_s.func_202192_s()), (double)(var8 / this.field_186000_s.func_202190_t()), (double)(var7 / this.field_186000_s.func_202191_u()));
      double[] var10 = this.field_185991_j.func_202647_a(var2, var3, var4, 5, 33, 5, (double)var7, (double)var8, (double)var7);
      double[] var11 = this.field_185992_k.func_202647_a(var2, var3, var4, 5, 33, 5, (double)var7, (double)var8, (double)var7);
      int var12 = 0;
      int var13 = 0;

      for(int var14 = 0; var14 < 5; ++var14) {
         for(int var15 = 0; var15 < 5; ++var15) {
            float var16 = 0.0F;
            float var17 = 0.0F;
            float var18 = 0.0F;
            boolean var19 = true;
            Biome var20 = var1[var14 + 2 + (var15 + 2) * 10];

            for(int var21 = -2; var21 <= 2; ++var21) {
               for(int var22 = -2; var22 <= 2; ++var22) {
                  Biome var23 = var1[var14 + var21 + 2 + (var15 + var22 + 2) * 10];
                  float var24 = this.field_186000_s.func_202203_v() + var23.func_185355_j() * this.field_186000_s.func_202202_w();
                  float var25 = this.field_186000_s.func_202204_x() + var23.func_185360_m() * this.field_186000_s.func_202205_y();
                  if (this.field_185997_p == WorldType.field_151360_e && var24 > 0.0F) {
                     var24 = 1.0F + var24 * 2.0F;
                     var25 = 1.0F + var25 * 4.0F;
                  }

                  float var26 = this.field_185999_r[var21 + 2 + (var22 + 2) * 5] / (var24 + 2.0F);
                  if (var23.func_185355_j() > var20.func_185355_j()) {
                     var26 /= 2.0F;
                  }

                  var16 += var25 * var26;
                  var17 += var24 * var26;
                  var18 += var26;
               }
            }

            var16 /= var18;
            var17 /= var18;
            var16 = var16 * 0.9F + 0.1F;
            var17 = (var17 * 4.0F - 1.0F) / 8.0F;
            double var42 = var6[var13] / 8000.0D;
            if (var42 < 0.0D) {
               var42 = -var42 * 0.3D;
            }

            var42 = var42 * 3.0D - 2.0D;
            if (var42 < 0.0D) {
               var42 /= 2.0D;
               if (var42 < -1.0D) {
                  var42 = -1.0D;
               }

               var42 /= 1.4D;
               var42 /= 2.0D;
            } else {
               if (var42 > 1.0D) {
                  var42 = 1.0D;
               }

               var42 /= 8.0D;
            }

            ++var13;
            double var43 = (double)var17;
            double var44 = (double)var16;
            var43 += var42 * 0.2D;
            var43 = var43 * this.field_186000_s.func_202201_z() / 8.0D;
            double var27 = this.field_186000_s.func_202201_z() + var43 * 4.0D;

            for(int var29 = 0; var29 < 33; ++var29) {
               double var30 = ((double)var29 - var27) * this.field_186000_s.func_202206_A() * 128.0D / 256.0D / var44;
               if (var30 < 0.0D) {
                  var30 *= 4.0D;
               }

               double var32 = var10[var12] / this.field_186000_s.func_202207_B();
               double var34 = var11[var12] / this.field_186000_s.func_202208_C();
               double var36 = (var9[var12] / 10.0D + 1.0D) / 2.0D;
               double var38 = MathHelper.func_151238_b(var32, var34, var36) - var30;
               if (var29 > 29) {
                  double var40 = (double)((float)(var29 - 29) / 3.0F);
                  var38 = var38 * (1.0D - var40) - 10.0D * var40;
               }

               var5[var12] = var38;
               ++var12;
            }
         }
      }

   }

   public List<Biome.SpawnListEntry> func_177458_a(EnumCreatureType var1, BlockPos var2) {
      Biome var3 = this.field_202095_a.func_180494_b(var2);
      if (var1 == EnumCreatureType.MONSTER && ((SwampHutStructure)Feature.field_202334_l).func_202383_b(this.field_202095_a, var2)) {
         return Feature.field_202334_l.func_202279_e();
      } else {
         return var1 == EnumCreatureType.MONSTER && Feature.field_202336_n.func_175796_a(this.field_202095_a, var2) ? Feature.field_202336_n.func_202279_e() : var3.func_76747_a(var1);
      }
   }

   public int func_203222_a(World var1, boolean var2, boolean var3) {
      byte var4 = 0;
      int var5 = var4 + this.field_203230_r.func_203232_a(var1, var2, var3);
      return var5;
   }

   public OverworldGenSettings func_201496_a_() {
      return this.field_186000_s;
   }

   public double[] func_205473_a(int var1, int var2) {
      double var3 = 0.03125D;
      return this.field_185994_m.func_202644_a((double)(var1 << 4), (double)(var2 << 4), 16, 16, 0.0625D, 0.0625D, 1.0D);
   }

   public int func_205470_d() {
      return this.field_202095_a.func_181545_F() + 1;
   }

   // $FF: synthetic method
   public IChunkGenSettings func_201496_a_() {
      return this.func_201496_a_();
   }
}
