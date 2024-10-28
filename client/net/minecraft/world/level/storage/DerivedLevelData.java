package net.minecraft.world.level.storage;

import java.util.UUID;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.timers.TimerQueue;

public class DerivedLevelData implements ServerLevelData {
   private final WorldData worldData;
   private final ServerLevelData wrapped;

   public DerivedLevelData(WorldData var1, ServerLevelData var2) {
      super();
      this.worldData = var1;
      this.wrapped = var2;
   }

   public BlockPos getSpawnPos() {
      return this.wrapped.getSpawnPos();
   }

   public float getSpawnAngle() {
      return this.wrapped.getSpawnAngle();
   }

   public long getGameTime() {
      return this.wrapped.getGameTime();
   }

   public long getDayTime() {
      return this.wrapped.getDayTime();
   }

   public String getLevelName() {
      return this.worldData.getLevelName();
   }

   public int getClearWeatherTime() {
      return this.wrapped.getClearWeatherTime();
   }

   public void setClearWeatherTime(int var1) {
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

   public void setGameTime(long var1) {
   }

   public void setDayTime(long var1) {
   }

   public void setSpawn(BlockPos var1, float var2) {
   }

   public void setThundering(boolean var1) {
   }

   public void setThunderTime(int var1) {
   }

   public void setRaining(boolean var1) {
   }

   public void setRainTime(int var1) {
   }

   public void setGameType(GameType var1) {
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

   public void setInitialized(boolean var1) {
   }

   public GameRules getGameRules() {
      return this.worldData.getGameRules();
   }

   public WorldBorder.Settings getWorldBorder() {
      return this.wrapped.getWorldBorder();
   }

   public void setWorldBorder(WorldBorder.Settings var1) {
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

   public void setWanderingTraderSpawnDelay(int var1) {
   }

   public int getWanderingTraderSpawnChance() {
      return 0;
   }

   public void setWanderingTraderSpawnChance(int var1) {
   }

   public UUID getWanderingTraderId() {
      return null;
   }

   public void setWanderingTraderId(UUID var1) {
   }

   public void fillCrashReportCategory(CrashReportCategory var1, LevelHeightAccessor var2) {
      var1.setDetail("Derived", (Object)true);
      this.wrapped.fillCrashReportCategory(var1, var2);
   }
}
