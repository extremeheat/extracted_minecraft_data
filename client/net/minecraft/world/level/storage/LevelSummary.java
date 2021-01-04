package net.minecraft.world.level.storage;

import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelType;

public class LevelSummary implements Comparable<LevelSummary> {
   private final String levelId;
   private final String levelName;
   private final long lastPlayed;
   private final long sizeOnDisk;
   private final boolean requiresConversion;
   private final GameType gameMode;
   private final boolean hardcore;
   private final boolean hasCheats;
   private final String worldVersionName;
   private final int worldVersion;
   private final boolean snapshot;
   private final LevelType generatorType;

   public LevelSummary(LevelData var1, String var2, String var3, long var4, boolean var6) {
      super();
      this.levelId = var2;
      this.levelName = var3;
      this.lastPlayed = var1.getLastPlayed();
      this.sizeOnDisk = var4;
      this.gameMode = var1.getGameType();
      this.requiresConversion = var6;
      this.hardcore = var1.isHardcore();
      this.hasCheats = var1.getAllowCommands();
      this.worldVersionName = var1.getMinecraftVersionName();
      this.worldVersion = var1.getMinecraftVersion();
      this.snapshot = var1.isSnapshot();
      this.generatorType = var1.getGeneratorType();
   }

   public String getLevelId() {
      return this.levelId;
   }

   public String getLevelName() {
      return this.levelName;
   }

   public long getSizeOnDisk() {
      return this.sizeOnDisk;
   }

   public boolean isRequiresConversion() {
      return this.requiresConversion;
   }

   public long getLastPlayed() {
      return this.lastPlayed;
   }

   public int compareTo(LevelSummary var1) {
      if (this.lastPlayed < var1.lastPlayed) {
         return 1;
      } else {
         return this.lastPlayed > var1.lastPlayed ? -1 : this.levelId.compareTo(var1.levelId);
      }
   }

   public GameType getGameMode() {
      return this.gameMode;
   }

   public boolean isHardcore() {
      return this.hardcore;
   }

   public boolean hasCheats() {
      return this.hasCheats;
   }

   public Component getWorldVersionName() {
      return (Component)(StringUtil.isNullOrEmpty(this.worldVersionName) ? new TranslatableComponent("selectWorld.versionUnknown", new Object[0]) : new TextComponent(this.worldVersionName));
   }

   public boolean markVersionInList() {
      return this.askToOpenWorld() || !SharedConstants.getCurrentVersion().isStable() && !this.snapshot || this.shouldBackup() || this.isOldCustomizedWorld();
   }

   public boolean askToOpenWorld() {
      return this.worldVersion > SharedConstants.getCurrentVersion().getWorldVersion();
   }

   public boolean isOldCustomizedWorld() {
      return this.generatorType == LevelType.CUSTOMIZED && this.worldVersion < 1466;
   }

   public boolean shouldBackup() {
      return this.worldVersion < SharedConstants.getCurrentVersion().getWorldVersion();
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((LevelSummary)var1);
   }
}
