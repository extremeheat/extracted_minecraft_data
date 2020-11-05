package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;

public class CaveWorldCarver extends WorldCarver<ProbabilityFeatureConfiguration> {
   public CaveWorldCarver(Codec<ProbabilityFeatureConfiguration> var1, int var2) {
      super(var1, var2);
   }

   public boolean isStartChunk(Random var1, int var2, int var3, ProbabilityFeatureConfiguration var4) {
      return var1.nextFloat() <= var4.probability;
   }

   public boolean carve(ChunkAccess var1, Function<BlockPos, Biome> var2, Random var3, int var4, int var5, int var6, int var7, int var8, BitSet var9, ProbabilityFeatureConfiguration var10) {
      int var11 = (this.getRange() * 2 - 1) * 16;
      int var12 = var3.nextInt(var3.nextInt(var3.nextInt(this.getCaveBound()) + 1) + 1);

      for(int var13 = 0; var13 < var12; ++var13) {
         double var14 = (double)(var5 * 16 + var3.nextInt(16));
         double var16 = (double)this.getCaveY(var3);
         double var18 = (double)(var6 * 16 + var3.nextInt(16));
         int var20 = 1;
         float var23;
         if (var3.nextInt(4) == 0) {
            double var21 = 0.5D;
            var23 = 1.0F + var3.nextFloat() * 6.0F;
            this.genRoom(var1, var2, var3.nextLong(), var4, var7, var8, var14, var16, var18, var23, 0.5D, var9);
            var20 += var3.nextInt(4);
         }

         for(int var27 = 0; var27 < var20; ++var27) {
            float var22 = var3.nextFloat() * 6.2831855F;
            var23 = (var3.nextFloat() - 0.5F) / 4.0F;
            float var24 = this.getThickness(var3);
            int var25 = var11 - var3.nextInt(var11 / 4);
            boolean var26 = false;
            this.genTunnel(var1, var2, var3.nextLong(), var4, var7, var8, var14, var16, var18, var24, var22, var23, 0, var25, this.getYScale(), var9);
         }
      }

      return true;
   }

   protected int getCaveBound() {
      return 15;
   }

   protected float getThickness(Random var1) {
      float var2 = var1.nextFloat() * 2.0F + var1.nextFloat();
      if (var1.nextInt(10) == 0) {
         var2 *= var1.nextFloat() * var1.nextFloat() * 3.0F + 1.0F;
      }

      return var2;
   }

   protected double getYScale() {
      return 1.0D;
   }

   protected int getCaveY(Random var1) {
      return var1.nextInt(var1.nextInt(120) + 8);
   }

   protected void genRoom(ChunkAccess var1, Function<BlockPos, Biome> var2, long var3, int var5, int var6, int var7, double var8, double var10, double var12, float var14, double var15, BitSet var17) {
      double var18 = 1.5D + (double)(Mth.sin(1.5707964F) * var14);
      double var20 = var18 * var15;
      this.carveSphere(var1, var2, var3, var5, var6, var7, var8 + 1.0D, var10, var12, var18, var20, var17);
   }

   protected void genTunnel(ChunkAccess var1, Function<BlockPos, Biome> var2, long var3, int var5, int var6, int var7, double var8, double var10, double var12, float var14, float var15, float var16, int var17, int var18, double var19, BitSet var21) {
      Random var22 = new Random(var3);
      int var23 = var22.nextInt(var18 / 2) + var18 / 4;
      boolean var24 = var22.nextInt(6) == 0;
      float var25 = 0.0F;
      float var26 = 0.0F;

      for(int var27 = var17; var27 < var18; ++var27) {
         double var28 = 1.5D + (double)(Mth.sin(3.1415927F * (float)var27 / (float)var18) * var14);
         double var30 = var28 * var19;
         float var32 = Mth.cos(var16);
         var8 += (double)(Mth.cos(var15) * var32);
         var10 += (double)Mth.sin(var16);
         var12 += (double)(Mth.sin(var15) * var32);
         var16 *= var24 ? 0.92F : 0.7F;
         var16 += var26 * 0.1F;
         var15 += var25 * 0.1F;
         var26 *= 0.9F;
         var25 *= 0.75F;
         var26 += (var22.nextFloat() - var22.nextFloat()) * var22.nextFloat() * 2.0F;
         var25 += (var22.nextFloat() - var22.nextFloat()) * var22.nextFloat() * 4.0F;
         if (var27 == var23 && var14 > 1.0F) {
            this.genTunnel(var1, var2, var22.nextLong(), var5, var6, var7, var8, var10, var12, var22.nextFloat() * 0.5F + 0.5F, var15 - 1.5707964F, var16 / 3.0F, var27, var18, 1.0D, var21);
            this.genTunnel(var1, var2, var22.nextLong(), var5, var6, var7, var8, var10, var12, var22.nextFloat() * 0.5F + 0.5F, var15 + 1.5707964F, var16 / 3.0F, var27, var18, 1.0D, var21);
            return;
         }

         if (var22.nextInt(4) != 0) {
            if (!this.canReach(var6, var7, var8, var12, var27, var18, var14)) {
               return;
            }

            this.carveSphere(var1, var2, var3, var5, var6, var7, var8, var10, var12, var28, var30, var21);
         }
      }

   }

   protected boolean skip(double var1, double var3, double var5, int var7) {
      return var3 <= -0.7D || var1 * var1 + var3 * var3 + var5 * var5 >= 1.0D;
   }
}
