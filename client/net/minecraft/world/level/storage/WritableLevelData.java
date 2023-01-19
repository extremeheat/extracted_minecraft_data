package net.minecraft.world.level.storage;

import net.minecraft.core.BlockPos;

public interface WritableLevelData extends LevelData {
   void setXSpawn(int var1);

   void setYSpawn(int var1);

   void setZSpawn(int var1);

   void setSpawnAngle(float var1);

   default void setSpawn(BlockPos var1, float var2) {
      this.setXSpawn(var1.getX());
      this.setYSpawn(var1.getY());
      this.setZSpawn(var1.getZ());
      this.setSpawnAngle(var2);
   }
}
