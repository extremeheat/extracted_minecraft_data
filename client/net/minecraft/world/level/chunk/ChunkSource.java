package net.minecraft.world.level.chunk;

import java.io.IOException;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.lighting.LevelLightEngine;

public abstract class ChunkSource implements LightChunkGetter, AutoCloseable {
   public ChunkSource() {
      super();
   }

   @Nullable
   public LevelChunk getChunk(int var1, int var2, boolean var3) {
      return (LevelChunk)this.getChunk(var1, var2, ChunkStatus.FULL, var3);
   }

   @Nullable
   public LevelChunk getChunkNow(int var1, int var2) {
      return this.getChunk(var1, var2, false);
   }

   @Nullable
   @Override
   public LightChunk getChunkForLighting(int var1, int var2) {
      return this.getChunk(var1, var2, ChunkStatus.EMPTY, false);
   }

   public boolean hasChunk(int var1, int var2) {
      return this.getChunk(var1, var2, ChunkStatus.FULL, false) != null;
   }

   @Nullable
   public abstract ChunkAccess getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

   public abstract void tick(BooleanSupplier var1, boolean var2);

   public void onSectionEmptinessChanged(int var1, int var2, int var3, boolean var4) {
   }

   public abstract String gatherStats();

   public abstract int getLoadedChunksCount();

   @Override
   public void close() throws IOException {
   }

   public abstract LevelLightEngine getLightEngine();

   public void setSpawnSettings(boolean var1) {
   }

   public void updateChunkForced(ChunkPos var1, boolean var2) {
   }
}
