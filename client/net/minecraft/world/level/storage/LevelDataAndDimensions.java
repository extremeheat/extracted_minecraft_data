package net.minecraft.world.level.storage;

import net.minecraft.world.level.levelgen.WorldDimensions;

public record LevelDataAndDimensions(WorldData worldData, WorldDimensions.Complete dimensions) {
   public LevelDataAndDimensions(WorldData var1, WorldDimensions.Complete var2) {
      super();
      this.worldData = var1;
      this.dimensions = var2;
   }
}
