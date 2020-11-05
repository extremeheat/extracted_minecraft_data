package net.minecraft.world.level.biome;

import com.google.common.hash.Hashing;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public class BiomeManager {
   private final BiomeManager.NoiseBiomeSource noiseBiomeSource;
   private final long biomeZoomSeed;
   private final BiomeZoomer zoomer;

   public BiomeManager(BiomeManager.NoiseBiomeSource var1, long var2, BiomeZoomer var4) {
      super();
      this.noiseBiomeSource = var1;
      this.biomeZoomSeed = var2;
      this.zoomer = var4;
   }

   public static long obfuscateSeed(long var0) {
      return Hashing.sha256().hashLong(var0).asLong();
   }

   public BiomeManager withDifferentSource(BiomeSource var1) {
      return new BiomeManager(var1, this.biomeZoomSeed, this.zoomer);
   }

   public Biome getBiome(BlockPos var1) {
      return this.zoomer.getBiome(this.biomeZoomSeed, var1.getX(), var1.getY(), var1.getZ(), this.noiseBiomeSource);
   }

   public Biome getNoiseBiomeAtPosition(double var1, double var3, double var5) {
      int var7 = Mth.floor(var1) >> 2;
      int var8 = Mth.floor(var3) >> 2;
      int var9 = Mth.floor(var5) >> 2;
      return this.getNoiseBiomeAtQuart(var7, var8, var9);
   }

   public Biome getNoiseBiomeAtPosition(BlockPos var1) {
      int var2 = var1.getX() >> 2;
      int var3 = var1.getY() >> 2;
      int var4 = var1.getZ() >> 2;
      return this.getNoiseBiomeAtQuart(var2, var3, var4);
   }

   public Biome getNoiseBiomeAtQuart(int var1, int var2, int var3) {
      return this.noiseBiomeSource.getNoiseBiome(var1, var2, var3);
   }

   public interface NoiseBiomeSource {
      Biome getNoiseBiome(int var1, int var2, int var3);
   }
}
