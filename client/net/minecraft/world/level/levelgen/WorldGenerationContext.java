package net.minecraft.world.level.levelgen;

import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class WorldGenerationContext {
   private final int minY;
   private final int height;

   public WorldGenerationContext(ChunkGenerator var1, LevelHeightAccessor var2) {
      super();
      this.minY = Math.max(var2.getMinBuildHeight(), var1.getMinY());
      this.height = Math.min(var2.getHeight(), var1.getGenDepth());
   }

   public int getMinGenY() {
      return this.minY;
   }

   public int getGenDepth() {
      return this.height;
   }
}
