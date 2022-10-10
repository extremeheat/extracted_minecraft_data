package net.minecraft.world.gen;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;

public class ChunkGeneratorEnd extends AbstractChunkGenerator<EndGenSettings> {
   protected static final IBlockState field_185965_b;
   private final NoiseGeneratorOctaves field_185969_i;
   private final NoiseGeneratorOctaves field_185970_j;
   private final NoiseGeneratorOctaves field_185971_k;
   private final NoiseGeneratorOctaves field_73214_a;
   private final NoiseGeneratorOctaves field_73212_b;
   private final NoiseGeneratorPerlin field_205478_l;
   private final BlockPos field_191061_n;
   private final EndGenSettings field_202116_l;
   private final IBlockState field_205479_o;
   private final IBlockState field_205477_p;

   public ChunkGeneratorEnd(IWorld var1, BiomeProvider var2, EndGenSettings var3) {
      super(var1, var2);
      this.field_202116_l = var3;
      this.field_205479_o = this.field_202116_l.func_205532_l();
      this.field_205477_p = this.field_202116_l.func_205533_m();
      this.field_191061_n = var3.func_205539_n();
      SharedSeedRandom var4 = new SharedSeedRandom(this.field_202096_b);
      this.field_185969_i = new NoiseGeneratorOctaves(var4, 16);
      this.field_185970_j = new NoiseGeneratorOctaves(var4, 16);
      this.field_185971_k = new NoiseGeneratorOctaves(var4, 8);
      this.field_73214_a = new NoiseGeneratorOctaves(var4, 10);
      this.field_73212_b = new NoiseGeneratorOctaves(var4, 16);
      var4.func_202423_a(262);
      this.field_205478_l = new NoiseGeneratorPerlin(new SharedSeedRandom(this.field_202096_b), 4);
   }

   public void func_202114_a(int var1, int var2, IChunk var3) {
      boolean var4 = true;
      boolean var5 = true;
      boolean var6 = true;
      boolean var7 = true;
      double[] var8 = this.func_202113_a(var1 * 2, 0, var2 * 2, 3, 33, 3);
      BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

      for(int var10 = 0; var10 < 2; ++var10) {
         for(int var11 = 0; var11 < 2; ++var11) {
            for(int var12 = 0; var12 < 32; ++var12) {
               double var13 = 0.25D;
               double var15 = var8[((var10 + 0) * 3 + var11 + 0) * 33 + var12 + 0];
               double var17 = var8[((var10 + 0) * 3 + var11 + 1) * 33 + var12 + 0];
               double var19 = var8[((var10 + 1) * 3 + var11 + 0) * 33 + var12 + 0];
               double var21 = var8[((var10 + 1) * 3 + var11 + 1) * 33 + var12 + 0];
               double var23 = (var8[((var10 + 0) * 3 + var11 + 0) * 33 + var12 + 1] - var15) * 0.25D;
               double var25 = (var8[((var10 + 0) * 3 + var11 + 1) * 33 + var12 + 1] - var17) * 0.25D;
               double var27 = (var8[((var10 + 1) * 3 + var11 + 0) * 33 + var12 + 1] - var19) * 0.25D;
               double var29 = (var8[((var10 + 1) * 3 + var11 + 1) * 33 + var12 + 1] - var21) * 0.25D;

               for(int var31 = 0; var31 < 4; ++var31) {
                  double var32 = 0.125D;
                  double var34 = var15;
                  double var36 = var17;
                  double var38 = (var19 - var15) * 0.125D;
                  double var40 = (var21 - var17) * 0.125D;

                  for(int var42 = 0; var42 < 8; ++var42) {
                     double var43 = 0.125D;
                     double var45 = var34;
                     double var47 = (var36 - var34) * 0.125D;

                     for(int var49 = 0; var49 < 8; ++var49) {
                        IBlockState var50 = field_185965_b;
                        if (var45 > 0.0D) {
                           var50 = this.field_205479_o;
                        }

                        int var51 = var42 + var10 * 8;
                        int var52 = var31 + var12 * 4;
                        int var53 = var49 + var11 * 8;
                        var3.func_177436_a(var9.func_181079_c(var51, var52, var53), var50, false);
                        var45 += var47;
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

   public void func_202088_a(IChunk var1) {
      ChunkPos var2 = var1.func_76632_l();
      int var3 = var2.field_77276_a;
      int var4 = var2.field_77275_b;
      SharedSeedRandom var5 = new SharedSeedRandom();
      var5.func_202422_a(var3, var4);
      Biome[] var6 = this.field_202097_c.func_201539_b(var3 * 16, var4 * 16, 16, 16);
      var1.func_201577_a(var6);
      this.func_202114_a(var3, var4, var1);
      this.func_205471_a(var1, var6, var5, 0);
      var1.func_201588_a(Heightmap.Type.WORLD_SURFACE_WG, Heightmap.Type.OCEAN_FLOOR_WG);
      var1.func_201574_a(ChunkStatus.BASE);
   }

   private double[] func_202113_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      double[] var7 = new double[var4 * var5 * var6];
      double var8 = 684.412D;
      double var10 = 684.412D;
      var8 *= 2.0D;
      double[] var12 = this.field_185971_k.func_202647_a(var1, var2, var3, var4, var5, var6, var8 / 80.0D, 4.277575000000001D, var8 / 80.0D);
      double[] var13 = this.field_185969_i.func_202647_a(var1, var2, var3, var4, var5, var6, var8, 684.412D, var8);
      double[] var14 = this.field_185970_j.func_202647_a(var1, var2, var3, var4, var5, var6, var8, 684.412D, var8);
      int var15 = var1 / 2;
      int var16 = var3 / 2;
      int var17 = 0;

      for(int var18 = 0; var18 < var4; ++var18) {
         for(int var19 = 0; var19 < var6; ++var19) {
            float var20 = this.field_202097_c.func_201536_c(var15, var16, var18, var19);

            for(int var21 = 0; var21 < var5; ++var21) {
               double var22 = var13[var17] / 512.0D;
               double var24 = var14[var17] / 512.0D;
               double var28 = (var12[var17] / 10.0D + 1.0D) / 2.0D;
               double var26;
               if (var28 < 0.0D) {
                  var26 = var22;
               } else if (var28 > 1.0D) {
                  var26 = var24;
               } else {
                  var26 = var22 + (var24 - var22) * var28;
               }

               var26 -= 8.0D;
               var26 += (double)var20;
               byte var30 = 2;
               double var31;
               if (var21 > var5 / 2 - var30) {
                  var31 = (double)((float)(var21 - (var5 / 2 - var30)) / 64.0F);
                  var31 = MathHelper.func_151237_a(var31, 0.0D, 1.0D);
                  var26 = var26 * (1.0D - var31) - 3000.0D * var31;
               }

               var30 = 8;
               if (var21 < var30) {
                  var31 = (double)((float)(var30 - var21) / ((float)var30 - 1.0F));
                  var26 = var26 * (1.0D - var31) - 30.0D * var31;
               }

               var7[var17] = var26;
               ++var17;
            }
         }
      }

      return var7;
   }

   public void func_202093_c(WorldGenRegion var1) {
   }

   public List<Biome.SpawnListEntry> func_177458_a(EnumCreatureType var1, BlockPos var2) {
      return this.field_202095_a.func_180494_b(var2).func_76747_a(var1);
   }

   public BlockPos func_202112_d() {
      return this.field_191061_n;
   }

   public int func_203222_a(World var1, boolean var2, boolean var3) {
      return 0;
   }

   public EndGenSettings func_201496_a_() {
      return this.field_202116_l;
   }

   public double[] func_205473_a(int var1, int var2) {
      double var3 = 0.03125D;
      return this.field_205478_l.func_202644_a((double)(var1 << 4), (double)(var2 << 4), 16, 16, 0.0625D, 0.0625D, 1.0D);
   }

   public int func_205470_d() {
      return 50;
   }

   // $FF: synthetic method
   public IChunkGenSettings func_201496_a_() {
      return this.func_201496_a_();
   }

   static {
      field_185965_b = Blocks.field_150350_a.func_176223_P();
   }
}
