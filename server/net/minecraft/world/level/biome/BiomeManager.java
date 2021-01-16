package net.minecraft.world.level.biome;

import com.google.common.hash.Hashing;
import net.minecraft.core.BlockPos;

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

   public interface NoiseBiomeSource {
      Biome getNoiseBiome(int var1, int var2, int var3);
   }
}
