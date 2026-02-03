package net.minecraft.world.level.storage;

import com.mojang.serialization.Lifecycle;
import java.util.Locale;
import java.util.Set;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.WorldOptions;
import org.jspecify.annotations.Nullable;

public interface WorldData {
   int ANVIL_VERSION_ID = 19133;
   int MCREGION_VERSION_ID = 19132;

   WorldDataConfiguration getDataConfiguration();

   void setDataConfiguration(final WorldDataConfiguration dataConfiguration);

   boolean wasModded();

   Set<String> getKnownServerBrands();

   Set<String> getRemovedFeatureFlags();

   void setModdedInfo(final String serverBrand, final boolean isModded);

   default void fillCrashReportCategory(final CrashReportCategory category) {
      category.setDetail("Known server brands", (CrashReportDetail)(() -> String.join(", ", this.getKnownServerBrands())));
      category.setDetail("Removed feature flags", (CrashReportDetail)(() -> String.join(", ", this.getRemovedFeatureFlags())));
      category.setDetail("Level was modded", (CrashReportDetail)(() -> Boolean.toString(this.wasModded())));
      category.setDetail("Level storage version", (CrashReportDetail)(() -> {
         int version = this.getVersion();
         return String.format(Locale.ROOT, "0x%05X - %s", version, this.getStorageVersionName(version));
      }));
   }

   default String getStorageVersionName(final int version) {
      switch (version) {
         case 19132 -> {
            return "McRegion";
         }
         case 19133 -> {
            return "Anvil";
         }
         default -> {
            return "Unknown?";
         }
      }
   }

   @Nullable CompoundTag getCustomBossEvents();

   void setCustomBossEvents(@Nullable CompoundTag customBossEvents);

   ServerLevelData overworldData();

   LevelSettings getLevelSettings();

   CompoundTag createTag(final RegistryAccess registryAccess, @Nullable CompoundTag playerData);

   boolean isHardcore();

   int getVersion();

   String getLevelName();

   GameType getGameType();

   void setGameType(GameType gameType);

   boolean isAllowCommands();

   Difficulty getDifficulty();

   void setDifficulty(final Difficulty difficulty);

   boolean isDifficultyLocked();

   void setDifficultyLocked(final boolean difficultyLocked);

   GameRules getGameRules();

   @Nullable CompoundTag getLoadedPlayerTag();

   EndDragonFight.Data endDragonFightData();

   void setEndDragonFightData(EndDragonFight.Data data);

   WorldOptions worldGenOptions();

   boolean isFlatWorld();

   boolean isDebugWorld();

   Lifecycle worldGenSettingsLifecycle();

   default FeatureFlagSet enabledFeatures() {
      return this.getDataConfiguration().enabledFeatures();
   }
}
