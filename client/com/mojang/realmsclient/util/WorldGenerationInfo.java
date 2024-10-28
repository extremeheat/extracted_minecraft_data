package com.mojang.realmsclient.util;

import java.util.Set;

public record WorldGenerationInfo(String seed, LevelType levelType, boolean generateStructures, Set<String> experiments) {
   public WorldGenerationInfo(String var1, LevelType var2, boolean var3, Set<String> var4) {
      super();
      this.seed = var1;
      this.levelType = var2;
      this.generateStructures = var3;
      this.experiments = var4;
   }

   public String seed() {
      return this.seed;
   }

   public LevelType levelType() {
      return this.levelType;
   }

   public boolean generateStructures() {
      return this.generateStructures;
   }

   public Set<String> experiments() {
      return this.experiments;
   }
}
