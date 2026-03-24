package net.minecraft.server.level.progress;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class LoggingLevelLoadListener implements LevelLoadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final boolean includePlayerChunks;
   private final LevelLoadProgressTracker progressTracker;
   private boolean closed;
   private long startTime = 9223372036854775807L;
   private long nextLogTime = 9223372036854775807L;

   public LoggingLevelLoadListener(final boolean includePlayerChunks) {
      super();
      this.includePlayerChunks = includePlayerChunks;
      this.progressTracker = new LevelLoadProgressTracker(includePlayerChunks);
   }

   public static LoggingLevelLoadListener forDedicatedServer() {
      return new LoggingLevelLoadListener(false);
   }

   public static LoggingLevelLoadListener forSingleplayer() {
      return new LoggingLevelLoadListener(true);
   }

   public void start(final LevelLoadListener.Stage stage, final int totalChunks) {
      if (!this.closed) {
         if (this.startTime == 9223372036854775807L) {
            long now = Util.getMillis();
            this.startTime = now;
            this.nextLogTime = now;
         }

         this.progressTracker.start(stage, totalChunks);
         switch (stage) {
            case PREPARE_GLOBAL_SPAWN -> LOGGER.info("Selecting global world spawn...");
            case LOAD_INITIAL_CHUNKS -> LOGGER.info("Loading {} persistent chunks...", totalChunks);
            case LOAD_PLAYER_CHUNKS -> LOGGER.info("Loading {} chunks for player spawn...", totalChunks);
         }

      }
   }

   public void update(final LevelLoadListener.Stage stage, final int currentChunks, final int totalChunks) {
      if (!this.closed) {
         this.progressTracker.update(stage, currentChunks, totalChunks);
         if (Util.getMillis() > this.nextLogTime) {
            this.nextLogTime += 500L;
            int percent = Mth.floor(this.progressTracker.get() * 100.0F);
            LOGGER.info(Component.translatable("menu.preparingSpawn", percent).getString());
         }

      }
   }

   public void finish(final LevelLoadListener.Stage stage) {
      if (!this.closed) {
         this.progressTracker.finish(stage);
         LevelLoadListener.Stage finalStage = this.includePlayerChunks ? LevelLoadListener.Stage.LOAD_PLAYER_CHUNKS : LevelLoadListener.Stage.LOAD_INITIAL_CHUNKS;
         if (stage == finalStage) {
            LOGGER.info("Time elapsed: {} ms", Util.getMillis() - this.startTime);
            this.nextLogTime = 9223372036854775807L;
            this.closed = true;
         }

      }
   }

   public void updateFocus(final ResourceKey<Level> dimension, final ChunkPos chunkPos) {
   }
}
