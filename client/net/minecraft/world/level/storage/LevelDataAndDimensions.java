package net.minecraft.world.level.storage;

import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;

public record LevelDataAndDimensions(WorldDataAndGenSettings worldDataAndGenSettings, WorldDimensions.Complete dimensions) {
   public LevelDataAndDimensions {
      super();
   }

   public static LevelDataAndDimensions create(final WorldData data, final WorldGenSettings genSettings, final WorldDimensions.Complete dimensions) {
      return new LevelDataAndDimensions(new WorldDataAndGenSettings(data, genSettings), dimensions);
   }

   public static record WorldDataAndGenSettings(WorldData data, WorldGenSettings genSettings) {
      public WorldDataAndGenSettings {
         super();
      }
   }
}
