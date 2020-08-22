package net.minecraft.world.level.biome;

import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.levelgen.OverworldGeneratorSettings;
import net.minecraft.world.level.storage.LevelData;

public class OverworldBiomeSourceSettings implements BiomeSourceSettings {
   private final long seed;
   private final LevelType generatorType;
   private OverworldGeneratorSettings generatorSettings = new OverworldGeneratorSettings();

   public OverworldBiomeSourceSettings(LevelData var1) {
      this.seed = var1.getSeed();
      this.generatorType = var1.getGeneratorType();
   }

   public OverworldBiomeSourceSettings setGeneratorSettings(OverworldGeneratorSettings var1) {
      this.generatorSettings = var1;
      return this;
   }

   public long getSeed() {
      return this.seed;
   }

   public LevelType getGeneratorType() {
      return this.generatorType;
   }

   public OverworldGeneratorSettings getGeneratorSettings() {
      return this.generatorSettings;
   }
}
