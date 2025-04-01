package net.minecraft.world.level.storage;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.server.TheGame;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.mines.SpecialMine;
import net.minecraft.world.level.mines.WorldEffect;
import net.minecraft.world.level.timers.TimerQueue;

public interface ServerLevelData extends WritableLevelData {
   String getLevelName();

   void setThundering(boolean var1);

   int getRainTime();

   void setRainTime(int var1);

   void setThunderTime(int var1);

   int getThunderTime();

   default void fillCrashReportCategory(CrashReportCategory var1, LevelHeightAccessor var2) {
      WritableLevelData.super.fillCrashReportCategory(var1, var2);
      var1.setDetail("Level name", this::getLevelName);
      var1.setDetail("Level game mode", (CrashReportDetail)(() -> String.format(Locale.ROOT, "Game mode: %s (ID %d). Hardcore: %b. Commands: %b", this.getGameType().getName(), this.getGameType().getId(), this.isHardcore(), this.isAllowCommands())));
      var1.setDetail("Level weather", (CrashReportDetail)(() -> String.format(Locale.ROOT, "Rain time: %d (now: %b), thunder time: %d (now: %b)", this.getRainTime(), this.isRaining(), this.getThunderTime(), this.isThundering())));
   }

   int getClearWeatherTime();

   void setClearWeatherTime(int var1);

   int getWanderingTraderSpawnDelay();

   void setWanderingTraderSpawnDelay(int var1);

   int getWanderingTraderSpawnChance();

   void setWanderingTraderSpawnChance(int var1);

   @Nullable
   UUID getWanderingTraderId();

   void setWanderingTraderId(UUID var1);

   GameType getGameType();

   void setWorldBorder(WorldBorder.Settings var1);

   WorldBorder.Settings getWorldBorder();

   boolean isInitialized();

   void setInitialized(boolean var1);

   boolean isAllowCommands();

   void setGameType(GameType var1);

   void unlockEffect(WorldEffect var1);

   boolean isEffectUnlocked(WorldEffect var1);

   boolean isSpecialMineUnlocked(SpecialMine var1);

   void unlockSpecialMine(SpecialMine var1);

   void mineCompleted(Optional<SpecialMine> var1, boolean var2);

   Optional<SpecialMine> getNextSpecialMine(RandomSource var1);

   int getMineCrafterLevel();

   int getMineCrafterExp();

   void addExperienceToMineCrafter(int var1);

   TimerQueue<TheGame> getScheduledEvents();

   void setGameTime(long var1);

   void setDayTime(long var1);

   GameRules getGameRules();

   int getLevelCount();

   int incrementAndGetLevelCount();
}
