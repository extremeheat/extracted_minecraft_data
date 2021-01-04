package net.minecraft.world.level.storage;

import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.timers.TimerQueue;

public class DerivedLevelData extends LevelData {
   private final LevelData wrapped;

   public DerivedLevelData(LevelData var1) {
      super();
      this.wrapped = var1;
   }

   public CompoundTag createTag(@Nullable CompoundTag var1) {
      return this.wrapped.createTag(var1);
   }

   public long getSeed() {
      return this.wrapped.getSeed();
   }

   public int getXSpawn() {
      return this.wrapped.getXSpawn();
   }

   public int getYSpawn() {
      return this.wrapped.getYSpawn();
   }

   public int getZSpawn() {
      return this.wrapped.getZSpawn();
   }

   public long getGameTime() {
      return this.wrapped.getGameTime();
   }

   public long getDayTime() {
      return this.wrapped.getDayTime();
   }

   public CompoundTag getLoadedPlayerTag() {
      return this.wrapped.getLoadedPlayerTag();
   }

   public String getLevelName() {
      return this.wrapped.getLevelName();
   }

   public int getVersion() {
      return this.wrapped.getVersion();
   }

   public long getLastPlayed() {
      return this.wrapped.getLastPlayed();
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
      return this.wrapped.getGameType();
   }

   public void setXSpawn(int var1) {
   }

   public void setYSpawn(int var1) {
   }

   public void setZSpawn(int var1) {
   }

   public void setGameTime(long var1) {
   }

   public void setDayTime(long var1) {
   }

   public void setSpawn(BlockPos var1) {
   }

   public void setLevelName(String var1) {
   }

   public void setVersion(int var1) {
   }

   public void setThundering(boolean var1) {
   }

   public void setThunderTime(int var1) {
   }

   public void setRaining(boolean var1) {
   }

   public void setRainTime(int var1) {
   }

   public boolean isGenerateMapFeatures() {
      return this.wrapped.isGenerateMapFeatures();
   }

   public boolean isHardcore() {
      return this.wrapped.isHardcore();
   }

   public LevelType getGeneratorType() {
      return this.wrapped.getGeneratorType();
   }

   public void setGenerator(LevelType var1) {
   }

   public boolean getAllowCommands() {
      return this.wrapped.getAllowCommands();
   }

   public void setAllowCommands(boolean var1) {
   }

   public boolean isInitialized() {
      return this.wrapped.isInitialized();
   }

   public void setInitialized(boolean var1) {
   }

   public GameRules getGameRules() {
      return this.wrapped.getGameRules();
   }

   public Difficulty getDifficulty() {
      return this.wrapped.getDifficulty();
   }

   public void setDifficulty(Difficulty var1) {
   }

   public boolean isDifficultyLocked() {
      return this.wrapped.isDifficultyLocked();
   }

   public void setDifficultyLocked(boolean var1) {
   }

   public TimerQueue<MinecraftServer> getScheduledEvents() {
      return this.wrapped.getScheduledEvents();
   }

   public void setDimensionData(DimensionType var1, CompoundTag var2) {
      this.wrapped.setDimensionData(var1, var2);
   }

   public CompoundTag getDimensionData(DimensionType var1) {
      return this.wrapped.getDimensionData(var1);
   }

   public void fillCrashReportCategory(CrashReportCategory var1) {
      var1.setDetail("Derived", (Object)true);
      this.wrapped.fillCrashReportCategory(var1);
   }
}
