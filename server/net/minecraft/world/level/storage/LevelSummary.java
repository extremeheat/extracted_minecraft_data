package net.minecraft.world.level.storage;

import java.io.File;
import net.minecraft.world.level.LevelSettings;

public class LevelSummary implements Comparable<LevelSummary> {
   private final LevelSettings settings;
   private final LevelVersion levelVersion;
   private final String levelId;
   private final boolean requiresConversion;
   private final boolean locked;
   private final File icon;

   public LevelSummary(LevelSettings var1, LevelVersion var2, String var3, boolean var4, boolean var5, File var6) {
      super();
      this.settings = var1;
      this.levelVersion = var2;
      this.levelId = var3;
      this.locked = var5;
      this.icon = var6;
      this.requiresConversion = var4;
   }

   public int compareTo(LevelSummary var1) {
      if (this.levelVersion.lastPlayed() < var1.levelVersion.lastPlayed()) {
         return 1;
      } else {
         return this.levelVersion.lastPlayed() > var1.levelVersion.lastPlayed() ? -1 : this.levelId.compareTo(var1.levelId);
      }
   }

   public LevelVersion levelVersion() {
      return this.levelVersion;
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((LevelSummary)var1);
   }
}
