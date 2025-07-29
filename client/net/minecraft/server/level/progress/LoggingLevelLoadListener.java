package net.minecraft.server.level.progress;

import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
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

   public LoggingLevelLoadListener(boolean var1) {
      super();
      this.includePlayerChunks = var1;
      this.progressTracker = new LevelLoadProgressTracker(var1);
   }

   public static LoggingLevelLoadListener forDedicatedServer() {
      return new LoggingLevelLoadListener(false);
   }

   public static LoggingLevelLoadListener forSingleplayer() {
      return new LoggingLevelLoadListener(true);
   }

   public void start(LevelLoadListener.Stage var1, int var2) {
      if (!this.closed) {
         if (this.startTime == 9223372036854775807L) {
            long var3 = Util.getMillis();
            this.startTime = var3;
            this.nextLogTime = var3;
         }

         this.progressTracker.start(var1, var2);
         switch (var1) {
            case PREPARE_GLOBAL_SPAWN -> LOGGER.info("Selecting global world spawn...");
            case LOAD_INITIAL_CHUNKS -> LOGGER.info("Loading {} persistent chunks...", var2);
            case LOAD_PLAYER_CHUNKS -> LOGGER.info("Loading {} chunks for player spawn...", var2);
         }

      }
   }

   public void update(LevelLoadListener.Stage var1, int var2, int var3) {
      if (!this.closed) {
         this.progressTracker.update(var1, var2, var3);
         if (Util.getMillis() > this.nextLogTime) {
            this.nextLogTime += 500L;
            int var4 = Mth.floor(this.progressTracker.get() * 100.0F);
            LOGGER.info(Component.translatable("menu.preparingSpawn", var4).getString());
         }

      }
   }

   public void finish(LevelLoadListener.Stage var1) {
      if (!this.closed) {
         this.progressTracker.finish(var1);
         LevelLoadListener.Stage var2 = this.includePlayerChunks ? LevelLoadListener.Stage.LOAD_PLAYER_CHUNKS : LevelLoadListener.Stage.LOAD_INITIAL_CHUNKS;
         if (var1 == var2) {
            LOGGER.info("Time elapsed: {} ms", Util.getMillis() - this.startTime);
            this.nextLogTime = 9223372036854775807L;
            this.closed = true;
         }

      }
   }

   public void updateFocus(ResourceKey<Level> var1, ChunkPos var2) {
   }
}
