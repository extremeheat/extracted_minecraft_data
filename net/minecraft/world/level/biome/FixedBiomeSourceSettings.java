package net.minecraft.world.level.biome;

import net.minecraft.world.level.storage.LevelData;

public class FixedBiomeSourceSettings implements BiomeSourceSettings {
   private Biome biome;

   public FixedBiomeSourceSettings(LevelData var1) {
      this.biome = Biomes.PLAINS;
   }

   public FixedBiomeSourceSettings setBiome(Biome var1) {
      this.biome = var1;
      return this;
   }

   public Biome getBiome() {
      return this.biome;
   }
}
