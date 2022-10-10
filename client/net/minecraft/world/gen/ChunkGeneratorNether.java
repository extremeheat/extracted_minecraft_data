package net.minecraft.world.gen;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.Feature;

public class ChunkGeneratorNether extends AbstractChunkGenerator<NetherGenSettings> {
   protected static final IBlockState field_185940_a;
   protected static final IBlockState field_185941_b;
   protected static final IBlockState field_185943_d;
   private final NoiseGeneratorOctaves field_185957_u;
   private final NoiseGeneratorOctaves field_185958_v;
   private final NoiseGeneratorOctaves field_185959_w;
   private final NoiseGeneratorOctaves field_73177_m;
   private final NoiseGeneratorOctaves field_185946_g;
   private final NoiseGeneratorOctaves field_185947_h;
   private final NetherGenSettings field_202107_q;
   private final IBlockState field_205474_p;
   private final IBlockState field_205604_n;

   public ChunkGeneratorNether(World var1, BiomeProvider var2, NetherGenSettings var3) {
      super(var1, var2);
      this.field_202107_q = var3;
      this.field_205474_p = this.field_202107_q.func_205532_l();
      this.field_205604_n = this.field_202107_q.func_205533_m();
      SharedSeedRandom var4 = new SharedSeedRandom(this.field_202096_b);
      this.field_185957_u = new NoiseGeneratorOctaves(var4, 16);
      this.field_185958_v = new NoiseGeneratorOctaves(var4, 16);
      this.field_185959_w = new NoiseGeneratorOctaves(var4, 8);
      var4.func_202423_a(1048);
      this.field_73177_m = new NoiseGeneratorOctaves(var4, 4);
      this.field_185946_g = new NoiseGeneratorOctaves(var4, 10);
      this.field_185947_h = new NoiseGeneratorOctaves(var4, 16);
      var1.func_181544_b(63);
   }

   public void func_185936_a(int var1, int var2, IChunk var3) {
      boolean var4 = true;
      int var5 = this.field_202095_a.func_181545_F() / 2 + 1;
      boolean var6 = true;
      boolean var7 = true;
      boolean var8 = true;
      double[] var9 = this.func_202104_a(var1 * 4, 0, var2 * 4, 5, 17, 5);
      BlockPos.MutableBlockPos var10 = new BlockPos.MutableBlockPos();

      for(int var11 = 0; var11 < 4; ++var11) {
         for(int var12 = 0; var12 < 4; ++var12) {
            for(int var13 = 0; var13 < 16; ++var13) {
               double var14 = 0.125D;
               double var16 = var9[((var11 + 0) * 5 + var12 + 0) * 17 + var13 + 0];
               double var18 = var9[((var11 + 0) * 5 + var12 + 1) * 17 + var13 + 0];
               double var20 = var9[((var11 + 1) * 5 + var12 + 0) * 17 + var13 + 0];
               double var22 = var9[((var11 + 1) * 5 + var12 + 1) * 17 + var13 + 0];
               double var24 = (var9[((var11 + 0) * 5 + var12 + 0) * 17 + var13 + 1] - var16) * 0.125D;
               double var26 = (var9[((var11 + 0) * 5 + var12 + 1) * 17 + var13 + 1] - var18) * 0.125D;
               double var28 = (var9[((var11 + 1) * 5 + var12 + 0) * 17 + var13 + 1] - var20) * 0.125D;
               double var30 = (var9[((var11 + 1) * 5 + var12 + 1) * 17 + var13 + 1] - var22) * 0.125D;

               for(int var32 = 0; var32 < 8; ++var32) {
                  double var33 = 0.25D;
                  double var35 = var16;
                  double var37 = var18;
                  double var39 = (var20 - var16) * 0.25D;
                  double var41 = (var22 - var18) * 0.25D;

                  for(int var43 = 0; var43 < 4; ++var43) {
                     double var44 = 0.25D;
                     double var46 = var35;
                     double var48 = (var37 - var35) * 0.25D;

                     for(int var50 = 0; var50 < 4; ++var50) {
                        IBlockState var51 = field_185940_a;
                        if (var13 * 8 + var32 < var5) {
                           var51 = this.field_205604_n;
                        }

                        if (var46 > 0.0D) {
                           var51 = this.field_205474_p;
                        }

                        int var52 = var43 + var11 * 4;
                        int var53 = var32 + var13 * 8;
                        int var54 = var50 + var12 * 4;
                        var3.func_177436_a(var10.func_181079_c(var52, var53, var54), var51, false);
                        var46 += var48;
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

   protected void func_205472_a(IChunk var1, Random var2) {
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();
      int var4 = var1.func_76632_l().func_180334_c();
      int var5 = var1.func_76632_l().func_180333_d();
      Iterator var6 = BlockPos.func_191532_a(var4, 0, var5, var4 + 16, 0, var5 + 16).iterator();

      while(var6.hasNext()) {
         BlockPos var7 = (BlockPos)var6.next();

         int var8;
         for(var8 = 127; var8 > 122; --var8) {
            if (var8 >= 127 - var2.nextInt(5)) {
               var1.func_177436_a(var3.func_181079_c(var7.func_177958_n(), var8, var7.func_177952_p()), Blocks.field_150357_h.func_176223_P(), false);
            }
         }

         for(var8 = 4; var8 >= 0; --var8) {
            if (var8 <= var2.nextInt(5)) {
               var1.func_177436_a(var3.func_181079_c(var7.func_177958_n(), var8, var7.func_177952_p()), Blocks.field_150357_h.func_176223_P(), false);
            }
         }
      }

   }

   public double[] func_205473_a(int var1, int var2) {
      double var3 = 0.03125D;
      return this.field_73177_m.func_202647_a(var1 << 4, var2 << 4, 0, 16, 16, 1, 0.0625D, 0.0625D, 0.0625D);
   }

   public void func_202088_a(IChunk var1) {
      ChunkPos var2 = var1.func_76632_l();
      int var3 = var2.field_77276_a;
      int var4 = var2.field_77275_b;
      SharedSeedRandom var5 = new SharedSeedRandom();
      var5.func_202422_a(var3, var4);
      Biome[] var6 = this.field_202097_c.func_201539_b(var3 * 16, var4 * 16, 16, 16);
      var1.func_201577_a(var6);
      this.func_185936_a(var3, var4, var1);
      this.func_205471_a(var1, var6, var5, this.field_202095_a.func_181545_F());
      this.func_205472_a(var1, var5);
      var1.func_201588_a(Heightmap.Type.WORLD_SURFACE_WG, Heightmap.Type.OCEAN_FLOOR_WG);
      var1.func_201574_a(ChunkStatus.BASE);
   }

   private double[] func_202104_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      double[] var7 = new double[var4 * var5 * var6];
      double var8 = 684.412D;
      double var10 = 2053.236D;
      this.field_185946_g.func_202647_a(var1, var2, var3, var4, 1, var6, 1.0D, 0.0D, 1.0D);
      this.field_185947_h.func_202647_a(var1, var2, var3, var4, 1, var6, 100.0D, 0.0D, 100.0D);
      double[] var14 = this.field_185959_w.func_202647_a(var1, var2, var3, var4, var5, var6, 8.555150000000001D, 34.2206D, 8.555150000000001D);
      double[] var15 = this.field_185957_u.func_202647_a(var1, var2, var3, var4, var5, var6, 684.412D, 2053.236D, 684.412D);
      double[] var16 = this.field_185958_v.func_202647_a(var1, var2, var3, var4, var5, var6, 684.412D, 2053.236D, 684.412D);
      double[] var17 = new double[var5];

      int var18;
      for(var18 = 0; var18 < var5; ++var18) {
         var17[var18] = Math.cos((double)var18 * 3.141592653589793D * 6.0D / (double)var5) * 2.0D;
         double var19 = (double)var18;
         if (var18 > var5 / 2) {
            var19 = (double)(var5 - 1 - var18);
         }

         if (var19 < 4.0D) {
            var19 = 4.0D - var19;
            var17[var18] -= var19 * var19 * var19 * 10.0D;
         }
      }

      var18 = 0;

      for(int var36 = 0; var36 < var4; ++var36) {
         for(int var20 = 0; var20 < var6; ++var20) {
            double var21 = 0.0D;

            for(int var23 = 0; var23 < var5; ++var23) {
               double var24 = var17[var23];
               double var26 = var15[var18] / 512.0D;
               double var28 = var16[var18] / 512.0D;
               double var30 = (var14[var18] / 10.0D + 1.0D) / 2.0D;
               double var32;
               if (var30 < 0.0D) {
                  var32 = var26;
               } else if (var30 > 1.0D) {
                  var32 = var28;
               } else {
                  var32 = var26 + (var28 - var26) * var30;
               }

               var32 -= var24;
               double var34;
               if (var23 > var5 - 4) {
                  var34 = (double)((float)(var23 - (var5 - 4)) / 3.0F);
                  var32 = var32 * (1.0D - var34) - 10.0D * var34;
               }

               if ((double)var23 < 0.0D) {
                  var34 = (0.0D - (double)var23) / 4.0D;
                  var34 = MathHelper.func_151237_a(var34, 0.0D, 1.0D);
                  var32 = var32 * (1.0D - var34) - 10.0D * var34;
               }

               var7[var18] = var32;
               ++var18;
            }
         }
      }

      return var7;
   }

   public void func_202093_c(WorldGenRegion var1) {
   }

   public List<Biome.SpawnListEntry> func_177458_a(EnumCreatureType var1, BlockPos var2) {
      if (var1 == EnumCreatureType.MONSTER) {
         if (Feature.field_202337_o.func_202366_b(this.field_202095_a, var2)) {
            return Feature.field_202337_o.func_202279_e();
         }

         if (Feature.field_202337_o.func_175796_a(this.field_202095_a, var2) && this.field_202095_a.func_180495_p(var2.func_177977_b()).func_177230_c() == Blocks.field_196653_dH) {
            return Feature.field_202337_o.func_202279_e();
         }
      }

      Biome var3 = this.field_202095_a.func_180494_b(var2);
      return var3.func_76747_a(var1);
   }

   public int func_203222_a(World var1, boolean var2, boolean var3) {
      return 0;
   }

   public NetherGenSettings func_201496_a_() {
      return this.field_202107_q;
   }

   public int func_205470_d() {
      return this.field_202095_a.func_181545_F() + 1;
   }

   public int func_207511_e() {
      return 128;
   }

   // $FF: synthetic method
   public IChunkGenSettings func_201496_a_() {
      return this.func_201496_a_();
   }

   static {
      field_185940_a = Blocks.field_150350_a.func_176223_P();
      field_185941_b = Blocks.field_150424_aL.func_176223_P();
      field_185943_d = Blocks.field_150353_l.func_176223_P();
   }
}
