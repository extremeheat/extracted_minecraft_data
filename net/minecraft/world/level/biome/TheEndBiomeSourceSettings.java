package net.minecraft.world.level.biome;

import net.minecraft.world.level.storage.LevelData;

public class TheEndBiomeSourceSettings implements BiomeSourceSettings {
   private final long seed;

   public TheEndBiomeSourceSettings(LevelData var1) {
      this.seed = var1.getSeed();
   }

   public long getSeed() {
      return this.seed;
   }
}
