package net.minecraft.world.level.storage;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.CrashReportCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.clock.PackedClockStates;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.timers.TimerQueue;

public class DerivedLevelData implements ServerLevelData {
   private final WorldData worldData;
   private final ServerLevelData wrapped;

   public DerivedLevelData(final WorldData worldData, final ServerLevelData wrapped) {
      super();
      this.worldData = worldData;
      this.wrapped = wrapped;
   }

   public LevelData.RespawnData getRespawnData() {
      return this.wrapped.getRespawnData();
   }

   public long getGameTime() {
      return this.wrapped.getGameTime();
   }

   public String getLevelName() {
      return this.worldData.getLevelName();
   }

   public int getClearWeatherTime() {
      return this.wrapped.getClearWeatherTime();
   }

   public void setClearWeatherTime(final int clearWeatherTime) {
   }

   public boolean isThundering() {
      return this.wrapped.isThundering();
   }

   public int getThunderTime() {
      return this.wrapped.getThunderTime();
   }

   public boolean isRaining() {
      return this.wrapped.isRaining();
   }

   public int getRainTime() {
      return this.wrapped.getRainTime();
   }

   public GameType getGameType() {
      return this.worldData.getGameType();
   }

   public void setGameTime(final long time) {
   }

   public void setSpawn(final LevelData.RespawnData respawnData) {
      this.wrapped.setSpawn(respawnData);
   }

   public void setThundering(final boolean thundering) {
   }

   public void setThunderTime(final int thunderTime) {
   }

   public void setRaining(final boolean raining) {
   }

   public void setRainTime(final int rainTime) {
   }

   public PackedClockStates clockStates() {
      return this.wrapped.clockStates();
   }

   public void setClockStates(final PackedClockStates packedClocks) {
   }

   public void setGameType(final GameType gameType) {
   }

   public boolean isHardcore() {
      return this.worldData.isHardcore();
   }

   public boolean isAllowCommands() {
      return this.worldData.isAllowCommands();
   }

   public boolean isInitialized() {
      return this.wrapped.isInitialized();
   }

   public void setInitialized(final boolean initialized) {
   }

   public GameRules getGameRules() {
      return this.worldData.getGameRules();
   }

   public Optional<WorldBorder.Settings> getLegacyWorldBorderSettings() {
      return this.wrapped.getLegacyWorldBorderSettings();
   }

   public void setLegacyWorldBorderSettings(final Optional<WorldBorder.Settings> settings) {
   }

   public Difficulty getDifficulty() {
      return this.worldData.getDifficulty();
   }

   public boolean isDifficultyLocked() {
      return this.worldData.isDifficultyLocked();
   }

   public TimerQueue<MinecraftServer> getScheduledEvents() {
      return this.wrapped.getScheduledEvents();
   }

   public int getWanderingTraderSpawnDelay() {
      return 0;
   }

   public void setWanderingTraderSpawnDelay(final int wanderingTraderSpawnDelay) {
   }

   public int getWanderingTraderSpawnChance() {
      return 0;
   }

   public void setWanderingTraderSpawnChance(final int wanderingTraderSpawnChance) {
   }

   public UUID getWanderingTraderId() {
      return null;
   }

   public void setWanderingTraderId(final UUID wanderingTraderId) {
   }

   public void fillCrashReportCategory(final CrashReportCategory category, final LevelHeightAccessor levelHeightAccessor) {
      category.setDetail("Derived", true);
      this.wrapped.fillCrashReportCategory(category, levelHeightAccessor);
   }
}
