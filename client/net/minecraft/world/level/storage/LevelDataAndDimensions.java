package net.minecraft.world.level.storage;

import net.minecraft.world.level.levelgen.WorldDimensions;

public record LevelDataAndDimensions(WorldData a, WorldDimensions.Complete b) {
   private final WorldData worldData;
   private final WorldDimensions.Complete dimensions;

   public LevelDataAndDimensions(WorldData var1, WorldDimensions.Complete var2) {
      super();
      this.worldData = var1;
      this.dimensions = var2;
   }
}
