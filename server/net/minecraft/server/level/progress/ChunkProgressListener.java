package net.minecraft.server.level.progress;

import javax.annotation.Nullable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;

public interface ChunkProgressListener {
   void updateSpawnPos(ChunkPos var1);

   void onStatusChange(ChunkPos var1, @Nullable ChunkStatus var2);

   void stop();
}
