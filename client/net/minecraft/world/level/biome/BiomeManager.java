package net.minecraft.world.level.biome;

import com.google.common.hash.Hashing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.util.LinearCongruentialGenerator;
import net.minecraft.util.Mth;

public class BiomeManager {
   public static final int CHUNK_CENTER_QUART = QuartPos.fromBlock(8);
   private static final int ZOOM_BITS = 2;
   private static final int ZOOM = 4;
   private static final int ZOOM_MASK = 3;
   private final BiomeManager.NoiseBiomeSource noiseBiomeSource;
   private final long biomeZoomSeed;

   public BiomeManager(BiomeManager.NoiseBiomeSource var1, long var2) {
      super();
      this.noiseBiomeSource = var1;
      this.biomeZoomSeed = var2;
   }

   public static long obfuscateSeed(long var0) {
      return Hashing.sha256().hashLong(var0).asLong();
   }

   public BiomeManager withDifferentSource(BiomeManager.NoiseBiomeSource var1) {
      return new BiomeManager(var1, this.biomeZoomSeed);
   }

   public Holder<Biome> getBiome(BlockPos var1) {
      int var2 = var1.getX() - 2;
      int var3 = var1.getY() - 2;
      int var4 = var1.getZ() - 2;
      int var5 = var2 >> 2;
      int var6 = var3 >> 2;
      int var7 = var4 >> 2;
      double var8 = (double)(var2 & 3) / 4.0;
      double var10 = (double)(var3 & 3) / 4.0;
      double var12 = (double)(var4 & 3) / 4.0;
      int var14 = 0;
      double var15 = 1.0 / 0.0;

      for (int var17 = 0; var17 < 8; var17++) {
         boolean var18 = (var17 & 4) == 0;
         boolean var19 = (var17 & 2) == 0;
         boolean var20 = (var17 & 1) == 0;
         int var21 = var18 ? var5 : var5 + 1;
         int var22 = var19 ? var6 : var6 + 1;
         int var23 = var20 ? var7 : var7 + 1;
         double var24 = var18 ? var8 : var8 - 1.0;
         double var26 = var19 ? var10 : var10 - 1.0;
         double var28 = var20 ? var12 : var12 - 1.0;
         double var30 = getFiddledDistance(this.biomeZoomSeed, var21, var22, var23, var24, var26, var28);
         if (var15 > var30) {
            var14 = var17;
            var15 = var30;
         }
      }

      int var32 = (var14 & 4) == 0 ? var5 : var5 + 1;
      int var33 = (var14 & 2) == 0 ? var6 : var6 + 1;
      int var34 = (var14 & 1) == 0 ? var7 : var7 + 1;
      return this.noiseBiomeSource.getNoiseBiome(var32, var33, var34);
   }

   public Holder<Biome> getNoiseBiomeAtPosition(double var1, double var3, double var5) {
      int var7 = QuartPos.fromBlock(Mth.floor(var1));
      int var8 = QuartPos.fromBlock(Mth.floor(var3));
      int var9 = QuartPos.fromBlock(Mth.floor(var5));
      return this.getNoiseBiomeAtQuart(var7, var8, var9);
   }

   public Holder<Biome> getNoiseBiomeAtPosition(BlockPos var1) {
      int var2 = QuartPos.fromBlock(var1.getX());
      int var3 = QuartPos.fromBlock(var1.getY());
      int var4 = QuartPos.fromBlock(var1.getZ());
      return this.getNoiseBiomeAtQuart(var2, var3, var4);
   }

   public Holder<Biome> getNoiseBiomeAtQuart(int var1, int var2, int var3) {
      return this.noiseBiomeSource.getNoiseBiome(var1, var2, var3);
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
      return Mth.square(var9 + var17) + Mth.square(var7 + var15) + Mth.square(var5 + var13);
   }

   private static double getFiddle(long var0) {
      double var2 = (double)Math.floorMod(var0 >> 24, 1024) / 1024.0;
      return (var2 - 0.5) * 0.9;
   }

   public interface NoiseBiomeSource {
      Holder<Biome> getNoiseBiome(int var1, int var2, int var3);
   }
}
