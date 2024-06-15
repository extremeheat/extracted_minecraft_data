package net.minecraft.server.level.progress;

import javax.annotation.Nullable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public interface ChunkProgressListener {
   void updateSpawnPos(ChunkPos var1);

   void onStatusChange(ChunkPos var1, @Nullable ChunkStatus var2);

   void start();

   void stop();

   static int calculateDiameter(int var0) {
      return 2 * var0 + 1;
   }
}
