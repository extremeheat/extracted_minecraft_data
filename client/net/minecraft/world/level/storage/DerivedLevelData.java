package net.minecraft.world.level.storage;

import net.minecraft.CrashReportCategory;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelHeightAccessor;

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

   public GameType getGameType() {
      return this.worldData.getGameType();
   }

   public void setGameTime(final long time) {
   }

   public void setSpawn(final LevelData.RespawnData respawnData) {
      this.wrapped.setSpawn(respawnData);
   }

   public void setGameType(final GameType gameType) {
   }

   public boolean isHardcore() {
      return this.worldData.isHardcore();
   }

   public boolean isAllowCommands() {
      return this.worldData.isAllowCommands();
   }

   public void setAllowCommands(final boolean allowCommands) {
   }

   public boolean isInitialized() {
      return this.wrapped.isInitialized();
   }

   public void setInitialized(final boolean initialized) {
   }

   public Difficulty getDifficulty() {
      return this.worldData.getDifficulty();
   }

   public boolean isDifficultyLocked() {
      return this.worldData.isDifficultyLocked();
   }

   public void fillCrashReportCategory(final CrashReportCategory category, final LevelHeightAccessor levelHeightAccessor) {
      category.setDetail("Derived", true);
      this.wrapped.fillCrashReportCategory(category, levelHeightAccessor);
   }
}
