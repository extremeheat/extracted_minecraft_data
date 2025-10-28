package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.util.function.BooleanSupplier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.jspecify.annotations.Nullable;

public abstract class ChunkSource implements LightChunkGetter, AutoCloseable {
   public ChunkSource() {
      super();
   }

   public @Nullable LevelChunk getChunk(int var1, int var2, boolean var3) {
      return (LevelChunk)this.getChunk(var1, var2, ChunkStatus.FULL, var3);
   }

   public @Nullable LevelChunk getChunkNow(int var1, int var2) {
      return this.getChunk(var1, var2, false);
   }

   public @Nullable LightChunk getChunkForLighting(int var1, int var2) {
      return this.getChunk(var1, var2, ChunkStatus.EMPTY, false);
   }

   public boolean hasChunk(int var1, int var2) {
      return this.getChunk(var1, var2, ChunkStatus.FULL, false) != null;
   }

   public abstract @Nullable ChunkAccess getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

   public abstract void tick(BooleanSupplier var1, boolean var2);

   public void onSectionEmptinessChanged(int var1, int var2, int var3, boolean var4) {
   }

   public abstract String gatherStats();

   public abstract int getLoadedChunksCount();

   public void close() throws IOException {
   }

   public abstract LevelLightEngine getLightEngine();

   public void setSpawnSettings(boolean var1) {
   }

   public boolean updateChunkForced(ChunkPos var1, boolean var2) {
      return false;
   }

   public LongSet getForceLoadedChunks() {
      return LongSet.of();
   }
}
