package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;

public class CanyonWorldCarver extends WorldCarver<CanyonCarverConfiguration> {
   public CanyonWorldCarver(Codec<CanyonCarverConfiguration> var1) {
      super(var1);
   }

   public boolean isStartChunk(CanyonCarverConfiguration var1, RandomSource var2) {
      return var2.nextFloat() <= var1.probability;
   }

   public boolean carve(
      CarvingContext var1,
      CanyonCarverConfiguration var2,
      ChunkAccess var3,
      Function<BlockPos, Holder<Biome>> var4,
      RandomSource var5,
      Aquifer var6,
      ChunkPos var7,
      CarvingMask var8
   ) {
      int var9 = (this.getRange() * 2 - 1) * 16;
      double var10 = (double)var7.getBlockX(var5.nextInt(16));
      int var12 = var2.y.sample(var5, var1);
      double var13 = (double)var7.getBlockZ(var5.nextInt(16));
      float var15 = var5.nextFloat() * 6.2831855F;
      float var16 = var2.verticalRotation.sample(var5);
      double var17 = (double)var2.yScale.sample(var5);
      float var19 = var2.shape.thickness.sample(var5);
      int var20 = (int)((float)var9 * var2.shape.distanceFactor.sample(var5));
      boolean var21 = false;
      this.doCarve(var1, var2, var3, var4, var5.nextLong(), var6, var10, (double)var12, var13, var19, var15, var16, 0, var20, var17, var8);
      return true;
   }

   private void doCarve(
      CarvingContext var1,
      CanyonCarverConfiguration var2,
      ChunkAccess var3,
      Function<BlockPos, Holder<Biome>> var4,
      long var5,
      Aquifer var7,
      double var8,
      double var10,
      double var12,
      float var14,
      float var15,
      float var16,
      int var17,
      int var18,
      double var19,
      CarvingMask var21
   ) {
      RandomSource var22 = RandomSource.create(var5);
      float[] var23 = this.initWidthFactors(var1, var2, var22);
      float var24 = 0.0F;
      float var25 = 0.0F;

      for (int var26 = var17; var26 < var18; var26++) {
         double var27 = 1.5 + (double)(Mth.sin((float)var26 * 3.1415927F / (float)var18) * var14);
         double var29 = var27 * var19;
         var27 *= (double)var2.shape.horizontalRadiusFactor.sample(var22);
         var29 = this.updateVerticalRadius(var2, var22, var29, (float)var18, (float)var26);
         float var31 = Mth.cos(var16);
         float var32 = Mth.sin(var16);
         var8 += (double)(Mth.cos(var15) * var31);
         var10 += (double)var32;
         var12 += (double)(Mth.sin(var15) * var31);
         var16 *= 0.7F;
         var16 += var25 * 0.05F;
         var15 += var24 * 0.05F;
         var25 *= 0.8F;
         var24 *= 0.5F;
         var25 += (var22.nextFloat() - var22.nextFloat()) * var22.nextFloat() * 2.0F;
         var24 += (var22.nextFloat() - var22.nextFloat()) * var22.nextFloat() * 4.0F;
         if (var22.nextInt(4) != 0) {
            if (!canReach(var3.getPos(), var8, var12, var26, var18, var14)) {
               return;
            }

            this.carveEllipsoid(
               var1,
               var2,
               var3,
               var4,
               var7,
               var8,
               var10,
               var12,
               var27,
               var29,
               var21,
               (var2x, var3x, var5x, var7x, var9) -> this.shouldSkip(var2x, var23, var3x, var5x, var7x, var9)
            );
         }
      }
   }

   private float[] initWidthFactors(CarvingContext var1, CanyonCarverConfiguration var2, RandomSource var3) {
      int var4 = var1.getGenDepth();
      float[] var5 = new float[var4];
      float var6 = 1.0F;

      for (int var7 = 0; var7 < var4; var7++) {
         if (var7 == 0 || var3.nextInt(var2.shape.widthSmoothness) == 0) {
            var6 = 1.0F + var3.nextFloat() * var3.nextFloat();
         }

         var5[var7] = var6 * var6;
      }

      return var5;
   }

   private double updateVerticalRadius(CanyonCarverConfiguration var1, RandomSource var2, double var3, float var5, float var6) {
      float var7 = 1.0F - Mth.abs(0.5F - var6 / var5) * 2.0F;
      float var8 = var1.shape.verticalRadiusDefaultFactor + var1.shape.verticalRadiusCenterFactor * var7;
      return (double)var8 * var3 * (double)Mth.randomBetween(var2, 0.75F, 1.0F);
   }

   private boolean shouldSkip(CarvingContext var1, float[] var2, double var3, double var5, double var7, int var9) {
      int var10 = var9 - var1.getMinGenY();
      return (var3 * var3 + var7 * var7) * (double)var2[var10 - 1] + var5 * var5 / 6.0 >= 1.0;
   }
}
