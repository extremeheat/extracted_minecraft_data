package net.minecraft.server.level.progress;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public class StoringChunkProgressListener implements ChunkProgressListener {
   private final LoggerChunkProgressListener delegate;
   private final Long2ObjectOpenHashMap<ChunkStatus> statuses = new Long2ObjectOpenHashMap();
   private ChunkPos spawnPos = new ChunkPos(0, 0);
   private final int fullDiameter;
   private final int radius;
   private final int diameter;
   private boolean started;

   private StoringChunkProgressListener(LoggerChunkProgressListener var1, int var2, int var3, int var4) {
      super();
      this.delegate = var1;
      this.fullDiameter = var2;
      this.radius = var3;
      this.diameter = var4;
   }

   public static StoringChunkProgressListener createFromGameruleRadius(int var0) {
      return var0 > 0 ? create(var0 + 1) : createCompleted();
   }

   public static StoringChunkProgressListener create(int var0) {
      LoggerChunkProgressListener var1 = LoggerChunkProgressListener.create(var0);
      int var2 = ChunkProgressListener.calculateDiameter(var0);
      int var3 = var0 + ChunkLevel.RADIUS_AROUND_FULL_CHUNK;
      int var4 = ChunkProgressListener.calculateDiameter(var3);
      return new StoringChunkProgressListener(var1, var2, var3, var4);
   }

   public static StoringChunkProgressListener createCompleted() {
      return new StoringChunkProgressListener(LoggerChunkProgressListener.createCompleted(), 0, 0, 0);
   }

   public void updateSpawnPos(ChunkPos var1) {
      if (this.started) {
         this.delegate.updateSpawnPos(var1);
         this.spawnPos = var1;
      }
   }

   public void onStatusChange(ChunkPos var1, @Nullable ChunkStatus var2) {
      if (this.started) {
         this.delegate.onStatusChange(var1, var2);
         if (var2 == null) {
            this.statuses.remove(var1.toLong());
         } else {
            this.statuses.put(var1.toLong(), var2);
         }

      }
   }

   public void start() {
      this.started = true;
      this.statuses.clear();
      this.delegate.start();
   }

   public void stop() {
      this.started = false;
      this.delegate.stop();
   }

   public int getFullDiameter() {
      return this.fullDiameter;
   }

   public int getDiameter() {
      return this.diameter;
   }

   public int getProgress() {
      return this.delegate.getProgress();
   }

   @Nullable
   public ChunkStatus getStatus(int var1, int var2) {
      return (ChunkStatus)this.statuses.get(ChunkPos.asLong(var1 + this.spawnPos.x - this.radius, var2 + this.spawnPos.z - this.radius));
   }
}
