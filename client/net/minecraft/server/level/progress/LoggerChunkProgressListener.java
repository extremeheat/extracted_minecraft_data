package net.minecraft.server.level.progress;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.slf4j.Logger;

public class LoggerChunkProgressListener implements ChunkProgressListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final int maxCount;
   private int count;
   private long startTime;
   private long nextTickTime = 9223372036854775807L;

   public LoggerChunkProgressListener(int var1) {
      super();
      int var2 = var1 * 2 + 1;
      this.maxCount = var2 * var2;
   }

   @Override
   public void updateSpawnPos(ChunkPos var1) {
      this.nextTickTime = Util.getMillis();
      this.startTime = this.nextTickTime;
   }

   @Override
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

   @Override
   public void start() {
   }

   @Override
   public void stop() {
      LOGGER.info("Time elapsed: {} ms", Util.getMillis() - this.startTime);
      this.nextTickTime = 9223372036854775807L;
   }

   public int getProgress() {
      return Mth.floor((float)this.count * 100.0F / (float)this.maxCount);
   }
}
