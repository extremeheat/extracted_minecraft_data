package net.minecraft.world.level.biome;

public class FixedBiomeSourceSettings implements BiomeSourceSettings {
   private Biome biome;

   public FixedBiomeSourceSettings() {
      super();
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
