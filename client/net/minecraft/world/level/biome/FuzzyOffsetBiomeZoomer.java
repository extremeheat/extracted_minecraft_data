package net.minecraft.world.level.biome;

import net.minecraft.util.LinearCongruentialGenerator;

public enum FuzzyOffsetBiomeZoomer implements BiomeZoomer {
   INSTANCE;

   private FuzzyOffsetBiomeZoomer() {
   }

   public Biome getBiome(long var1, int var3, int var4, int var5, BiomeManager.NoiseBiomeSource var6) {
      int var7 = var3 - 2;
      int var8 = var4 - 2;
      int var9 = var5 - 2;
      int var10 = var7 >> 2;
      int var11 = var8 >> 2;
      int var12 = var9 >> 2;
      double var13 = (double)(var7 & 3) / 4.0D;
      double var15 = (double)(var8 & 3) / 4.0D;
      double var17 = (double)(var9 & 3) / 4.0D;
      double[] var19 = new double[8];

      int var20;
      int var24;
      int var25;
      for(var20 = 0; var20 < 8; ++var20) {
         boolean var21 = (var20 & 4) == 0;
         boolean var22 = (var20 & 2) == 0;
         boolean var23 = (var20 & 1) == 0;
         var24 = var21 ? var10 : var10 + 1;
         var25 = var22 ? var11 : var11 + 1;
         int var26 = var23 ? var12 : var12 + 1;
         double var27 = var21 ? var13 : var13 - 1.0D;
         double var29 = var22 ? var15 : var15 - 1.0D;
         double var31 = var23 ? var17 : var17 - 1.0D;
         var19[var20] = getFiddledDistance(var1, var24, var25, var26, var27, var29, var31);
      }

      var20 = 0;
      double var33 = var19[0];

      int var34;
      for(var34 = 1; var34 < 8; ++var34) {
         if (var33 > var19[var34]) {
            var20 = var34;
            var33 = var19[var34];
         }
      }

      var34 = (var20 & 4) == 0 ? var10 : var10 + 1;
      var24 = (var20 & 2) == 0 ? var11 : var11 + 1;
      var25 = (var20 & 1) == 0 ? var12 : var12 + 1;
      return var6.getNoiseBiome(var34, var24, var25);
   }

   private static double getFiddledDistance(long var0, int var2, int var3, int var4, double var5, double var7, double var9) {
      long var11 = LinearCongruentialGenerator.next(var0, (long)var2);
      var11 = LinearCongruentialGenerator.next(var11, (long)var3);
      var11 = LinearCongruentialGenerator.next(var11, (long)var4);
      var11 = LinearCongruentialGenerator.next(var11, (long)var2);
      var11 = LinearCongruentialGenerator.next(var11, (long)var3);
      var11 = LinearCongruentialGenerator.next(var11, (long)var4);
      double var13 = getFiddle(var11);
      var11 = LinearCongruentialGenerator.next(var11, var0);
      double var15 = getFiddle(var11);
      var11 = LinearCongruentialGenerator.next(var11, var0);
      double var17 = getFiddle(var11);
      return sqr(var9 + var17) + sqr(var7 + var15) + sqr(var5 + var13);
   }

   private static double getFiddle(long var0) {
      double var2 = (double)((int)Math.floorMod(var0 >> 24, 1024L)) / 1024.0D;
      return (var2 - 0.5D) * 0.9D;
   }

   private static double sqr(double var0) {
      return var0 * var0;
   }
}
