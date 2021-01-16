package net.minecraft.world.level.biome;

public enum NearestNeighborBiomeZoomer implements BiomeZoomer {
   INSTANCE;

   private NearestNeighborBiomeZoomer() {
   }

   public Biome getBiome(long var1, int var3, int var4, int var5, BiomeManager.NoiseBiomeSource var6) {
      return var6.getNoiseBiome(var3 >> 2, var4 >> 2, var5 >> 2);
   }
}
