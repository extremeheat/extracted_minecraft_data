package net.minecraft.server.level.progress;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.slf4j.Logger;

public class LoggerChunkProgressListener implements ChunkProgressListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final int maxCount;
   private int count;
   private long startTime;
   private long nextTickTime = 9223372036854775807L;

   private LoggerChunkProgressListener(int var1) {
      super();
      this.maxCount = var1;
   }

   public static LoggerChunkProgressListener createFromGameruleRadius(int var0) {
      return var0 > 0 ? create(var0 + 1) : createCompleted();
   }

   public static LoggerChunkProgressListener create(int var0) {
      int var1 = ChunkProgressListener.calculateDiameter(var0);
      return new LoggerChunkProgressListener(var1 * var1);
   }

   public static LoggerChunkProgressListener createCompleted() {
      return new LoggerChunkProgressListener(0);
   }

   public void updateSpawnPos(ChunkPos var1) {
      this.nextTickTime = Util.getMillis();
      this.startTime = this.nextTickTime;
   }

   public void onStatusChange(ChunkPos var1, @Nullable ChunkStatus var2) {
      if (var2 == ChunkStatus.FULL) {
         ++this.count;
      }

      int var3 = this.getProgress();
      if (Util.getMillis() > this.nextTickTime) {
         this.nextTickTime += 500L;
         LOGGER.info(Component.translatable("menu.preparingSpawn", Mth.clamp(var3, 0, 100)).getString());
      }

   }

   public void start() {
   }

   public void stop() {
      LOGGER.info("Time elapsed: {} ms", Util.getMillis() - this.startTime);
      this.nextTickTime = 9223372036854775807L;
   }

   public int getProgress() {
      return this.maxCount == 0 ? 100 : Mth.floor((float)this.count * 100.0F / (float)this.maxCount);
   }
}
