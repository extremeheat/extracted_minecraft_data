package net.minecraft.realms;

import net.minecraft.world.level.storage.LevelSummary;

public class RealmsLevelSummary implements Comparable {
   private final LevelSummary levelSummary;

   public RealmsLevelSummary(LevelSummary var1) {
      this.levelSummary = var1;
   }

   public int getGameMode() {
      return this.levelSummary.getGameMode().getId();
   }

   public String getLevelId() {
      return this.levelSummary.getLevelId();
   }

   public boolean hasCheats() {
      return this.levelSummary.hasCheats();
   }

   public boolean isHardcore() {
      return this.levelSummary.isHardcore();
   }

   public boolean isRequiresConversion() {
      return this.levelSummary.isRequiresConversion();
   }

   public String getLevelName() {
      return this.levelSummary.getLevelName();
   }

   public long getLastPlayed() {
      return this.levelSummary.getLastPlayed();
   }

   public int compareTo(LevelSummary var1) {
      return this.levelSummary.compareTo(var1);
   }

   public long getSizeOnDisk() {
      return this.levelSummary.getSizeOnDisk();
   }

   public int compareTo(RealmsLevelSummary var1) {
      if (this.levelSummary.getLastPlayed() < var1.getLastPlayed()) {
         return 1;
      } else {
         return this.levelSummary.getLastPlayed() > var1.getLastPlayed() ? -1 : this.levelSummary.getLevelId().compareTo(var1.getLevelId());
      }
   }
}
