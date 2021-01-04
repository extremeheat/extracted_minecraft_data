package net.minecraft.world.level.levelgen;

import net.minecraft.core.BlockPos;

public class TheEndGeneratorSettings extends ChunkGeneratorSettings {
   private BlockPos spawnPosition;

   public TheEndGeneratorSettings() {
      super();
   }

   public TheEndGeneratorSettings setSpawnPosition(BlockPos var1) {
      this.spawnPosition = var1;
      return this;
   }

   public BlockPos getSpawnPosition() {
      return this.spawnPosition;
   }
}
