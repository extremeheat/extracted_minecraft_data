package net.minecraft.server.level.progress;

import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerChunkProgressListener implements ChunkProgressListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private final int maxCount;
   private int count;
   private long startTime;
   private long nextTickTime = Long.MAX_VALUE;

   public LoggerChunkProgressListener(int var1) {
      int var2 = var1 * 2 + 1;
      this.maxCount = var2 * var2;
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
         LOGGER.info((new TranslatableComponent("menu.preparingSpawn", new Object[]{Mth.clamp(var3, 0, 100)})).getString());
      }

   }

   public void stop() {
      LOGGER.info("Time elapsed: {} ms", Util.getMillis() - this.startTime);
      this.nextTickTime = Long.MAX_VALUE;
   }

   public int getProgress() {
      return Mth.floor((float)this.count * 100.0F / (float)this.maxCount);
   }
}
