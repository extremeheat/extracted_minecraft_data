package net.minecraft.world.level.storage;

import com.mojang.serialization.Lifecycle;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;

public interface WorldData {
   int ANVIL_VERSION_ID = 19133;
   int MCREGION_VERSION_ID = 19132;

   DataPackConfig getDataPackConfig();

   void setDataPackConfig(DataPackConfig var1);

   boolean wasModded();

   Set<String> getKnownServerBrands();

   void setModdedInfo(String var1, boolean var2);

   default void fillCrashReportCategory(CrashReportCategory var1) {
      var1.setDetail("Known server brands", () -> String.join(", ", this.getKnownServerBrands()));
      var1.setDetail("Level was modded", () -> Boolean.toString(this.wasModded()));
      var1.setDetail("Level storage version", () -> {
         int var1x = this.getVersion();
         return String.format("0x%05X - %s", var1x, this.getStorageVersionName(var1x));
      });
   }

   default String getStorageVersionName(int var1) {
      switch(var1) {
         case 19132:
            return "McRegion";
         case 19133:
            return "Anvil";
         default:
            return "Unknown?";
      }
   }

   @Nullable
   CompoundTag getCustomBossEvents();

   void setCustomBossEvents(@Nullable CompoundTag var1);

   ServerLevelData overworldData();

   LevelSettings getLevelSettings();

   CompoundTag createTag(RegistryAccess var1, @Nullable CompoundTag var2);

   boolean isHardcore();

   int getVersion();

   String getLevelName();

   GameType getGameType();

   void setGameType(GameType var1);

   boolean getAllowCommands();

   Difficulty getDifficulty();

   void setDifficulty(Difficulty var1);

   boolean isDifficultyLocked();

   void setDifficultyLocked(boolean var1);

   GameRules getGameRules();

   @Nullable
   CompoundTag getLoadedPlayerTag();

   CompoundTag endDragonFightData();

   void setEndDragonFightData(CompoundTag var1);

   WorldGenSettings worldGenSettings();

   Lifecycle worldGenSettingsLifecycle();
}
