package net.minecraft.world.level.storage;

import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import org.apache.commons.lang3.StringUtils;

public class LevelSummary implements Comparable<LevelSummary> {
   private final LevelSettings settings;
   private final LevelVersion levelVersion;
   private final String levelId;
   private final boolean requiresManualConversion;
   private final boolean locked;
   private final File icon;
   @Nullable
   private Component info;

   public LevelSummary(LevelSettings var1, LevelVersion var2, String var3, boolean var4, boolean var5, File var6) {
      super();
      this.settings = var1;
      this.levelVersion = var2;
      this.levelId = var3;
      this.locked = var5;
      this.icon = var6;
      this.requiresManualConversion = var4;
   }

   public String getLevelId() {
      return this.levelId;
   }

   public String getLevelName() {
      return StringUtils.isEmpty(this.settings.levelName()) ? this.levelId : this.settings.levelName();
   }

   public File getIcon() {
      return this.icon;
   }

   public boolean requiresManualConversion() {
      return this.requiresManualConversion;
   }

   public long getLastPlayed() {
      return this.levelVersion.lastPlayed();
   }

   public int compareTo(LevelSummary var1) {
      if (this.levelVersion.lastPlayed() < var1.levelVersion.lastPlayed()) {
         return 1;
      } else {
         return this.levelVersion.lastPlayed() > var1.levelVersion.lastPlayed() ? -1 : this.levelId.compareTo(var1.levelId);
      }
   }

   public LevelSettings getSettings() {
      return this.settings;
   }

   public GameType getGameMode() {
      return this.settings.gameType();
   }

   public boolean isHardcore() {
      return this.settings.hardcore();
   }

   public boolean hasCheats() {
      return this.settings.allowCommands();
   }

   public MutableComponent getWorldVersionName() {
      return (MutableComponent)(StringUtil.isNullOrEmpty(this.levelVersion.minecraftVersionName()) ? new TranslatableComponent("selectWorld.versionUnknown") : new TextComponent(this.levelVersion.minecraftVersionName()));
   }

   public LevelVersion levelVersion() {
      return this.levelVersion;
   }

   public boolean markVersionInList() {
      return this.askToOpenWorld() || !SharedConstants.getCurrentVersion().isStable() && !this.levelVersion.snapshot() || this.backupStatus().shouldBackup();
   }

   public boolean askToOpenWorld() {
      return this.levelVersion.minecraftVersion().getVersion() > SharedConstants.getCurrentVersion().getDataVersion().getVersion();
   }

   public LevelSummary.BackupStatus backupStatus() {
      WorldVersion var1 = SharedConstants.getCurrentVersion();
      int var2 = var1.getDataVersion().getVersion();
      int var3 = this.levelVersion.minecraftVersion().getVersion();
      if (!var1.isStable() && var3 < var2) {
         return LevelSummary.BackupStatus.UPGRADE_TO_SNAPSHOT;
      } else {
         return var3 > var2 ? LevelSummary.BackupStatus.DOWNGRADE : LevelSummary.BackupStatus.NONE;
      }
   }

   public boolean isLocked() {
      return this.locked;
   }

   public boolean isDisabled() {
      if (!this.isLocked() && !this.requiresManualConversion()) {
         return !this.isCompatible();
      } else {
         return true;
      }
   }

   public boolean isCompatible() {
      return SharedConstants.getCurrentVersion().getDataVersion().isCompatible(this.levelVersion.minecraftVersion());
   }

   public Component getInfo() {
      if (this.info == null) {
         this.info = this.createInfo();
      }

      return this.info;
   }

   private Component createInfo() {
      if (this.isLocked()) {
         return (new TranslatableComponent("selectWorld.locked")).withStyle(ChatFormatting.RED);
      } else if (this.requiresManualConversion()) {
         return (new TranslatableComponent("selectWorld.conversion")).withStyle(ChatFormatting.RED);
      } else if (!this.isCompatible()) {
         return (new TranslatableComponent("selectWorld.incompatible_series")).withStyle(ChatFormatting.RED);
      } else {
         Object var1 = this.isHardcore() ? (new TextComponent("")).append((new TranslatableComponent("gameMode.hardcore")).withStyle(ChatFormatting.DARK_RED)) : new TranslatableComponent("gameMode." + this.getGameMode().getName());
         if (this.hasCheats()) {
            ((MutableComponent)var1).append(", ").append((Component)(new TranslatableComponent("selectWorld.cheats")));
         }

         MutableComponent var2 = this.getWorldVersionName();
         MutableComponent var3 = (new TextComponent(", ")).append(new TranslatableComponent("selectWorld.version")).append(" ");
         if (this.markVersionInList()) {
            var3.append((Component)var2.withStyle(this.askToOpenWorld() ? ChatFormatting.RED : ChatFormatting.ITALIC));
         } else {
            var3.append((Component)var2);
         }

         ((MutableComponent)var1).append((Component)var3);
         return (Component)var1;
      }
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((LevelSummary)var1);
   }

   public static enum BackupStatus {
      NONE(false, false, ""),
      DOWNGRADE(true, true, "downgrade"),
      UPGRADE_TO_SNAPSHOT(true, false, "snapshot");

      private final boolean shouldBackup;
      private final boolean severe;
      private final String translationKey;

      private BackupStatus(boolean var3, boolean var4, String var5) {
         this.shouldBackup = var3;
         this.severe = var4;
         this.translationKey = var5;
      }

      public boolean shouldBackup() {
         return this.shouldBackup;
      }

      public boolean isSevere() {
         return this.severe;
      }

      public String getTranslationKey() {
         return this.translationKey;
      }

      // $FF: synthetic method
      private static LevelSummary.BackupStatus[] $values() {
         return new LevelSummary.BackupStatus[]{NONE, DOWNGRADE, UPGRADE_TO_SNAPSHOT};
      }
   }
}
