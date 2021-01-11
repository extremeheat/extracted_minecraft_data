package net.minecraft.realms;

import net.minecraft.world.storage.SaveFormatComparator;

public class RealmsLevelSummary implements Comparable<RealmsLevelSummary> {
   private SaveFormatComparator levelSummary;

   public RealmsLevelSummary(SaveFormatComparator var1) {
      super();
      this.levelSummary = var1;
   }

   public int getGameMode() {
      return this.levelSummary.func_75790_f().func_77148_a();
   }

   public String getLevelId() {
      return this.levelSummary.func_75786_a();
   }

   public boolean hasCheats() {
      return this.levelSummary.func_75783_h();
   }

   public boolean isHardcore() {
      return this.levelSummary.func_75789_g();
   }

   public boolean isRequiresConversion() {
      return this.levelSummary.func_75785_d();
   }

   public String getLevelName() {
      return this.levelSummary.func_75788_b();
   }

   public long getLastPlayed() {
      return this.levelSummary.func_75784_e();
   }

   public int compareTo(SaveFormatComparator var1) {
      return this.levelSummary.compareTo(var1);
   }

   public long getSizeOnDisk() {
      return this.levelSummary.func_154336_c();
   }

   public int compareTo(RealmsLevelSummary var1) {
      if (this.levelSummary.func_75784_e() < var1.getLastPlayed()) {
         return 1;
      } else {
         return this.levelSummary.func_75784_e() > var1.getLastPlayed() ? -1 : this.levelSummary.func_75786_a().compareTo(var1.getLevelId());
      }
   }
}
