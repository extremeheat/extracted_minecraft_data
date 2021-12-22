package net.minecraft.server.level.progress;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;

public class StoringChunkProgressListener implements ChunkProgressListener {
   private final LoggerChunkProgressListener delegate;
   private final Long2ObjectOpenHashMap<ChunkStatus> statuses;
   private ChunkPos spawnPos = new ChunkPos(0, 0);
   private final int fullDiameter;
   private final int radius;
   private final int diameter;
   private boolean started;

   public StoringChunkProgressListener(int var1) {
      super();
      this.delegate = new LoggerChunkProgressListener(var1);
      this.fullDiameter = var1 * 2 + 1;
      this.radius = var1 + ChunkStatus.maxDistance();
      this.diameter = this.radius * 2 + 1;
      this.statuses = new Long2ObjectOpenHashMap();
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
      return (ChunkStatus)this.statuses.get(ChunkPos.asLong(var1 + this.spawnPos.field_504 - this.radius, var2 + this.spawnPos.field_505 - this.radius));
   }
}
