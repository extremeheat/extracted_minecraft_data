package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;

public class CaveWorldCarver extends WorldCarver<CaveCarverConfiguration> {
   public CaveWorldCarver(Codec<CaveCarverConfiguration> var1) {
      super(var1);
   }

   public boolean isStartChunk(CaveCarverConfiguration var1, RandomSource var2) {
      return var2.nextFloat() <= var1.probability;
   }

   public boolean carve(
      CarvingContext var1,
      CaveCarverConfiguration var2,
      ChunkAccess var3,
      Function<BlockPos, Holder<Biome>> var4,
      RandomSource var5,
      Aquifer var6,
      ChunkPos var7,
      CarvingMask var8
   ) {
      int var9 = SectionPos.sectionToBlockCoord(this.getRange() * 2 - 1);
      int var10 = var5.nextInt(var5.nextInt(var5.nextInt(this.getCaveBound()) + 1) + 1);

      for (int var11 = 0; var11 < var10; var11++) {
         double var12 = (double)var7.getBlockX(var5.nextInt(16));
         double var14 = (double)var2.y.sample(var5, var1);
         double var16 = (double)var7.getBlockZ(var5.nextInt(16));
         double var18 = (double)var2.horizontalRadiusMultiplier.sample(var5);
         double var20 = (double)var2.verticalRadiusMultiplier.sample(var5);
         double var22 = (double)var2.floorLevel.sample(var5);
         WorldCarver.CarveSkipChecker var24 = (var2x, var3x, var5x, var7x, var9x) -> shouldSkip(var3x, var5x, var7x, var22);
         int var25 = 1;
         if (var5.nextInt(4) == 0) {
            double var26 = (double)var2.yScale.sample(var5);
            float var28 = 1.0F + var5.nextFloat() * 6.0F;
            this.createRoom(var1, var2, var3, var4, var6, var12, var14, var16, var28, var26, var8, var24);
            var25 += var5.nextInt(4);
         }

         for (int var32 = 0; var32 < var25; var32++) {
            float var27 = var5.nextFloat() * 6.2831855F;
            float var33 = (var5.nextFloat() - 0.5F) / 4.0F;
            float var29 = this.getThickness(var5);
            int var30 = var9 - var5.nextInt(var9 / 4);
            boolean var31 = false;
            this.createTunnel(
               var1, var2, var3, var4, var5.nextLong(), var6, var12, var14, var16, var18, var20, var29, var27, var33, 0, var30, this.getYScale(), var8, var24
            );
         }
      }

      return true;
   }

   protected int getCaveBound() {
      return 15;
   }

   protected float getThickness(RandomSource var1) {
      float var2 = var1.nextFloat() * 2.0F + var1.nextFloat();
      if (var1.nextInt(10) == 0) {
         var2 *= var1.nextFloat() * var1.nextFloat() * 3.0F + 1.0F;
      }

      return var2;
   }

   protected double getYScale() {
      return 1.0;
   }

   protected void createRoom(
      CarvingContext var1,
      CaveCarverConfiguration var2,
      ChunkAccess var3,
      Function<BlockPos, Holder<Biome>> var4,
      Aquifer var5,
      double var6,
      double var8,
      double var10,
      float var12,
      double var13,
      CarvingMask var15,
      WorldCarver.CarveSkipChecker var16
   ) {
      double var17 = 1.5 + (double)(Mth.sin(1.5707964F) * var12);
      double var19 = var17 * var13;
      this.carveEllipsoid(var1, var2, var3, var4, var5, var6 + 1.0, var8, var10, var17, var19, var15, var16);
   }

   protected void createTunnel(
      CarvingContext var1,
      CaveCarverConfiguration var2,
      ChunkAccess var3,
      Function<BlockPos, Holder<Biome>> var4,
      long var5,
      Aquifer var7,
      double var8,
      double var10,
      double var12,
      double var14,
      double var16,
      float var18,
      float var19,
      float var20,
      int var21,
      int var22,
      double var23,
      CarvingMask var25,
      WorldCarver.CarveSkipChecker var26
   ) {
      RandomSource var27 = RandomSource.create(var5);
      int var28 = var27.nextInt(var22 / 2) + var22 / 4;
      boolean var29 = var27.nextInt(6) == 0;
      float var30 = 0.0F;
      float var31 = 0.0F;

      for (int var32 = var21; var32 < var22; var32++) {
         double var33 = 1.5 + (double)(Mth.sin(3.1415927F * (float)var32 / (float)var22) * var18);
         double var35 = var33 * var23;
         float var37 = Mth.cos(var20);
         var8 += (double)(Mth.cos(var19) * var37);
         var10 += (double)Mth.sin(var20);
         var12 += (double)(Mth.sin(var19) * var37);
         var20 *= var29 ? 0.92F : 0.7F;
         var20 += var31 * 0.1F;
         var19 += var30 * 0.1F;
         var31 *= 0.9F;
         var30 *= 0.75F;
         var31 += (var27.nextFloat() - var27.nextFloat()) * var27.nextFloat() * 2.0F;
         var30 += (var27.nextFloat() - var27.nextFloat()) * var27.nextFloat() * 4.0F;
         if (var32 == var28 && var18 > 1.0F) {
            this.createTunnel(
               var1,
               var2,
               var3,
               var4,
               var27.nextLong(),
               var7,
               var8,
               var10,
               var12,
               var14,
               var16,
               var27.nextFloat() * 0.5F + 0.5F,
               var19 - 1.5707964F,
               var20 / 3.0F,
               var32,
               var22,
               1.0,
               var25,
               var26
            );
            this.createTunnel(
               var1,
               var2,
               var3,
               var4,
               var27.nextLong(),
               var7,
               var8,
               var10,
               var12,
               var14,
               var16,
               var27.nextFloat() * 0.5F + 0.5F,
               var19 + 1.5707964F,
               var20 / 3.0F,
               var32,
               var22,
               1.0,
               var25,
               var26
            );
            return;
         }

         if (var27.nextInt(4) != 0) {
            if (!canReach(var3.getPos(), var8, var12, var32, var22, var18)) {
               return;
            }

            this.carveEllipsoid(var1, var2, var3, var4, var7, var8, var10, var12, var33 * var14, var35 * var16, var25, var26);
         }
      }
   }

   private static boolean shouldSkip(double var0, double var2, double var4, double var6) {
      return var2 <= var6 ? true : var0 * var0 + var2 * var2 + var4 * var4 >= 1.0;
   }
}
