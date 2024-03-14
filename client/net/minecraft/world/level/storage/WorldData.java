package net.minecraft.world.level.storage;

import com.mojang.serialization.Lifecycle;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.WorldOptions;

public interface WorldData {
   int ANVIL_VERSION_ID = 19133;
   int MCREGION_VERSION_ID = 19132;

   WorldDataConfiguration getDataConfiguration();

   void setDataConfiguration(WorldDataConfiguration var1);

   boolean wasModded();

   Set<String> getKnownServerBrands();

   Set<String> getRemovedFeatureFlags();

   void setModdedInfo(String var1, boolean var2);

   default void fillCrashReportCategory(CrashReportCategory var1) {
      var1.setDetail("Known server brands", () -> String.join(", ", this.getKnownServerBrands()));
      var1.setDetail("Removed feature flags", () -> String.join(", ", this.getRemovedFeatureFlags()));
      var1.setDetail("Level was modded", () -> Boolean.toString(this.wasModded()));
      var1.setDetail("Level storage version", () -> {
         int var1xx = this.getVersion();
         return String.format(Locale.ROOT, "0x%05X - %s", var1xx, this.getStorageVersionName(var1xx));
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

   boolean isAllowCommands();

   Difficulty getDifficulty();

   void setDifficulty(Difficulty var1);

   boolean isDifficultyLocked();

   void setDifficultyLocked(boolean var1);

   GameRules getGameRules();

   @Nullable
   CompoundTag getLoadedPlayerTag();

   EndDragonFight.Data endDragonFightData();

   void setEndDragonFightData(EndDragonFight.Data var1);

   WorldOptions worldGenOptions();

   boolean isFlatWorld();

   boolean isDebugWorld();

   Lifecycle worldGenSettingsLifecycle();

   default FeatureFlagSet enabledFeatures() {
      return this.getDataConfiguration().enabledFeatures();
   }
}
