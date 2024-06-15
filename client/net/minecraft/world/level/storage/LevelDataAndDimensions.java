package net.minecraft.world.level.storage;

import net.minecraft.world.level.levelgen.WorldDimensions;

public record LevelDataAndDimensions(WorldData worldData, WorldDimensions.Complete dimensions) {
   public LevelDataAndDimensions(WorldData worldData, WorldDimensions.Complete dimensions) {
      super();
      this.worldData = worldData;
      this.dimensions = dimensions;
   }
}
