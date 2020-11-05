package net.minecraft.world.level.biome;

public enum FuzzyOffsetConstantColumnBiomeZoomer implements BiomeZoomer {
   INSTANCE;

   private FuzzyOffsetConstantColumnBiomeZoomer() {
   }

   public Biome getBiome(long var1, int var3, int var4, int var5, BiomeManager.NoiseBiomeSource var6) {
      return FuzzyOffsetBiomeZoomer.INSTANCE.getBiome(var1, var3, 0, var5, var6);
   }
}
