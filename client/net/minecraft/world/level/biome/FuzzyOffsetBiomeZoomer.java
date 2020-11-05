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
      int var19 = 0;
      double var20 = 1.0D / 0.0;

      int var22;
      for(var22 = 0; var22 < 8; ++var22) {
         boolean var23 = (var22 & 4) == 0;
         boolean var24 = (var22 & 2) == 0;
         boolean var25 = (var22 & 1) == 0;
         int var26 = var23 ? var10 : var10 + 1;
         int var27 = var24 ? var11 : var11 + 1;
         int var28 = var25 ? var12 : var12 + 1;
         double var29 = var23 ? var13 : var13 - 1.0D;
         double var31 = var24 ? var15 : var15 - 1.0D;
         double var33 = var25 ? var17 : var17 - 1.0D;
         double var35 = getFiddledDistance(var1, var26, var27, var28, var29, var31, var33);
         if (var20 > var35) {
            var19 = var22;
            var20 = var35;
         }
      }

      var22 = (var19 & 4) == 0 ? var10 : var10 + 1;
      int var37 = (var19 & 2) == 0 ? var11 : var11 + 1;
      int var38 = (var19 & 1) == 0 ? var12 : var12 + 1;
      return var6.getNoiseBiome(var22, var37, var38);
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
