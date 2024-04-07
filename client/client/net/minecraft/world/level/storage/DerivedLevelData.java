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

   @Override
   public BlockPos getSpawnPos() {
      return this.wrapped.getSpawnPos();
   }

   @Override
   public float getSpawnAngle() {
      return this.wrapped.getSpawnAngle();
   }

   @Override
   public long getGameTime() {
      return this.wrapped.getGameTime();
   }

   @Override
   public long getDayTime() {
      return this.wrapped.getDayTime();
   }

   @Override
   public String getLevelName() {
      return this.worldData.getLevelName();
   }

   @Override
   public int getClearWeatherTime() {
      return this.wrapped.getClearWeatherTime();
   }

   @Override
   public void setClearWeatherTime(int var1) {
   }

   @Override
   public boolean isThundering() {
      return this.wrapped.isThundering();
   }

   @Override
   public int getThunderTime() {
      return this.wrapped.getThunderTime();
   }

   @Override
   public boolean isRaining() {
      return this.wrapped.isRaining();
   }

   @Override
   public int getRainTime() {
      return this.wrapped.getRainTime();
   }

   @Override
   public GameType getGameType() {
      return this.worldData.getGameType();
   }

   @Override
   public void setGameTime(long var1) {
   }

   @Override
   public void setDayTime(long var1) {
   }

   @Override
   public void setSpawn(BlockPos var1, float var2) {
   }

   @Override
   public void setThundering(boolean var1) {
   }

   @Override
   public void setThunderTime(int var1) {
   }

   @Override
   public void setRaining(boolean var1) {
   }

   @Override
   public void setRainTime(int var1) {
   }

   @Override
   public void setGameType(GameType var1) {
   }

   @Override
   public boolean isHardcore() {
      return this.worldData.isHardcore();
   }

   @Override
   public boolean isAllowCommands() {
      return this.worldData.isAllowCommands();
   }

   @Override
   public boolean isInitialized() {
      return this.wrapped.isInitialized();
   }

   @Override
   public void setInitialized(boolean var1) {
   }

   @Override
   public GameRules getGameRules() {
      return this.worldData.getGameRules();
   }

   @Override
   public WorldBorder.Settings getWorldBorder() {
      return this.wrapped.getWorldBorder();
   }

   @Override
   public void setWorldBorder(WorldBorder.Settings var1) {
   }

   @Override
   public Difficulty getDifficulty() {
      return this.worldData.getDifficulty();
   }

   @Override
   public boolean isDifficultyLocked() {
      return this.worldData.isDifficultyLocked();
   }

   @Override
   public TimerQueue<MinecraftServer> getScheduledEvents() {
      return this.wrapped.getScheduledEvents();
   }

   @Override
   public int getWanderingTraderSpawnDelay() {
      return 0;
   }

   @Override
   public void setWanderingTraderSpawnDelay(int var1) {
   }

   @Override
   public int getWanderingTraderSpawnChance() {
      return 0;
   }

   @Override
   public void setWanderingTraderSpawnChance(int var1) {
   }

   @Override
   public UUID getWanderingTraderId() {
      return null;
   }

   @Override
   public void setWanderingTraderId(UUID var1) {
   }

   @Override
   public void fillCrashReportCategory(CrashReportCategory var1, LevelHeightAccessor var2) {
      var1.setDetail("Derived", true);
      this.wrapped.fillCrashReportCategory(var1, var2);
   }
}
