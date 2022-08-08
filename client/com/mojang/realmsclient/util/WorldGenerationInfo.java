package com.mojang.realmsclient.util;

public class WorldGenerationInfo {
   private final String seed;
   private final LevelType levelType;
   private final boolean generateStructures;

   public WorldGenerationInfo(String var1, LevelType var2, boolean var3) {
      super();
      this.seed = var1;
      this.levelType = var2;
      this.generateStructures = var3;
   }

   public String getSeed() {
      return this.seed;
   }

   public LevelType getLevelType() {
      return this.levelType;
   }

   public boolean shouldGenerateStructures() {
      return this.generateStructures;
   }
}
