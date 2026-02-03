package net.minecraft.world.level.storage;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.clock.PackedClockStates;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.timers.TimerQueue;
import org.jspecify.annotations.Nullable;

public interface ServerLevelData extends WritableLevelData {
   String getLevelName();

   void setThundering(boolean thundering);

   int getRainTime();

   void setRainTime(int rainTime);

   void setThunderTime(int thunderTime);

   int getThunderTime();

   default void fillCrashReportCategory(final CrashReportCategory category, final LevelHeightAccessor levelHeightAccessor) {
      WritableLevelData.super.fillCrashReportCategory(category, levelHeightAccessor);
      category.setDetail("Level name", this::getLevelName);
      category.setDetail("Level game mode", (CrashReportDetail)(() -> String.format(Locale.ROOT, "Game mode: %s (ID %d). Hardcore: %b. Commands: %b", this.getGameType().getName(), this.getGameType().getId(), this.isHardcore(), this.isAllowCommands())));
      category.setDetail("Level weather", (CrashReportDetail)(() -> String.format(Locale.ROOT, "Rain time: %d (now: %b), thunder time: %d (now: %b)", this.getRainTime(), this.isRaining(), this.getThunderTime(), this.isThundering())));
   }

   int getClearWeatherTime();

   void setClearWeatherTime(int clearWeatherTime);

   int getWanderingTraderSpawnDelay();

   void setWanderingTraderSpawnDelay(int wanderingTraderSpawnDelay);

   int getWanderingTraderSpawnChance();

   void setWanderingTraderSpawnChance(int wanderingTraderSpawnChance);

   @Nullable UUID getWanderingTraderId();

   void setWanderingTraderId(final UUID wanderingTraderId);

   GameType getGameType();

   /** @deprecated */
   @Deprecated
   Optional<WorldBorder.Settings> getLegacyWorldBorderSettings();

   /** @deprecated */
   @Deprecated
   void setLegacyWorldBorderSettings(final Optional<WorldBorder.Settings> settings);

   boolean isInitialized();

   void setInitialized(boolean initialized);

   boolean isAllowCommands();

   void setGameType(GameType gameType);

   TimerQueue<MinecraftServer> getScheduledEvents();

   void setGameTime(final long time);

   void setClockStates(PackedClockStates packedClocks);

   PackedClockStates clockStates();

   GameRules getGameRules();
}
