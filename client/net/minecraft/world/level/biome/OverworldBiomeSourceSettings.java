package net.minecraft.world.level.biome;

import net.minecraft.world.level.levelgen.OverworldGeneratorSettings;
import net.minecraft.world.level.storage.LevelData;

public class OverworldBiomeSourceSettings implements BiomeSourceSettings {
   private LevelData levelData;
   private OverworldGeneratorSettings generatorSettings;

   public OverworldBiomeSourceSettings() {
      super();
   }

   public OverworldBiomeSourceSettings setLevelData(LevelData var1) {
      this.levelData = var1;
      return this;
   }

   public OverworldBiomeSourceSettings setGeneratorSettings(OverworldGeneratorSettings var1) {
      this.generatorSettings = var1;
      return this;
   }

   public LevelData getLevelData() {
      return this.levelData;
   }

   public OverworldGeneratorSettings getGeneratorSettings() {
      return this.generatorSettings;
   }
}
